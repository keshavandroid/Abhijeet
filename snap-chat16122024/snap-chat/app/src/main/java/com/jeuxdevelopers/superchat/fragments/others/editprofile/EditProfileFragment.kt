package com.jeuxdevelopers.superchat.fragments.others.editprofile

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.options
import com.google.android.material.chip.Chip
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.activities.MainActivity
import com.jeuxdevelopers.superchat.databinding.EditProfileFragmentBinding
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.fragments.auth.interests.InterestsFragment
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils

class EditProfileFragment : Fragment() {
    private lateinit var binding: EditProfileFragmentBinding
    private lateinit var viewModel: EditProfileViewModel
    private val selectedInterests: MutableList<String> = ArrayList()
    private lateinit var imageLauncher: ActivityResultLauncher<CropImageContractOptions>
    private lateinit var waitingDialog:WaitingDialog

    private var profileUri = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    fun init() {
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        viewModel.setUserModel(MyPreferences(requireContext()).getCurrentUser())
        waitingDialog = WaitingDialog(requireContext())
        initClickListeners()
        initObservers()
        initLaunchers()
        initViews()
    }


    private fun initLaunchers() {
        imageLauncher = this.registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uriContent = result.uriContent
                profileUri = uriContent.toString()
                binding.civProfile.setImageURI(uriContent)
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
                } else if (!validation.status && validation.inputView == null && validation.message!="Empty") {
                    Utils.showShortToast(requireContext(), validation.message)
                } else if (validation.status) {
                    viewModel.getUserModel().interestsList.clear()
                    viewModel.getUserModel().interestsList = selectedInterests
                    // check if profile photo need to add or not
                    if (profileUri.isNotEmpty()) {
                        viewModel.addUserProfileImage(profileUri.toUri())
                    } else {
                        viewModel.updateUser(requireContext())
                    }

                }
            }
        }

        viewModel.addProfileImage.observe(viewLifecycleOwner) { profileUrl ->
            if (profileUrl.isNotEmpty()) {
                viewModel.getUserModel().profileUrl = profileUrl
                viewModel.updateUser(requireContext())
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

        viewModel.isUpdateUser.observe(viewLifecycleOwner) { isAdd ->
            isAdd?.let {
                if (it) {
                    Utils.showShortToast(requireContext(), "Profile updated successfully")
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
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnDone.setOnClickListener {
            viewModel.validate(profileUri,binding.inputName,binding.autoCompleteGender,binding.inputGender,binding.inputAge,selectedInterests.size)
        }
        binding.btnPickProfile.setOnClickListener { imageLauncher.launch(options { }) }
    }

    private fun initViews() {
        val userModel = MyPreferences(requireContext()).getCurrentUser()
        Log.d(TAG, "initViews: user -> ${userModel.toString()}")
        selectedInterests.addAll(userModel.interestsList)
        Glide.with(requireContext()).load(userModel.profileUrl).into(binding.civProfile)
        binding.inputName.editText?.setText( userModel.name)
        binding.inputGender.editText?.setText( userModel.gender)
        binding.inputAge.editText?.setText(userModel.age.toString())
        initGenderAutoComplete()
        setInterestChipViews(Constants.getInterestsList())
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
                    Log.d(InterestsFragment.TAG, "setCategoryChips: Interest added => $text")
                } else {
                    if (selectedInterests.contains(text)) {
                        Log.d(InterestsFragment.TAG, "setCategoryChips: Interest removed => $text")
                        selectedInterests.remove(text)
                    }
                }
            }
            binding.chipGroup.addView(chipView)
        }
    }



    companion object {
        const val TAG = "EditProfileFragment"
    }
}