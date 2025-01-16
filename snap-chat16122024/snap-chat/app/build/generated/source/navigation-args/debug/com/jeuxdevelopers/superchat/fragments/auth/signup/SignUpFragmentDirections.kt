package com.jeuxdevelopers.superchat.fragments.auth.signup

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class SignUpFragmentDirections private constructor() {
  public companion object {
    public fun actionSignUpFragmentToInterestsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_signUpFragment_to_interestsFragment)
  }
}
