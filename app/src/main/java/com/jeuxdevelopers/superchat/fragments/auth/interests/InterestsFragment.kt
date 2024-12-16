package com.jeuxdevelopers.superchat.fragments.auth.interests


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jeuxdevelopers.superchat.databinding.FragmentInterestsBinding


import android.widget.ArrayAdapter
import android.util.Log

import android.util.TypedValue

import android.widget.CompoundButton
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.options

import com.google.android.material.chip.Chip
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.activities.MainActivity
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils


class InterestsFragment : Fragment() {
    private lateinit var binding: FragmentInterestsBinding
    private val selectedInterests: MutableList<String> = ArrayList()
    private lateinit var viewModel: InterestsViewModel
    private lateinit var waitingDialog: WaitingDialog
    private lateinit var userModel: UserModel

    private lateinit var imageLauncher: ActivityResultLauncher<CropImageContractOptions>

    private var profileUri = ""
    private var isLocalProfile = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInterestsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(InterestsViewModel::class.java)
        waitingDialog = WaitingDialog(requireContext())
        userModel = arguments?.getSerializable(Constants.USER_MODEL) as UserModel
        profileUri = userModel.profileUrl
        initGenderAutoComplete()
        initViews()
        initClickListeners()
        initLaunchers()
        initObservers()
    }


    private fun initLaunchers() {
        imageLauncher = this.registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uriContent = result.uriContent
                profileUri = uriContent.toString()
                binding.civProfile.setImageURI(uriContent)
                isLocalProfile = true
            } else {
                // an error occurred
                //val exception = result.error
                Utils.showShortToast(
                    requireContext(),
                    "Something went wrong!${result.error?.message}"
                )
            }
        }
    }

    private fun initObservers() {
        viewModel.isValidated.observe(viewLifecycleOwner) { validation ->
            validation?.let {
                if (!validation.status && validation.inputView != null) {
                    validation.inputView!!.error = validation.message
                } else if (!validation.status && validation.inputView == null) {
                    Utils.showShortToast(requireContext(), validation.message)
                } else if (validation.status) {
                    userModel.interestsList = selectedInterests
                    // check if profile photo need to add or not
                    if (isLocalProfile) {
                        viewModel.addUserProfileImage(profileUri.toUri())
                    } else {
                        viewModel.addNewUser(userModel, requireContext())
                    }

                }
            }
        }

        viewModel.addProfileImage.observe(viewLifecycleOwner) { profileUrl ->
            if (profileUrl.isNotEmpty()) {
                userModel.profileUrl = profileUrl
                viewModel.addNewUser(userModel, requireContext())
            } else {
                Utils.showShortToast(requireContext(), "Unable to upload image")
            }
        }

        viewModel.isWaiting.observe(viewLifecycleOwner) { waitState ->
            waitState?.let {
                if (it.status) {
                    waitingDialog.show(it.message)
                } else {
                    waitingDialog.dismiss()
                }
            }
        }
        viewModel.isAddNewUser.observe(viewLifecycleOwner) { isAdd ->
            isAdd?.let {
                if (it) {
                    Utils.showShortToast(requireContext(), "User added successfully")
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                } else {
                    Utils.showShortToast(
                        requireContext(),
                        "Unable to add user, please check your internet connection"
                    )
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.btnPickProfile.setOnClickListener { imageLauncher.launch(options { }) }
        binding.btnDone.setOnClickListener {
            viewModel.validate(
                profileUri,
                binding.autoCompleteGender,
                binding.inputGender,
                binding.inputAge,
                selectedInterests.size
            )
        }
    }

    private fun initViews() {
        binding.inputJoinDate.editText?.setText(Utils.getDateFromMillis(System.currentTimeMillis()))
        setInterestChipViews(Constants.getInterestsList())
        if (userModel.profileUrl.isNotEmpty()) {
            Glide.with(requireContext()).load(userModel.profileUrl).into(binding.civProfile)
        }
    }

    private fun initGenderAutoComplete() {
        binding.autoCompleteGender.threshold = 0
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Constants.getGendersList()
            )
        binding.autoCompleteGender.setAdapter(adapter)
        adapter.setNotifyOnChange(true)
    }

    private fun setInterestChipViews(interests: List<String?>) {
        for (interest in interests) {
            val chipView = this.layoutInflater
                .inflate(R.layout.item_interest, null, false) as Chip
            chipView.text = interest
            val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
            ).toInt()
            chipView.setPadding(paddingDp, 0, paddingDp, 0)
            if (selectedInterests.contains(interest)) {
                chipView.isChecked = true
            }
            chipView.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
                val text = compoundButton.text.toString()
                if (b) {
                    if (selectedInterests.size <= 9) {
                        selectedInterests.add(text)
                    } else {
                        chipView.isChecked = false
                    }
                    Log.d(TAG, "setCategoryChips: Interest added => $text")
                } else {
                    if (selectedInterests.contains(text)) {
                        Log.d(TAG, "setCategoryChips: Interest removed => $text")
                        selectedInterests.remove(text)
                    }
                }
            }
            binding.chipGroup.addView(chipView)
        }
    }


    companion object {
        const val TAG = "InterestsFragment"
    }
}