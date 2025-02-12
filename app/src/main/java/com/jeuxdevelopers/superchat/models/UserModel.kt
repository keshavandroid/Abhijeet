package com.jeuxdevelopers.superchat.models

import com.jeuxdevelopers.superchat.enums.UserState
import java.io.Serializable

data class UserModel(
    var userId: String = "",
    var email: String = "",
    var name: String = "",
    var profileUrl: String = "",
    var interestsList: MutableList<String> = ArrayList(),
    var gender: String = "",
   // var age: Int  = 0,
    //var age:String = "",
    var joinDate: Long = 0,
    var userState: UserState = UserState.OFFLINE,
    var lastSeen: Long = 0,
    var paymentMethod: PaymentMethod = PaymentMethod(),
    var chatRate: RateModel = RateModel(),
    var balance: Double = 0.0,
    var credit: Double = 0.0,
    var pricePerImage:Double = 0.0,
    var pricePerCharacter:Double = 0.0

) : Serializable
