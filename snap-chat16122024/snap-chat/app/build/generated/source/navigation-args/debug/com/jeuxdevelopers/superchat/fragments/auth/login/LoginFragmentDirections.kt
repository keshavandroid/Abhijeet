package com.jeuxdevelopers.superchat.fragments.auth.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginFragmentToSignUpFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_signUpFragment)

    public fun actionLoginFragmentToInterestsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_interestsFragment)
  }
}
