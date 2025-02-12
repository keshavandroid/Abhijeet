package com.jeuxdevelopers.superchat.fragments.main

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.FragmentProfileBinding
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var navigationListener: MainNavigationListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        navigationListener = requireContext() as MainNavigationListener
        initClickListeners()
        initViews()
    }

    private fun initClickListeners() {
        binding.btnSettings.setOnClickListener { navigationListener.fromProfileFragmentToSettingsFragment() }
        binding.btnEdit.setOnClickListener { navigationListener.fromProfileFragmentToEditProfileFragment() }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        val userModel = MyPreferences(requireContext()).getCurrentUser()
        binding.tvUserName.text = userModel.name
        binding.tvNickName.text = "@${userModel.name}"
        binding.tvGender.text = "Gender : ${userModel.gender}"
       // binding.tvAge.text = "Age : ${userModel.age}"
        binding.tvStatus.text = "(${userModel.userState.name.lowercase()})"
        binding.tvJoinDate.text = Utils.getDateFromMillis(userModel.joinDate)
        Glide.with(requireContext()).load(userModel.profileUrl)
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pbLoading.visibility = View.GONE
                    binding.imgProfile.setImageResource(R.drawable.ic_placeholder)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.pbLoading.visibility = View.GONE
                    return false
                }

            }).into(binding.imgProfile)
    }


    companion object {
        const val TAG = "ProfileFragment"
    }
}