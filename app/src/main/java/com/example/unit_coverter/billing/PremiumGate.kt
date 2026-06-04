package com.example.unit_coverter.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface PremiumGate {
    /** Emits true when the user has unlocked the premium tier. */
    val isUnlocked: StateFlow<Boolean>

    /** Launch the Play Billing purchase flow attached to [activity]. No-op in the stub. */
    fun launchBillingFlow(activity: Activity)
}
