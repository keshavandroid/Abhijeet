package com.jeuxdevelopers.superchat.fragments.others.setPayment

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeuxdevelopers.superchat.enums.PaymentType
import com.jeuxdevelopers.superchat.models.PaymentMethod
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.MyPreferences
import kotlinx.coroutines.launch

class SetPaymentViewModel : ViewModel() {

    private var _payId = ""
    private var _phone = ""
    private var _payType: PaymentType = PaymentType.NONE

    // dialog visibility handler
    class WaitingState(var status: Boolean, var message: String)

    private val _waiteState = MutableLiveData(WaitingState(false, ""))
    val isWaiting: LiveData<WaitingState> get() = _waiteState

    // validation of inputs
    class Validation(var status: Boolean, var inputView: EditText?, var message: String)

    private val _validated = MutableLiveData(Validation(false, null, "Empty"))
    val isValidated: LiveData<Validation> get() = _validated

    fun validate(
        etPaymentId: EditText,
        etPhone: EditText,
        paymentType: PaymentType
    ) {
        etPaymentId.error = null
        etPhone.error = null

        val paymentId = etPaymentId.text.toString()
        val phone = etPhone.text.toString()

        when {
            paymentId.isEmpty() -> {
                _validated.postValue(
                    Validation(
                        false,
                        etPaymentId,
                        "Please set payment ID"
                    )
                )
            }
            phone.isEmpty() || !Patterns.PHONE.matcher(phone).matches() -> {
                _validated.postValue(
                    Validation(
                        false,
                        etPhone,
                        "Phone is empty or incorrect"
                    )
                )
            }
            paymentType == PaymentType.NONE -> {
                _validated.postValue(
                    Validation(
                        false,
                        null,
                        "Please select payment type"
                    )
                )
            }
            else -> {
                _payId = paymentId
                _phone = phone
                _payType = paymentType
                _validated.postValue(
                    Validation(
                        true,
                        null,
                        "Validated successfully"
                    )
                )
            }
        }
    }


    // add payment method to database
    private val _addPayMethod = MutableLiveData(false)
    val isAddPayMethod: LiveData<Boolean> get() = _addPayMethod
    fun setPaymentMethod(currentUserId: String, context: Context) {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Updating payment method"))
                val result = UsersRepository().addPaymentMethodToUser(
                    currentUserId,
                    PaymentMethod(_payId, _phone, _payType)
                )
                // update in preferences also
                val user = MyPreferences(context).getCurrentUser()
                user.paymentMethod = PaymentMethod(_payId, _phone, _payType)
                MyPreferences(context).saveCurrentUser(user)
                _addPayMethod.postValue(result)
            } catch (e: Exception) {
                _waiteState.postValue(WaitingState(false, ""))
            } finally {
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

}