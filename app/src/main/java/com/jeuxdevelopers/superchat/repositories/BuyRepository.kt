package com.jeuxdevelopers.superchat.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import kotlinx.coroutines.tasks.await

class BuyRepository(applicationContext: Context) {

    companion object {
        const val TAG = "BuyRepository"
    }

    private val firestore = FirebaseFirestore.getInstance()

    private val preferences = MyPreferences(applicationContext)

    private val currentCredit = MutableLiveData(preferences.getCurrentUser().credit)

    fun getCurrentCredit(): LiveData<Double> {

        return currentCredit

    }


    fun addCreditToUser(amount: Double, isFromBalance: Boolean): LiveData<Boolean> {

        val result: MutableLiveData<Boolean> = MutableLiveData()

        val currentUser = preferences.getCurrentUser()

        if (currentUser.credit == 0.0) {

            firestore.collection(Constants.USERS_COLLECTION)
                .document(currentUser.userId)
                .update(
                    Constants.CREDIT_FIELD, amount
                )
                .addOnSuccessListener {

                    currentUser.credit = currentUser.credit + amount

                    if (isFromBalance){
                        deductFromBalanceCurrentUser(amount)
                    }

                    preferences.saveCurrentUser(currentUser)

                    currentCredit.postValue(currentUser.credit)

                    result.postValue(true)

                }.addOnFailureListener {

                    result.postValue(false)

                    Log.d(TAG, "Error -> ${it.localizedMessage})")
                }

        } else {

            firestore.collection(Constants.USERS_COLLECTION)
                .document(currentUser.userId)
                .update(
                    Constants.CREDIT_FIELD,
                    FieldValue.increment(amount)
                )
                .addOnSuccessListener {

                    currentUser.credit = currentUser.credit + amount
                    if (isFromBalance){
                        deductFromBalanceCurrentUser(amount)
                    }

                    preferences.saveCurrentUser(currentUser)

                    currentCredit.postValue(currentUser.credit)

                    result.postValue(true)

                }.addOnFailureListener {

                    result.postValue(false)

                    Log.d(TAG, "Error -> ${it.localizedMessage})")
                }

        }

        return result

    }

   private  fun deductFromBalanceCurrentUser(amount: Double) {
        val balance = preferences.getCurrentUser().balance - amount
        firestore.collection(Constants.USERS_COLLECTION).document(preferences.getCurrentUser().userId)
            .update(Constants.BALANCE_USER,balance).addOnSuccessListener {
                val user = preferences.getCurrentUser();
                user.balance = balance
                preferences.saveCurrentUser(user)
            }

    }

    suspend fun addBalanceToUser(amount: Double, userId:String):Boolean {
        return try {
            val otherUser = UsersRepository().getUserFromDatabaseById(userId)
            val balance = otherUser.balance + amount
            firestore.collection(Constants.USERS_COLLECTION).document(userId) .update(Constants.BALANCE_USER,balance).await()
            if (userId == preferences.getCurrentUser().userId){
                val user = preferences.getCurrentUser()
                user.balance = balance
                preferences.saveCurrentUser(user)
            }
            true
        }catch (e:Exception){
            false
        }

    }

   suspend fun deductFromCreditCurrentUser(amount: Double):Boolean {
       val credit = preferences.getCurrentUser().credit - amount
       return try {
           firestore.collection(Constants.USERS_COLLECTION).document(preferences.getCurrentUser().userId)
               .update(Constants.CREDIT_USER,credit).await()
           val user = preferences.getCurrentUser()
           user.credit = credit
           preferences.saveCurrentUser(user)
           true
       }catch (e:Exception){
           false
       }
    }

}