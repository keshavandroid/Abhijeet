package com.jeuxdevelopers.superchat.interfaces

import com.jeuxdevelopers.superchat.models.UserModel

interface AuthNavigationListener {
    fun fromLoginFragmentToSignUpFragment()
    fun fromSignUpFragmentToInterestsFragment(userModel: UserModel)
    fun fromLoginFragmentToInterestsFragment(userModel: UserModel)
}