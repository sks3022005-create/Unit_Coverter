package com.example.unit_coverter.data.currency

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

private val Context.currencyStore: DataStore<Preferences> by
    preferencesDataStore(name = "currency_rates")

/**
 * Fetches and caches foreign-exchange rates.
 *
 * Design constraints:
 *  - **No API key.** Both endpoints below are free and unauthenticated, so there
 *    is no secret to leak and nothing for the user to sign up for.
 *  - **Offline-first.** Rates are cached to DataStore as raw JSON. The app is
 *    fully usable on a plane; it just shows how stale the rates are.
 *  - **No new dependencies.** Uses HttpURLConnection rather than pulling in
 *    Retrofit/OkHttp for two GET requests.
 */
@Singleton
class CurrencyRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val store = context.currencyStore
    private val json = Json { ignoreUnknownKeys = true }
    private val ratesSerializer = MapSerializer(String.serializer(), Double.serializer())

    /** Rates are quoted against USD; every pair is derived from this base. */
    private val primaryUrl = "https://open.er-api.com/v6/latest/USD"

    /** Independent provider used when the primary is unreachable. */
    private val fallbackUrl =
        "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.min.json"

    /** Cached USD-based rates, or null when nothing has been fetched yet. */
    val cachedRates: Flow<CurrencyRates?> = store.data.map { prefs ->
        val raw = prefs[Keys.RATES_JSON] ?: return@map null
        val fetchedAt = prefs[Keys.FETCHED_AT] ?: 0L
        runCatching {
            val map: Map<String, Double> = json.decodeFromString(ratesSerializer, raw)
            CurrencyRates(usdRates = map, fetchedAtMillis = fetchedAt)
        }.getOrNull()
    }

    /**
     * Fetches fresh rates and caches them.
     *
     * Returns the new rates on success. On failure the cached rates are returned
     * when present, so a transient network error never blanks the screen; only a
     * failure with no cache at all surfaces as an error.
     */
    suspend fun refresh(): Result<CurrencyRates> = withContext(Dispatchers.IO) {
        val fetched = fetchFrom(primaryUrl, ::parsePrimary)
            ?: fetchFrom(fallbackUrl, ::parseFallback)

        if (fetched != null && fetched.isNotEmpty()) {
            val now = System.currentTimeMillis()
            store.edit { prefs ->
                prefs[Keys.RATES_JSON] = json.encodeToString(ratesSerializer, fetched)
                prefs[Keys.FETCHED_AT] = now
            }
            return@withContext Result.success(
                CurrencyRates(usdRates = fetched, fetchedAtMillis = now),
            )
        }

        val cached = cachedRates.first()
        if (cached != null) {
            Result.success(cached)
        } else {
            Result.failure(IOException("Couldn't reach the rate service. Check your connection."))
        }
    }

    private fun fetchFrom(
        url: String,
        parse: (String) -> Map<String, Double>,
    ): Map<String, Double>? = runCatching {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/json")
        }
        try {
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@runCatching null
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            parse(body).takeIf { it.isNotEmpty() }
        } finally {
            connection.disconnect()
        }
    }.getOrNull()

    private fun parsePrimary(body: String): Map<String, Double> {
        val parsed = json.decodeFromString<ExchangeRateResponse>(body)
        return if (parsed.isSuccess) parsed.rates else emptyMap()
    }

    /** The fallback nests rates under a lowercase "usd" key and lowercases codes. */
    private fun parseFallback(body: String): Map<String, Double> {
        val root = json.parseToJsonElement(body).jsonObject
        val usd = root["usd"]?.jsonObject ?: return emptyMap()
        return usd.entries.mapNotNull { (code, value) ->
            val rate = runCatching { value.jsonPrimitive.content.toDouble() }.getOrNull()
            if (rate == null) null else code.uppercase() to rate
        }.toMap()
    }

    private object Keys {
        val RATES_JSON = stringPreferencesKey("rates_json")
        val FETCHED_AT = longPreferencesKey("fetched_at")
    }
}

/**
 * A snapshot of USD-based rates.
 *
 * Cross rates are derived as `amount / rate(from) * rate(to)`, which is exact
 * enough for currency display and avoids needing one request per base.
 */
data class CurrencyRates(
    val usdRates: Map<String, Double>,
    val fetchedAtMillis: Long,
) {
    val availableCodes: List<String> get() = usdRates.keys.sorted()

    /** Converts [amount] from [from] to [to], or null if either code is unknown. */
    fun convert(amount: Double, from: String, to: String): Double? {
        if (from == to) return amount
        val fromRate = usdRates[from] ?: return null
        val toRate = usdRates[to] ?: return null
        if (fromRate == 0.0) return null
        return amount / fromRate * toRate
    }

    /** Single-unit rate, used for the "1 USD = 94.54 INR" caption. */
    fun unitRate(from: String, to: String): Double? = convert(1.0, from, to)
}
