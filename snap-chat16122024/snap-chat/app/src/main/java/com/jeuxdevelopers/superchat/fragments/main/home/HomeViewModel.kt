package com.jeuxdevelopers.superchat.fragments.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeuxdevelopers.superchat.adapters.UsersAdapter
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {


    fun setRecyclerListener(adapter: UsersAdapter, list: MutableList<UserModel>) {
        UsersRepository().setFirebaseUsersListener(adapter, list)
    }
}