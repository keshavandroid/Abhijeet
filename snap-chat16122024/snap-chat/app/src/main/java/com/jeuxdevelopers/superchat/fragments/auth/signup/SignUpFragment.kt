package com.jeuxdevelopers.superchat.fragments.auth.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.jeuxdevelopers.superchat.activities.MainActivity
import com.jeuxdevelopers.superchat.utils.Utils
import com.jeuxdevelopers.superchat.databinding.FragmentSignUpBinding
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.fragments.auth.login.LoginFragment
import com.jeuxdevelopers.superchat.interfaces.AuthNavigationListener
import com.jeuxdevelopers.superchat.models.UserModel


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navigationListener: AuthNavigationListener
    private lateinit var viewModel: SignUpViewModel
    private lateinit var waitingDialog: WaitingDialog

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }


    private fun init() {
        navigationListener = requireContext() as AuthNavigationListener
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        waitingDialog = WaitingDialog(requireContext())
        initClickListeners()
        initLaunchers()
        initObservers()
    }

    private fun initLaunchers() {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(LoginFragment.TAG, "firebaseAuthWithGoogle:" + account.id)
                viewModel.signUpWithGoogle(account)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(LoginFragment.TAG, "Google sign in failed", e)
            }

        }.also { googleSignInLauncher = it }
    }

    private fun initObservers() {
        viewModel.isValidated.observe(viewLifecycleOwner) { validation ->
            validation?.let {
                if (!it.status) {
                    it.inputView?.error = it.message
                } else {
                    viewModel.createUserWithEmailAndPassword()
                }

            }
        }

        viewModel.signUpError.observe(viewLifecycleOwner) { message ->
            Utils.showShortToast(requireContext(), message)
        }

        viewModel.signUpResults.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.userId.isNotEmpty()){
                    if (it.gender.isNotEmpty()){
                        startActivity(Intent(requireActivity(),MainActivity::class.java))
                        requireActivity().finishAffinity()
                    }else{
                        navigationListener.fromSignUpFragmentToInterestsFragment(it)
                    }

                }
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
    }

    private fun initClickListeners() {
        binding.tvbackToSignIn.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnCreate.setOnClickListener {
            viewModel.validateInputs(binding.inputName, binding.inputEmail, binding.inputPassword)
        }

        binding.btnGoogleLogin.setOnClickListener {
            googleSignInLauncher.launch(viewModel.getGoogleSignInClient(requireContext()).signInIntent)
        }

    }


    companion object {
        const val TAG = "SignUpFragment"
    }
}