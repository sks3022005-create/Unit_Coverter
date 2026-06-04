package com.example.unit_coverter.feature.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unit_coverter.billing.PremiumGate
import com.example.unit_coverter.data.local.prefs.UserPrefsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPrefsDataStore: UserPrefsDataStore,
    private val premiumGate: PremiumGate,
) : ViewModel() {

    data class UiState(
        val isDynamicColorEnabled: Boolean = true,
        val isForceDark: Boolean = false,
        val historyMaxEntries: Int = 500,
        val isUnlocked: Boolean = false,
    )

    val state: StateFlow<UiState> = combine(
        userPrefsDataStore.isDynamicColorEnabled,
        userPrefsDataStore.isDarkThemeForced,
        userPrefsDataStore.historyMaxEntries,
        premiumGate.isUnlocked,
    ) { dynamicColor, forceDark, historyMax, unlocked ->
        UiState(
            isDynamicColorEnabled = dynamicColor,
            isForceDark = forceDark,
            historyMaxEntries = historyMax,
            isUnlocked = unlocked,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { userPrefsDataStore.setDynamicColorEnabled(enabled) }
    }

    fun setForceDark(force: Boolean) {
        viewModelScope.launch { userPrefsDataStore.setForceDarkTheme(force) }
    }

    fun setHistoryMaxEntries(max: Int) {
        viewModelScope.launch { userPrefsDataStore.setHistoryMaxEntries(max) }
    }

    fun launchBillingFlow(activity: Activity) {
        premiumGate.launchBillingFlow(activity)
    }
}
