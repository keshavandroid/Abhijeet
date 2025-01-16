package com.jeuxdevelopers.superchat.fragments.others.settings

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class SettingsFragmentDirections private constructor() {
  public companion object {
    public fun actionSettingsFragmentToSetPaymentFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_settingsFragment_to_setPaymentFragment)

    public fun actionSettingsFragmentToBuyCreditFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_settingsFragment_to_buyCreditFragment)

    public fun actionSettingsFragmentToPrivacyPolicyFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_settingsFragment_to_privacyPolicyFragment)

    public fun actionSettingsFragmentToTermsOfUseFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_settingsFragment_to_termsOfUseFragment)

    public fun actionSettingsFragmentToHelpFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_settingsFragment_to_helpFragment)
  }
}
