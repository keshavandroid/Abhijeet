package com.jeuxdevelopers.superchat.fragments.main

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class ProfileFragmentDirections private constructor() {
  public companion object {
    public fun actionProfileFragmentToSettingsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_settingsFragment)

    public fun actionProfileFragmentToEditProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_editProfileFragment)
  }
}
