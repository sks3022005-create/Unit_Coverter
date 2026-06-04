package com.example.unit_coverter.billing

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class StubPremiumGate @Inject constructor() : PremiumGate {
    override val isUnlocked: StateFlow<Boolean> = MutableStateFlow(true)
    override fun launchBillingFlow(activity: Activity) = Unit
}
