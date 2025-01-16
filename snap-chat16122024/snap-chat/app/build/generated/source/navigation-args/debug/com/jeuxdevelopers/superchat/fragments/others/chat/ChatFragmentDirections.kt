package com.jeuxdevelopers.superchat.fragments.others.chat

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class ChatFragmentDirections private constructor() {
  public companion object {
    public fun actionChatFragmentToBuyCreditFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_chatFragment_to_buyCreditFragment)
  }
}
