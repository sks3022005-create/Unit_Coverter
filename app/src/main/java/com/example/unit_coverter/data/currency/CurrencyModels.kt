package com.example.unit_coverter.data.currency

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response shape of https://open.er-api.com/v6/latest/USD
 *
 * The endpoint is free and requires no API key or registration. Unknown fields
 * are ignored so a provider-side addition can't break parsing.
 */
@Serializable
data class ExchangeRateResponse(
    val result: String = "",
    @SerialName("base_code") val baseCode: String = "",
    @SerialName("time_last_update_unix") val timeLastUpdateUnix: Long = 0L,
    @SerialName("time_next_update_unix") val timeNextUpdateUnix: Long = 0L,
    val rates: Map<String, Double> = emptyMap(),
) {
    val isSuccess: Boolean get() = result == "success" && rates.isNotEmpty()
}

/** A currency the user can pick, with a display name and symbol. */
data class CurrencyInfo(
    val code: String,
    val displayName: String,
    val symbol: String,
)

/**
 * Currencies surfaced in the picker, in display order.
 *
 * The API returns ~160 codes including ones most people will never need; this
 * curated list front-loads the widely used ones and gives each a proper name and
 * symbol. Codes returned by the API but missing here still convert correctly —
 * they simply appear with the raw code as their name.
 */
val CURRENCIES: List<CurrencyInfo> = listOf(
    CurrencyInfo("USD", "US Dollar", "$"),
    CurrencyInfo("EUR", "Euro", "€"),
    CurrencyInfo("INR", "Indian Rupee", "₹"),
    CurrencyInfo("GBP", "British Pound", "£"),
    CurrencyInfo("JPY", "Japanese Yen", "¥"),
    CurrencyInfo("AUD", "Australian Dollar", "A$"),
    CurrencyInfo("CAD", "Canadian Dollar", "C$"),
    CurrencyInfo("CHF", "Swiss Franc", "Fr"),
    CurrencyInfo("CNY", "Chinese Yuan", "¥"),
    CurrencyInfo("HKD", "Hong Kong Dollar", "HK$"),
    CurrencyInfo("SGD", "Singapore Dollar", "S$"),
    CurrencyInfo("AED", "UAE Dirham", "د.إ"),
    CurrencyInfo("SAR", "Saudi Riyal", "﷼"),
    CurrencyInfo("NZD", "New Zealand Dollar", "NZ$"),
    CurrencyInfo("ZAR", "South African Rand", "R"),
    CurrencyInfo("BRL", "Brazilian Real", "R$"),
    CurrencyInfo("MXN", "Mexican Peso", "MX$"),
    CurrencyInfo("RUB", "Russian Ruble", "₽"),
    CurrencyInfo("KRW", "South Korean Won", "₩"),
    CurrencyInfo("TRY", "Turkish Lira", "₺"),
    CurrencyInfo("SEK", "Swedish Krona", "kr"),
    CurrencyInfo("NOK", "Norwegian Krone", "kr"),
    CurrencyInfo("DKK", "Danish Krone", "kr"),
    CurrencyInfo("PLN", "Polish Zloty", "zł"),
    CurrencyInfo("THB", "Thai Baht", "฿"),
    CurrencyInfo("IDR", "Indonesian Rupiah", "Rp"),
    CurrencyInfo("MYR", "Malaysian Ringgit", "RM"),
    CurrencyInfo("PHP", "Philippine Peso", "₱"),
    CurrencyInfo("VND", "Vietnamese Dong", "₫"),
    CurrencyInfo("BDT", "Bangladeshi Taka", "৳"),
    CurrencyInfo("PKR", "Pakistani Rupee", "₨"),
    CurrencyInfo("LKR", "Sri Lankan Rupee", "Rs"),
    CurrencyInfo("NPR", "Nepalese Rupee", "Rs"),
    CurrencyInfo("EGP", "Egyptian Pound", "E£"),
    CurrencyInfo("NGN", "Nigerian Naira", "₦"),
    CurrencyInfo("KES", "Kenyan Shilling", "KSh"),
    CurrencyInfo("ILS", "Israeli Shekel", "₪"),
    CurrencyInfo("CZK", "Czech Koruna", "Kč"),
    CurrencyInfo("HUF", "Hungarian Forint", "Ft"),
    CurrencyInfo("RON", "Romanian Leu", "lei"),
    CurrencyInfo("ARS", "Argentine Peso", "AR$"),
    CurrencyInfo("CLP", "Chilean Peso", "CL$"),
    CurrencyInfo("COP", "Colombian Peso", "CO$"),
    CurrencyInfo("TWD", "Taiwan Dollar", "NT$"),
    CurrencyInfo("QAR", "Qatari Riyal", "﷼"),
    CurrencyInfo("KWD", "Kuwaiti Dinar", "د.ك"),
    CurrencyInfo("BHD", "Bahraini Dinar", ".د.ب"),
    CurrencyInfo("OMR", "Omani Rial", "﷼"),
)

private val CURRENCY_BY_CODE = CURRENCIES.associateBy { it.code }

/** Metadata for [code], or a generic entry when the code isn't curated. */
fun currencyInfo(code: String): CurrencyInfo =
    CURRENCY_BY_CODE[code] ?: CurrencyInfo(code, code, code)
