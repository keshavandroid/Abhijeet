package com.jeuxdevelopers.superchat.fragments.main.home

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class HomeFragmentDirections private constructor() {
  public companion object {
    public fun actionHomeFragmentToSettingsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_settingsFragment)

    public fun actionHomeFragmentToOtherProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_otherProfileFragment)
  }
}
