package com.jeuxdevelopers.superchat.fragments.auth.login

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
import com.jeuxdevelopers.superchat.databinding.FragmentLoginBinding
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.interfaces.AuthNavigationListener
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils
import kotlinx.coroutines.ExperimentalCoroutinesApi


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navigationListener: AuthNavigationListener
    private lateinit var viewModel: LoginViewModel
    private lateinit var waitingDialog: WaitingDialog

    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        navigationListener = requireContext() as AuthNavigationListener
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        waitingDialog = WaitingDialog(requireContext())
        initLaunchers()
        initClickListeners()
        initObservers()
    }

    private fun initLaunchers() {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                viewModel.loginWithGoogle(account)

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }

        }.also { googleSignInLauncher = it }
    }

    private fun initObservers() {
        viewModel.isWaiting.observe(viewLifecycleOwner) { waitState ->
            waitState?.let {
                if (it.status) {
                    waitingDialog.show(it.message)
                } else {
                    waitingDialog.dismiss()
                }
            }
        }
        viewModel.isValidated.observe(viewLifecycleOwner) { validation ->
            validation?.let {
                if (!it.status) {
                    it.inputView?.error = it.message
                } else {
                    viewModel.loginUserWithEmailAndPassword()
                }

            }
        }
        viewModel.loginResult.observe(viewLifecycleOwner) { userModel ->
            userModel?.let {
                if (it.userId.isEmpty()) {
                    Utils.showShortToast(
                        requireContext(),
                        "Unable to login, please check your credentials"
                    )
                    Log.d(TAG, "initObservers: Unable to login, please check your credentials")
                } else if (userModel.userId!= "dum") {
                    MyPreferences(requireContext()).saveCurrentUser(userModel)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                }
            }
        }

        // google login new user listener
        viewModel.isNewUser.observe(viewLifecycleOwner) { userModel ->
            if (userModel.name.isNotEmpty())
                navigationListener.fromLoginFragmentToInterestsFragment(userModel)
        }
    }

    private fun initClickListeners() {
        binding.btnLogin.setOnClickListener {
            viewModel.validateInputs(binding.inputEmail, binding.inputPassword)
        }
        binding.tvGoSignUp.setOnClickListener { navigationListener.fromLoginFragmentToSignUpFragment() }

        binding.btnGoogleLogin.setOnClickListener {
            googleSignInLauncher.launch(viewModel.getGoogleSignInClient(requireContext()).signInIntent)
        }
    }

    companion object {
        const val TAG = "LoginFragment"
    }
}