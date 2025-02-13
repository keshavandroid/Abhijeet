package com.jeuxdevelopers.superchat.fragments.auth.interests

import android.content.Context
import android.net.Uri
import android.widget.AutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.MyPreferences
import kotlinx.coroutines.launch

class InterestsViewModel : ViewModel() {

    private var _gender = ""
    private var _age: Int = 0
    private var _joinDate: Long = 0

    // dialog visibility handler
    class WaitingState(var status: Boolean, var message: String)

    private val _waiteState = MutableLiveData(WaitingState(false, ""))
    val isWaiting: LiveData<WaitingState> get() = _waiteState

    // validation of inputs
    class Validation(var status: Boolean, var inputView: TextInputLayout?, var message: String)

    private val _validated = MutableLiveData(Validation(false, null, "Empty"))
    val isValidated: LiveData<Validation> get() = _validated

    fun validate(
        profileUri: String,
        edGender: AutoCompleteTextView,
        inputGender: TextInputLayout,
        inputAge: TextInputLayout,
        interestsSize: Int
    ) {
        inputGender.error = null
        inputAge.error = null
        val gender = edGender.text.toString()
        val age = inputAge.editText?.text.toString()
        when {
            profileUri.isEmpty() -> {
                _validated.postValue(Validation(false, null, "Please select profile image"))
            }
            gender.isEmpty() -> {
                _validated.postValue(Validation(false, inputGender, "Please select gender"))
            }
            age.isEmpty() -> {
                _validated.postValue(Validation(false, inputAge, "Please enter your Age"))
            }
            age.toInt() <= 0 -> {
                _validated.postValue(Validation(false, inputAge, "Please enter valid Age"))
            }
            interestsSize == 0 -> {
                _validated.postValue(Validation(false, null, "Please chose some interests"))
            }
            else -> {
                _gender = gender
                _age = age.toInt()
                _joinDate = System.currentTimeMillis()
                _validated.postValue(Validation(true, null, "Validation successful"))
            }
        }
    }


    //add user to database
    private val _addNewUser = MutableLiveData<Boolean>()
    val isAddNewUser: LiveData<Boolean> get() = _addNewUser
    fun addNewUser(userModel: UserModel, context: Context) {
        userModel.joinDate = _joinDate
        userModel.gender = _gender
        //userModel.age = _age
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Adding New User"))
                UsersRepository().addNewUserToDatabase(userModel)
                MyPreferences(context).saveCurrentUser(userModel)
                _addNewUser.postValue(true)
            } catch (e: Exception) {
                _waiteState.postValue(WaitingState(false, ""))
                _addNewUser.postValue(false)
            } finally {
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

    // add user profile to bucket
    private val _addProfileImage = MutableLiveData("")
    val addProfileImage: LiveData<String> get() = _addProfileImage
    fun addUserProfileImage(localUri: Uri) {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Uploading profile"))
                val imageUrl = UsersRepository().addUserProfileImage(localUri)
                _addProfileImage.postValue(imageUrl)
            } catch (e: Exception) {
                _addProfileImage.postValue("")
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

}