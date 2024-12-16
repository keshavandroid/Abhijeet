package com.jeuxdevelopers.superchat.fragments.others

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jeuxdevelopers.superchat.databinding.FragmentOtherProfileBinding
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.Utils


class OtherProfileFragment : Fragment() {
    private lateinit var binding: FragmentOtherProfileBinding
    private lateinit var userModel: UserModel
    private lateinit var navigationListener: MainNavigationListener
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOtherProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        userModel = arguments?.getSerializable(Constants.USER_MODEL) as UserModel
        navigationListener = requireContext() as MainNavigationListener
        binding.userModel = userModel
        initViews()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnMessage.setOnClickListener {navigationListener.fromOtherProfileFragmentToChatFragment(userModel) }
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        binding.tvGender.text = "Gender : ${userModel.gender}"
        binding.tvAge.text = "Age : ${userModel.age}"
        binding.tvStatus.text = "(${userModel.userState.name.lowercase()})"
        binding.tvJoinDate.text = Utils.getDateFromMillis(userModel.joinDate)
        Glide.with(requireContext()).load(userModel.profileUrl).into(binding.imgProfile)
    }

    companion object {
        const val TAG = "OtherProfileFragment"
    }
}