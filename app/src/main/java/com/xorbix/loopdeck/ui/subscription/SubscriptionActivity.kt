package com.xorbix.loopdeck.ui.subscription

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.ui.subscription.SubscriptionActivity.GameSku.CONSUMABLE_SKUS
import com.xorbix.loopdeck.ui.subscription.SubscriptionActivity.GameSku.INAPP_SKUS
import com.xorbix.loopdeck.ui.subscription.SubscriptionActivity.GameSku.SUBS_SKUS
import kotlinx.android.synthetic.main.activity_subscription.*
import java.util.ArrayList

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class SubscriptionActivity : AppCompatActivity(), PurchasesUpdatedListener,
    BillingClientStateListener {


    private lateinit var billingClient: BillingClient
    private val skusWithSkuDetails = mutableMapOf<String, SkuDetails>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        buy.setOnClickListener {
            startBillingServiceConnection()
        }

    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.apply { processPurchases(this.toSet()) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // call queryPurchases to verify and process all owned items
                queryPurchases()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connectToBillingService()
            }
            else -> {
                Log.e("BillingClient", "Failed to onPurchasesUpdated")
            }
        }
    }

    override fun onBillingServiceDisconnected() {
        connectToBillingService()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. Retrieve in-app products and subscriptions details
            querySkuDetailsAsync(BillingClient.SkuType.INAPP, INAPP_SKUS)
            querySkuDetailsAsync(BillingClient.SkuType.SUBS, SUBS_SKUS)

            // Refresh your application access based on the billing cache
            queryPurchases()
        }
    }

    private fun startBillingServiceConnection() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this).build()

        connectToBillingService()
    }

    private fun connectToBillingService() {
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    private object GameSku {
        const val WEEKLY = "weekly"
        const val ANNUAL = "annual"
        const val COIN = "coin"
        const val RACE_TRACK = "race_trake"

        val INAPP_SKUS = listOf(COIN, RACE_TRACK)
        val SUBS_SKUS = listOf(WEEKLY, ANNUAL)
        val CONSUMABLE_SKUS = listOf(COIN)
    }

    private fun querySkuDetailsAsync(
        @BillingClient.SkuType skuType: String,
        skuList: List<String>
    ) {
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(skuList)
            .setType(skuType)
            .build()

        billingClient.querySkuDetailsAsync(
            params
        ) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (details in skuDetailsList) {
                    skusWithSkuDetails[details.sku] = details
                }
            }
        }
    }

    private fun queryPurchases() {
        val purchasesResult = HashSet<Purchase>()
        var result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        result.purchasesList?.apply { purchasesResult.addAll(this) }

        result = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        result.purchasesList?.apply { purchasesResult.addAll(this) }

        processPurchases(purchasesResult)
    }

    private fun purchase(skuDetails: SkuDetails) {
        val params = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()

        billingClient.launchBillingFlow(this, params)
            .takeIf { billingResult -> billingResult.responseCode != BillingClient.BillingResponseCode.OK }
            ?.let { billingResult ->
                Log.e("BillingClient", "Failed to launch billing flow $billingResult")
            }
    }

    private fun processPurchases(purchases: Set<Purchase>) {
        purchases.forEach { purchase ->
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                // Implement server verification
                // If purchase token is OK, then unlock user access to the content
                acknowledgePurchase(purchase)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val skuDetails = skusWithSkuDetails[purchase.skus] ?: run {
            Log.e("BillingClient", "Could not find SkuDetails to acknowledge purchase")
            return
        }
        if (isSkuConsumable(purchase.skus)) {
            consume(purchase.purchaseToken)
        } else if (skuDetails.type == BillingClient.SkuType.SUBS && !purchase.isAcknowledged) {
            acknowledge(purchase.purchaseToken)
        }
    }

    private fun isSkuConsumable(sku: ArrayList<String>) = CONSUMABLE_SKUS.contains(sku)


    private fun consume(purchaseToken: String) {
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.consumeAsync(
            params
        ) { billingResult, token ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {

                }
                else -> {
                    Log.e("BillingClient", "Failed to consume purchase $billingResult")
                }
            }
        }
    }

    private fun acknowledge(purchaseToken: String) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        billingClient.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {

                }
                else -> {
                    Log.e("BillingClient", "Failed to acknowledge purchase $billingResult")
                }
            }
        }
    }

}