package com.xorbix.loopdeck.ui.subscription

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.SkuType.SUBS
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.ui.collection.CollectionActivity
import com.xorbix.loopdeck.ui.subscription.Security.verifyPurchase
import java.io.IOException


@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING", "DEPRECATED_IDENTITY_EQUALS")
class SubscriptionActivity : AppCompatActivity(), PurchasesUpdatedListener {

    val PREF_FILE = "MyPref"
    val SUBSCRIBE_KEY = "subscribe"
    val ITEM_SKU_SUBSCRIBE = "loopdeck"

    var premiumContent: TextView? = null
    var subscriptionStatus: TextView? = null
    var subscribe: Button? = null

    private var billingClient: BillingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        premiumContent = findViewById(R.id.premium_content)
        subscriptionStatus = findViewById(R.id.subscription_status)
        subscribe = findViewById(R.id.subscribe)




        billingClient =
            BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val queryPurchase = billingClient?.queryPurchases(SUBS)
                    val queryPurchases = queryPurchase?.purchasesList
                    if (queryPurchases != null && queryPurchases.size > 0) {
                        handlePurchases(queryPurchases)
                    } else {
                        saveSubscribeValueToPref(false)
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                Toast.makeText(applicationContext, "Service Disconnected", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        if (getSubscribeValueFromPref()) {
            subscribe?.visibility = View.GONE
            premiumContent?.visibility = View.VISIBLE
            subscriptionStatus?.text = "Subscription Status : Subscribed"
        }
        //item not subscribed
        else {
            premiumContent?.visibility = View.GONE
            subscribe?.visibility = View.VISIBLE
            subscriptionStatus?.text = "Subscription Status : Not Subscribed"
        }
    }

    override fun onStart() {
        super.onStart()

        if (getSubscribeValueFromPref()) {
            startActivity(
                Intent(
                    this,
                    CollectionActivity::class.java
                )
            )
            return
        }


        if (billingClient!!.isReady) {
            initiatePurchase()
        } else {
            billingClient =
                BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Error " + billingResult.debugMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Toast.makeText(applicationContext, "Service Disconnected ", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    fun subscribe(view: View?) {
        //check if service is already connected

    }

    private fun getPreferenceObject(): SharedPreferences {
        return applicationContext.getSharedPreferences(PREF_FILE, 0)
    }

    private fun getPreferenceEditObject(): SharedPreferences.Editor {
        val pref = applicationContext.getSharedPreferences(PREF_FILE, 0)
        return pref.edit()
    }

    private fun getSubscribeValueFromPref(): Boolean {
        return getPreferenceObject().getBoolean(SUBSCRIBE_KEY, false)
    }

    private fun saveSubscribeValueToPref(value: Boolean) {
        getPreferenceEditObject().putBoolean(SUBSCRIBE_KEY, value).commit()
    }


    private fun initiatePurchase() {
        val skuList: MutableList<String> = ArrayList()
        skuList.add(ITEM_SKU_SUBSCRIBE)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(SUBS)
        val billingResult =
            billingClient?.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            billingClient?.querySkuDetailsAsync(
                params.build()
            ) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailsList != null && skuDetailsList.size > 0) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList[0])
                            .build()
                        billingClient?.launchBillingFlow(this, flowParams)
                    } else {
                        //try to add subscription item "sub_example" in google play console
                        Toast.makeText(applicationContext, "Item not Found", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        " Error " + billingResult.debugMessage, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Sorry Subscription not Supported. Please Update Play Store", Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode === BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
            startActivity(
                Intent(
                    this,
                    CollectionActivity::class.java
                )
            )
        } else if (billingResult.responseCode === BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Toast.makeText(applicationContext, "Item already purchased", Toast.LENGTH_SHORT).show()
            val queryAlreadyPurchasesResult = billingClient!!.queryPurchases(SUBS)
            val alreadyPurchases = queryAlreadyPurchasesResult.purchasesList
            alreadyPurchases?.let { handlePurchases(it) }
            startActivity(
                Intent(
                    this,
                    CollectionActivity::class.java
                )
            )
        } else if (billingResult.responseCode === BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(applicationContext, "Purchase Canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                applicationContext,
                "Error " + billingResult.debugMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            //if item is purchased
            if (ITEM_SKU_SUBSCRIBE == purchase.skus.toString() && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(
                        applicationContext,
                        "Error : invalid Purchase",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
            } else if (ITEM_SKU_SUBSCRIBE == purchase.skus.toString() && purchase.purchaseState == Purchase.PurchaseState.PENDING) {
                Toast.makeText(
                    applicationContext,
                    "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT
                ).show()
            } else if (ITEM_SKU_SUBSCRIBE == purchase.skus.toString() && purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                saveSubscribeValueToPref(false)
                premiumContent!!.visibility = View.GONE
                subscribe!!.visibility = View.VISIBLE
                subscriptionStatus!!.text = "Subscription Status : Not Subscribed"
                Toast.makeText(applicationContext, "Purchase Status Unknown", Toast.LENGTH_SHORT)
                    .show()
            } else if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient!!.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase)
            } else {
                // Grant entitlement to the user on item purchase
                // restart activity
                if (getSubscribeValueFromPref()) {
                    saveSubscribeValueToPref(true)
                    Toast.makeText(applicationContext, "Item Purchased", Toast.LENGTH_SHORT)
                        .show()
                    recreate()

                }


            }
        }
    }

    var ackPurchase =
        AcknowledgePurchaseResponseListener { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
                saveSubscribeValueToPref(true)
                this.recreate()
            }
        }

    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            val base64Key =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlPMs7N9bwVLn5WpQnLxmDKRfC83c8xK5bdlLo919OHBfNtvO4W4YOMN+UcnZiNrkgsILOPhzLzaPeOzJYpab4Flwtc/3XhIwX44wf34sCCQ0+jXA784k9rqH4jbmwhaiLXMS+2V6CO5A4Y2wTPci2w8EmQHD57WDxrOCxiKPvmOr2dQkQgBw9ESF2lfpGvjGR4Ym17YJrDTa2ppMaV+goK0ZVkDvl8Khf3yxZL1VrPocrre+roUHWGW1Z81CBI3s+/INP/4aPdXYH6rNLKTBBijDGO4cJHM5Kq00yc/2O21o2AsJf2yS7b30+3QxlEhQQ/1LnPP5dIr/g3IIA7HiqQIDAQAB"
            verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (billingClient != null) {
            billingClient!!.endConnection()
        }
    }


}