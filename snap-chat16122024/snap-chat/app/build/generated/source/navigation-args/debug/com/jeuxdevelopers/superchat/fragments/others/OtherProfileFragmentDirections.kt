package com.jeuxdevelopers.superchat.fragments.others

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class OtherProfileFragmentDirections private constructor() {
  public companion object {
    public fun actionOtherProfileFragmentToChatFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_otherProfileFragment_to_chatFragment)
  }
}
