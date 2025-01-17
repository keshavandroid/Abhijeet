package com.jeuxdevelopers.superchat.fragments.auth.login

import android.content.Context
import android.util.Patterns
import android.widget.EditText
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
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.AuthRepository
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel : ViewModel() {

    private var _email = ""
    private var _password = ""

    // dialog visibility handler
    class WaitingState(var status: Boolean, var message: String)

    private val _waiteState = MutableLiveData(WaitingState(false, ""))
    val isWaiting: LiveData<WaitingState> get() = _waiteState

    // validation of inputs
    class Validation(var status: Boolean, var inputView: EditText?, var message: String)

    private val _validated = MutableLiveData(Validation(false, null, "Empty"))
    val isValidated: LiveData<Validation> get() = _validated
    fun validateInputs(
        inputEmail: EditText,
        inputPassword: EditText
    ) {
        val email = inputEmail?.text.toString()
        val password = inputPassword?.text.toString()
        inputEmail.error = null
        inputPassword.error = null
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _validated.postValue(Validation(false, inputEmail, "Email is empty or invalid"))
        } else if (password.isEmpty()) {
            _validated.postValue(
                Validation(
                    false,
                    inputPassword,
                    "Password must not be empty"
                )
            )
        } else {
            this._email = email
            this._password = password
            _validated.postValue(Validation(true, null, "Validate data successfully"))
        }
    }


    // login handler
    private val _loginResult = MutableLiveData(UserModel("dum"))
    val loginResult: LiveData<UserModel> get() = _loginResult

    //loginWithEmailAndPassword
    fun loginUserWithEmailAndPassword() {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Signing In"))
                val result = AuthRepository().loginWithEmailAndPassword(_email, _password)
                if (result != null) {
                    val userModel = UsersRepository().getUserFromDatabaseById(result.user!!.uid)
                    _loginResult.postValue(userModel)
                    _waiteState.postValue(WaitingState(false, ""))
                } else {
                    _loginResult.postValue(UserModel())
                    _waiteState.postValue(WaitingState(false, ""))
                }
            } catch (e: Exception) {
                _loginResult.postValue(UserModel())
                _waiteState.postValue(WaitingState(false, ""))
            } finally {
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

    private val _newUser = MutableLiveData(UserModel())
    val isNewUser: LiveData<UserModel> get() = _newUser
    // login with google
    fun loginWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Signing In"))
                val authResult = AuthRepository().loginWithGoogle(account)
                if (authResult != null) {
                    // check if new user then signup else login
                    val userModel = UsersRepository().getUserFromDatabaseById(authResult.user!!.uid)
                    if (userModel.name.isEmpty()) {
                        _waiteState.postValue(WaitingState(false, ""))
                        _newUser.postValue(UserModel(authResult.user!!.uid,account.email,account.displayName,account.photoUrl.toString()))
                    } else {
                        _waiteState.postValue(WaitingState(false, ""))
                        _loginResult.postValue(userModel)
                    }
                } else {
                    _loginResult.postValue(UserModel())
                    _waiteState.postValue(WaitingState(false, ""))
                }
            } catch (e: Exception) {
                _loginResult.postValue(UserModel())
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