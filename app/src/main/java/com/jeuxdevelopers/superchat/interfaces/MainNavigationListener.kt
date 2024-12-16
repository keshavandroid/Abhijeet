package com.jeuxdevelopers.superchat.interfaces

import com.jeuxdevelopers.superchat.models.InboxModel
import com.jeuxdevelopers.superchat.models.UserModel

interface MainNavigationListener {
    fun fromInboxFragmentToSettingsFragment()
    fun fromHomeFragmentToSettingsFragment()
    fun fromProfileFragmentToSettingsFragment()
    fun fromSettingsFragmentToBuyCreditFragment()
    fun fromSettingsFragmentToSetPaymentFragment()
    fun fromHomeFragmentToOtherProfileFragment(userModel: UserModel)
    fun fromOtherProfileFragmentToChatFragment(userModel: UserModel)
    fun fromInboxFragmentToChatFragment(inboxModel: InboxModel)
    fun fromChatFragmentToBuyCreditFragment()
    fun fromProfileFragmentToEditProfileFragment()

    fun fromSettingsFragmentToHelpFragment()
    fun fromSettingsFragmentToPrivacyPolicyFragment()
    fun fromSettingsFragmentToTermsOfUseFragment()

}