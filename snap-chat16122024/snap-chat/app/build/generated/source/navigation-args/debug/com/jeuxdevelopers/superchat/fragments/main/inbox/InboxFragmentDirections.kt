package com.jeuxdevelopers.superchat.fragments.main.inbox

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.jeuxdevelopers.superchat.R

public class InboxFragmentDirections private constructor() {
  public companion object {
    public fun actionInboxFragmentToSettingsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_inboxFragment_to_settingsFragment)

    public fun actionInboxFragmentToChatFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_inboxFragment_to_chatFragment)
  }
}
