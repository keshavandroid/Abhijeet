package com.jeuxdevelopers.superchat.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences

class SettingRepository(applicationContext: Context) {

    private val preferences = MyPreferences(applicationContext)

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): UserModel {

        return preferences.getCurrentUser()

    }

    fun saveUserPrice(pricePerImage: Double, pricePerCharacter: Double): LiveData<Boolean> {

        val result: MutableLiveData<Boolean> = MutableLiveData()

        val currentUser = preferences.getCurrentUser()

        firestore.collection(Constants.USERS_COLLECTION)
            .document(currentUser.userId)
            .update(
                Constants.IMAGE_PRICE_FIELD, pricePerImage,
                Constants.CHARACTER_PRICE_FIELD, pricePerCharacter

            ).addOnSuccessListener {

                currentUser.pricePerImage = pricePerImage
                currentUser.pricePerCharacter = pricePerCharacter

                preferences.saveCurrentUser(currentUser)

                result.postValue(true)

            }.addOnFailureListener {

                result.postValue(false)

                Log.d(BuyRepository.TAG, "Error -> ${it.localizedMessage})")

            }


        return result

    }

}