package com.jeuxdevelopers.superchat.fragments.others.buycredit

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.jeuxdevelopers.superchat.enums.CreditPurchase
import com.jeuxdevelopers.superchat.repositories.BuyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class BuyCreditViewModel(application: Application) :
    AndroidViewModel(application) {

    companion object {
        const val TAG = "BuyCreditViewModel"
    }

    private val repository = BuyRepository(
        application.applicationContext
    )

    // billing client purchase methods---------------------------------------

    private var _isConnectedToPayment = false

    // listener for purchase updates
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "Purchase success: message -> ${billingResult.debugMessage} ")
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d(TAG, "user cancel the billing flow: message -> ${billingResult.debugMessage}")
        } else {
            Log.d(TAG, "some other error exist -> :${billingResult.debugMessage}")
            // Handle any other error codes.
        }
    }



    private fun handlePurchase(purchase: Purchase) {
        Log.d(TAG, "handlePurchase: purchase -> ${purchase.packageName}")
        viewModelScope.launch {
            //TODO: initiate consume in the case of consumable item e.g Subscription item
//            val consumeParams =
//                ConsumeParams.newBuilder()
//                    .setPurchaseToken(purchase.purchaseToken)
//                    .build()
//            val consumeResult = withContext(Dispatchers.IO) {
//                billingClient.consumePurchase(consumeParams)
//            }
//            Log.d(TAG, "handlePurchase: consume results -> ${consumeResult.billingResult.debugMessage}")


            // perform acknowledgement in case of purchased item .e.g In app purchase
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    val ackPurchaseResult = withContext(Dispatchers.IO) {
                        billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
                    }
                    Log.d(TAG, "handlePurchase: ${ackPurchaseResult.debugMessage}")
                    //TODO: here perform db related update
                } else {
                    Log.d(TAG, "handlePurchase: purchase already acknowledged")
                    //TODO: here perform db related update
                }
            } else {
                Log.d(TAG, "handlePurchase: not purchased state -> ${purchase.purchaseState}")
            }


        }

    }

    private var billingClient = BillingClient.newBuilder(application.applicationContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    //establish connection
    fun startBillingConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.d(
                    TAG,
                    "onBillingServiceDisconnected: unable to connect, try again in next request"
                )
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        _isConnectedToPayment = true
                        Log.d(
                            TAG,
                            "onBillingSetupFinished: The billing is ready. You can purchase here"
                        )
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        Log.d("BillingClient", "Connection -> Response Code -> USER_CANCELED")
                    }
                    else -> {
                        Log.d(TAG, "onBillingSetupFinished: error -> ${billingResult.debugMessage}")
                    }
                }
            }

        })
    }

    fun purchaseWithInAppPurchase(purchase: CreditPurchase, activity: Activity) {
        if (_isConnectedToPayment) {
            viewModelScope.launch {
                val skuList = ArrayList<String>()
                skuList.add(purchase.name)
                val params = SkuDetailsParams.newBuilder()
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                // leverage querySkuDetails Kotlin extension function
                val skuDetailsResult = withContext(Dispatchers.IO) {
                    billingClient.querySkuDetails(params.build())
                }
                Log.d(TAG, "Sku details result -> ${skuDetailsResult.billingResult.responseCode}")
                if (skuDetailsResult.skuDetailsList != null && skuDetailsResult.skuDetailsList!!.isNotEmpty()) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetailsResult.skuDetailsList!![0])
                        .build()
                    val responseCode =
                        billingClient.launchBillingFlow(activity, flowParams).responseCode
                    Log.d(TAG, "purchaseWithInAppPurchase: launch response code -> $responseCode")
                }else{
                    Log.d(TAG, "purchaseWithInAppPurchase: sku results are empty or null")
                }
            }
        } else {
            Log.d(TAG, "purchaseWithInAppPurchase: unable to connect, please try again")
        }


    }

    // update to database methods----------------------------------------------

    fun addCreditToUser(amount: Double, isFromBalance: Boolean): LiveData<Boolean> {
        return repository.addCreditToUser(amount, isFromBalance)
    }

    fun getCurrentCredit(): LiveData<Double> {
        return repository.getCurrentCredit()

    }

}