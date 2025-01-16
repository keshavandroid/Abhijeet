package com.jeuxdevelopers.superchat.fragments.main.inbox

import androidx.lifecycle.ViewModel
import com.jeuxdevelopers.superchat.adapters.InboxAdapter
import com.jeuxdevelopers.superchat.models.InboxModel
import com.jeuxdevelopers.superchat.repositories.InboxRepository

class InboxViewModel : ViewModel() {

    fun setInboxListener(adapter:InboxAdapter, list:MutableList<InboxModel>, currentUserId:String){
        InboxRepository(currentUserId,"not available").setInboxRecycler(adapter,list)
    }
}