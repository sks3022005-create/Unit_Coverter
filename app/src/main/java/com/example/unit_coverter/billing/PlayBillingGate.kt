package com.example.unit_coverter.billing

import android.app.Activity
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production Play Billing implementation.
 *
 * Swap into [BillingModule] for release builds by replacing [StubPremiumGate] with this class.
 *
 * Implementation checklist (TODO before release):
 *   1. Build BillingClient with PurchasesUpdatedListener
 *   2. startConnection; cache result in an internal state
 *   3. queryProductDetailsAsync for product ID "premium_unlock" (one-time purchase)
 *   4. launchBillingFlow(activity, BillingFlowParams) in [launchBillingFlow]
 *   5. On BILLING_RESPONSE_RESULT_OK + PURCHASED: verify receipt server-side
 *   6. acknowledgePurchase; set [_isUnlocked] = true
 *   7. Persist unlock state to DataStore so it survives app restarts
 *   8. Handle ITEM_ALREADY_OWNED on startup (restore previous purchase)
 */
@Singleton
class PlayBillingGate @Inject constructor(
    @ApplicationContext private val context: Context,
) : PremiumGate {

    private val _isUnlocked = MutableStateFlow(false)
    override val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    override fun launchBillingFlow(activity: Activity) {
        // TODO(M11-release): connect BillingClient, query ProductDetails for
        //   product "premium_unlock", then call billingClient.launchBillingFlow(activity, params)
    }
}
