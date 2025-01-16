package com.jeuxdevelopers.superchat.utils

object Constants {

    // const strings
    const val UNAVAILABLE = "Unavailable"

    // variable names
    const val USER_ID = "userId"
    const val MESSAGE_SENT = "sent"
    const val MESSAGE_SERVER_TIME = "time"
    const val MESSAGE_TIME = "time"
    const val USER_PAYMENT_METHOD = "paymentMethod"
    const val BALANCE_USER = "balance"
    const val CREDIT_USER = "credit"
   const val USER_CONNECTION_STATE = "userState"

    const val CREDIT_FIELD = "credit"
    const val IMAGE_PRICE_FIELD = "pricePerImage"
    const val CHARACTER_PRICE_FIELD = "pricePerCharacter"

    //storage references
    const val PROFILE_IMAGES = "Profile-Images"
    const val CHAT_STORAGE = "Chat-Storage"

    // collection names
    const val USERS_COLLECTION = "Users"
    const val CHAT_COLLECTION = "Chat"
    const val MESSAGES_COLLECTION = "Messages"
    const val CHAT_INBOX = "Inbox"
    const val INBOX_USERS = "InboxUsers"

    // argument keys
    const val USER_MODEL = "user-model"

    //const val INBOX_MODEL = "inbox-model"
    const val OTHER_USER_ID = "other-user-id"
    const val OTHER_USER_IMAGE = "other-user-image"
    const val OTHER_USER_NAME = "other-user-name"


    // list of interests
    fun getInterestsList(): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        list.add("Activities")
        list.add("Guitar")
        list.add("Fishing")
        list.add("Biking")
        list.add("Beach")
        list.add("Dancing")
        list.add("Hiking")
        list.add("Camping")
        list.add("Netflix")
        list.add("Movies")
        list.add("TV Shows")
        list.add("Books")
        list.add("Games")
        list.add("Fashion")
        return list
    }

    // list of gender
    fun getGendersList(): MutableList<String> {
        val list: MutableList<String> = ArrayList()
        list.add("Male")
        list.add("Female")
        return list
    }

    // payment types list
    fun getPaymentTypesList(): MutableList<String> {
        val list: ArrayList<String> = ArrayList()
        list.add("Paypal")
        list.add("Bank Transfer")
        return list
    }

}