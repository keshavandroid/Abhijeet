package com.jeuxdevelopers.superchat.fragments.auth.signup

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.textfield.TextInputLayout
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.fragments.auth.login.LoginViewModel
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.AuthRepository
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class SignUpViewModel : ViewModel() {
    companion object {
        const val TAG = "SignUpViewModel"
    }

    private var name = ""
    private var email = ""
    private var password = ""

    // dialog visibility handler
    class WaitingState(var status: Boolean, var message: String)

    private val _waiteState = MutableLiveData(WaitingState(false, ""))
    val isWaiting: LiveData<WaitingState> get() = _waiteState


    // validation of inputs
    class Validation(var status: Boolean, var inputView: TextInputLayout?, var message: String)

    private val _validated = MutableLiveData(Validation(false, null, "Empty"))
    val isValidated: LiveData<Validation> get() = _validated
    fun validateInputs(
        inputName: TextInputLayout,
        inputEmail: TextInputLayout,
        inputPassword: TextInputLayout
    ) {
        val name = inputName.editText?.text.toString()
        val email = inputEmail.editText?.text.toString()
        val password = inputPassword.editText?.text.toString()
        inputName.error = null
        inputEmail.error = null
        inputPassword.error = null
        if (name.isEmpty()) {
            _validated.postValue(Validation(false, inputName, "Please enter your name"))
        } else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _validated.postValue(Validation(false, inputEmail, "Email is empty or invalid"))
        } else if (password.isEmpty() || password.length < 6) {
            _validated.postValue(
                Validation(
                    false,
                    inputPassword,
                    "Password must be greater then 6 characters"
                )
            )
        } else {
            this.name = name
            this.email = email
            this.password = password
            _validated.postValue(Validation(true, null, "Validate data successfully"))
        }
    }


    // authentication
    //  class SignUpResults(var userId: String, var name: String, var email: String)

    private val _signUpResults = MutableLiveData(UserModel())
    private val _signUpError = MutableLiveData<String>()
    val signUpResults: LiveData<UserModel> get() = _signUpResults
    val signUpError: LiveData<String> get() = _signUpError
    fun createUserWithEmailAndPassword() {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Signing Up"))
                val result = AuthRepository().signUpWithEmailAndPassword(email, password)
                if (result != null) {
                    _signUpResults.postValue(UserModel(result.user!!.uid, email, name))
                } else {
                    _signUpError.postValue("Please try again later or check your internet connection")
                }

            } catch (error: Exception) {
                _signUpError.postValue("Please try again later or check your internet connection")
            } finally {
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }


    // login with google
    fun signUpWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Signing In"))
                val authResult = AuthRepository().loginWithGoogle(account)
                if (authResult != null) {
                    // check if new user then signup else login
                    val userModel = UsersRepository().getUserFromDatabaseById(authResult.user!!.uid)
                    if (userModel.name.isEmpty()) {
                        _waiteState.postValue(WaitingState(false, ""))
                        _signUpResults.postValue(
                            UserModel(
                                authResult.user!!.uid,
                                account.email,
                                account.displayName,
                                account.photoUrl.toString()
                            )
                        )
                    } else {
                        _waiteState.postValue(WaitingState(false, ""))
                        _signUpResults.postValue(userModel)
                    }
                } else {
                    _signUpError.postValue("Unable to sign up")
                    _waiteState.postValue(WaitingState(false, ""))
                }
            } catch (e: Exception) {
                _signUpError.postValue("Unable to sign up")
                _waiteState.postValue(WaitingState(false, ""))
            } finally {
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

    // GOOGLE signin client
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

}