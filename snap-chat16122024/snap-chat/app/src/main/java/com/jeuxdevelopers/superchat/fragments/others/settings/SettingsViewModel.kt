package com.jeuxdevelopers.superchat.fragments.others.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.SettingRepository

class SettingsViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository = SettingRepository(
        application.applicationContext
    )

    fun getCurrentUser(): UserModel {

        return repository.getCurrentUser()

    }

    fun saveUserPrice(pricePerImage: Double, pricePerCharacter: Double): LiveData<Boolean> {

        return repository.saveUserPrice(pricePerImage, pricePerCharacter)

    }

}