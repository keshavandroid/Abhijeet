package com.jeuxdevelopers.superchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.ActivityAuthenticationBinding
import com.jeuxdevelopers.superchat.databinding.FragmentLogin1Binding
import com.jeuxdevelopers.superchat.interfaces.AuthNavigationListener
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.utils.Constants

class AuthenticationActivity : AppCompatActivity(), AuthNavigationListener {
    private lateinit var binding: ActivityAuthenticationBinding
 //   private lateinit var binding: FragmentLogin1Binding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.auth_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun fromLoginFragmentToSignUpFragment() {
        navController.navigate(R.id.action_loginFragment_to_signUpFragment)
    }

    override fun fromSignUpFragmentToInterestsFragment(userModel: UserModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.USER_MODEL, userModel)
        navController.navigate(R.id.action_signUpFragment_to_interestsFragment, bundle)
    }

    override fun fromLoginFragmentToInterestsFragment(userModel: UserModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.USER_MODEL, userModel)
        navController.navigate(R.id.action_loginFragment_to_interestsFragment, bundle)
    }
}