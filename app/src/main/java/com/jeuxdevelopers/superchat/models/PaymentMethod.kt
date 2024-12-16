package com.jeuxdevelopers.superchat.models
import com.jeuxdevelopers.superchat.enums.PaymentType
import java.io.Serializable

data class PaymentMethod(
    var paymentId:String = "",
    var phoneNumber:String = "",
    var paymentType: PaymentType = PaymentType.NONE
):Serializable
