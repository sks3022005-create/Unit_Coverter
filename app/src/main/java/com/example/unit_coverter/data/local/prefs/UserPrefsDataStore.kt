package com.example.unit_coverter.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPrefsDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val store = context.dataStore

    val defaultCategoryId: Flow<String> =
        store.data.map { it[Keys.DEFAULT_CATEGORY] ?: "length" }

    val lastFromUnitId: Flow<String?> =
        store.data.map { it[Keys.LAST_FROM_UNIT] }

    val lastToUnitId: Flow<String?> =
        store.data.map { it[Keys.LAST_TO_UNIT] }

    // Default OFF so the branded palette is what users see out of the box; they can
    // opt into Material You (wallpaper-based dynamic color) from Settings.
    val isDynamicColorEnabled: Flow<Boolean> =
        store.data.map { it[Keys.DYNAMIC_COLOR] ?: false }

    val isDarkThemeForced: Flow<Boolean> =
        store.data.map { it[Keys.FORCE_DARK] ?: false }

    val historyMaxEntries: Flow<Int> =
        store.data.map { (it[Keys.HISTORY_MAX] ?: "500").toIntOrNull() ?: 500 }

    suspend fun setDefaultCategoryId(id: String) {
        store.edit { it[Keys.DEFAULT_CATEGORY] = id }
    }

    /** Remember the last Convert pair so the next launch opens on it. */
    suspend fun setLastConversion(categoryId: String, fromUnitId: String, toUnitId: String) {
        store.edit {
            it[Keys.DEFAULT_CATEGORY] = categoryId
            it[Keys.LAST_FROM_UNIT] = fromUnitId
            it[Keys.LAST_TO_UNIT] = toUnitId
        }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        store.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    suspend fun setForceDarkTheme(force: Boolean) {
        store.edit { it[Keys.FORCE_DARK] = force }
    }

    suspend fun setHistoryMaxEntries(max: Int) {
        store.edit { it[Keys.HISTORY_MAX] = max.toString() }
    }

    private object Keys {
        val DEFAULT_CATEGORY = stringPreferencesKey("default_category")
        val LAST_FROM_UNIT = stringPreferencesKey("last_from_unit")
        val LAST_TO_UNIT = stringPreferencesKey("last_to_unit")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FORCE_DARK = booleanPreferencesKey("force_dark")
        val HISTORY_MAX = stringPreferencesKey("history_max")
    }
}
