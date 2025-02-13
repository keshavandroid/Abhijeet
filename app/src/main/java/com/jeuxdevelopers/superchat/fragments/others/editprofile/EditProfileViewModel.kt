package com.jeuxdevelopers.superchat.fragments.others.editprofile

import android.content.Context
import android.net.Uri
import android.widget.AutoCompleteTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.jeuxdevelopers.superchat.fragments.auth.interests.InterestsViewModel
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.MyPreferences
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private lateinit var _userModel: UserModel

    fun setUserModel(userModel: UserModel) {
        _userModel = userModel
    }

    fun getUserModel(): UserModel {
        return _userModel
    }

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
        inputName: TextInputLayout,
        edGender: AutoCompleteTextView,
        inputGender: TextInputLayout,
        inputAge: TextInputLayout,
        interestsSize: Int
    ) {
        inputName.error = null
        inputGender.error = null
        inputAge.error = null
        val name = inputName.editText?.text.toString()
        val gender = edGender.text.toString()
        val age = inputAge.editText?.text.toString()
        when {
            _userModel.profileUrl.isEmpty() -> {
                _validated.postValue(
                    Validation(
                        false,
                        null,
                        "Please select profile image"
                    )
                )
            }
            name.isEmpty() -> {
                _validated.postValue(
                    Validation(
                        false,
                        inputName,
                        "Please enter your name"
                    )
                )
            }
            gender.isEmpty() -> {
                _validated.postValue(
                    Validation(
                        false,
                        inputGender,
                        "Please select gender"
                    )
                )
            }
            age.isEmpty() -> {
                _validated.postValue(
                    Validation(
                        false,
                        inputAge,
                        "Please enter your Age"
                    )
                )
            }
            age.toInt() <= 0 -> {
                _validated.postValue(
                    Validation(
                        false,
                        inputAge,
                        "Please enter valid Age"
                    )
                )
            }
            interestsSize == 0 -> {
                _validated.postValue(
                    Validation(
                        false,
                        null,
                        "Please chose some interests"
                    )
                )
            }
            else -> {
                _userModel.name = name
                _userModel.gender = gender
              //  _userModel.age = age.toInt()
                _validated.postValue(
                    Validation(
                        true,
                        null,
                        "Validation successful"
                    )
                )
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


    //add user to database
    private val _updateUser = MutableLiveData<Boolean>()
    val isUpdateUser: LiveData<Boolean> get() = _updateUser
    fun updateUser(context: Context) {

        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Updating profile"))
                UsersRepository().addNewUserToDatabase(_userModel)
                MyPreferences(context).saveCurrentUser(_userModel)
                _updateUser.postValue(true)
            } catch (e: Exception) {
                _waiteState.postValue(WaitingState(false, ""))
                _updateUser.postValue(false)
            } finally {
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

}