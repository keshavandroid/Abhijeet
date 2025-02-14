package com.jeuxdevelopers.superchat.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.ActivityMainBinding
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.models.InboxModel
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.google.android.material.bottomnavigation.BottomNavigationItemView


import android.view.LayoutInflater

import android.widget.FrameLayout
import com.google.android.material.bottomnavigation.BottomNavigationMenuView


class MainActivity : AppCompatActivity(), MainNavigationListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val userRepo = UsersRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        initNavListener()
        // userRepo.setConnectionListener(MyPreferences(this).getCurrentUser().userId, this)
        // bottomNavListener()
    }

    private fun initNavListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment -> binding.bottomNav.visibility = View.GONE

                R.id.buyCreditFragment -> binding.bottomNav.visibility = View.GONE

                R.id.setPaymentFragment -> binding.bottomNav.visibility = View.GONE

                R.id.otherProfileFragment -> binding.bottomNav.visibility = View.GONE

                R.id.editProfileFragment -> binding.bottomNav.visibility = View.GONE

                R.id.chatFragment -> binding.bottomNav.visibility = View.GONE

                R.id.helpFragment -> binding.bottomNav.visibility = View.GONE

                R.id.privacyPolicyFragment -> binding.bottomNav.visibility = View.GONE

                R.id.termsOfUseFragment -> binding.bottomNav.visibility = View.GONE

                R.id.inboxFragment -> {
                    removeBadge(0)
                    removeBadge(1)
                    removeBadge(2)
                    addBadge(0)
                    binding.bottomNav.visibility = View.VISIBLE
                }

                R.id.homeFragment -> {
                    removeBadge(0)
                    removeBadge(1)
                    removeBadge(2)
                    addBadge(1)
                    binding.bottomNav.visibility = View.VISIBLE
                }

                R.id.profileFragment -> {
                    removeBadge(0)
                    removeBadge(1)
                    removeBadge(2)
                    addBadge(2)
                    binding.bottomNav.visibility = View.VISIBLE
                }
                else -> binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }


    private fun bottomNavListener() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.inboxFragment -> {
                    addBadge(0)
                    removeBadge(1)
                    removeBadge(2)
                    navController.navigate(R.id.inboxFragment)
                }
                R.id.homeFragment -> {
                    removeBadge(0)
                    addBadge(1)
                    removeBadge(2)
                    navController.navigate(R.id.homeFragment)
                }
                R.id.profileFragment -> {
                    removeBadge(0)
                    removeBadge(1)
                    addBadge(2)
                    navController.navigate(R.id.profileFragment)
                }
            }

            true
        }
        binding.bottomNav.selectedItemId = R.id.homeFragment
    }


    @SuppressLint("PrivateResource", "RestrictedApi")
    private fun addBadge(position: Int) {
        // get badge container (parent)
        val bottomMenu = binding.bottomNav.getChildAt(0) as? BottomNavigationMenuView
        val v = bottomMenu?.getChildAt(position) as? BottomNavigationItemView

        if (position==0) {
            v!!.setTitle("Home")
        }

        // inflate badge from layout
        val badge = LayoutInflater.from(this).inflate(R.layout.badge_layout, bottomMenu, false)
        badge.id = position * 120
        // create badge layout parameter
        val badgeLayout: FrameLayout.LayoutParams =
            FrameLayout.LayoutParams(badge?.layoutParams!!).apply {
                gravity = Gravity.CENTER_HORIZONTAL
               // topMargin = resources.getDimension(R.dimen.design_bottom_navigation_margin).toInt()
                topMargin = resources.getDimension(R.dimen.ten_dp).toInt()

                // <dimen name="bagde_left_margin">8dp</dimen>
                leftMargin = resources.getDimension(R.dimen.nine_dp).toInt()
            }

        // add view to bottom bar with layout parameter
        v?.addView(badge, badgeLayout)
    }

    @SuppressLint("PrivateResource")
    private fun removeBadge(position: Int) {
        // get badge container (parent)
        val bottomMenu = binding.bottomNav.getChildAt(0) as? BottomNavigationMenuView
        val v = bottomMenu?.getChildAt(position) as? BottomNavigationItemView

        val b = v?.findViewById<View>(position * 120)
        if (b != null) {
            v.removeView(b)
        }

    }


    override fun fromInboxFragmentToSettingsFragment() {
        navController.navigate(R.id.action_inboxFragment_to_settingsFragment)
    }

    override fun fromHomeFragmentToSettingsFragment() {
        navController.navigate(R.id.action_homeFragment_to_settingsFragment)
    }

    override fun fromProfileFragmentToSettingsFragment() {
        navController.navigate(R.id.action_profileFragment_to_settingsFragment)
    }

    override fun fromSettingsFragmentToBuyCreditFragment() {
        navController.navigate(R.id.action_settingsFragment_to_buyCreditFragment)
    }

    override fun fromSettingsFragmentToSetPaymentFragment() {
        navController.navigate(R.id.action_settingsFragment_to_setPaymentFragment)
    }

    override fun fromHomeFragmentToOtherProfileFragment(userModel: UserModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.USER_MODEL, userModel)
        navController.navigate(R.id.action_homeFragment_to_otherProfileFragment, bundle)
    }

    override fun fromOtherProfileFragmentToChatFragment(userModel: UserModel) {
        val bundle = Bundle()
        bundle.putString(Constants.OTHER_USER_ID, userModel.userId)
        bundle.putString(Constants.OTHER_USER_IMAGE, userModel.profileUrl)
        bundle.putString(Constants.OTHER_USER_NAME, userModel.name)
        navController.navigate(R.id.action_otherProfileFragment_to_chatFragment, bundle)
    }

    override fun fromInboxFragmentToChatFragment(inboxModel: InboxModel) {
        val bundle = Bundle()
        if (MyPreferences(this).getCurrentUser().userId == inboxModel.senderId) {
            bundle.putString(Constants.OTHER_USER_ID, inboxModel.receiverId)
        } else {
            bundle.putString(Constants.OTHER_USER_ID, inboxModel.senderId)
        }
        bundle.putString(Constants.OTHER_USER_IMAGE, inboxModel.userImage)
        bundle.putString(Constants.OTHER_USER_NAME, inboxModel.senderName)
        navController.navigate(R.id.action_inboxFragment_to_chatFragment, bundle)
    }

    override fun fromChatFragmentToBuyCreditFragment() {
        navController.navigate(R.id.action_chatFragment_to_buyCreditFragment)
    }

    override fun fromProfileFragmentToEditProfileFragment() {
        navController.navigate(R.id.action_profileFragment_to_editProfileFragment)
    }

    override fun fromSettingsFragmentToHelpFragment() {
        navController.navigate(R.id.action_settingsFragment_to_helpFragment)
    }

    override fun fromSettingsFragmentToPrivacyPolicyFragment() {
        navController.navigate(R.id.action_settingsFragment_to_privacyPolicyFragment)
    }

    override fun fromSettingsFragmentToTermsOfUseFragment() {
        navController.navigate(R.id.action_settingsFragment_to_termsOfUseFragment)
    }


    override fun onStart() {
        super.onStart()
        userRepo.updateUserConnectionState(true, this, MyPreferences(this).getCurrentUser().userId)
    }

    override fun onStop() {
        if (MyPreferences(this).getCurrentUser().userId.isNotEmpty()) {
            userRepo.updateUserConnectionState(
                false,
                this,
                MyPreferences(this).getCurrentUser().userId
            )
        }
        super.onStop()
    }

    override fun onDestroy() {
        //userRepo.removeConnectionListener()
        super.onDestroy()
    }

}