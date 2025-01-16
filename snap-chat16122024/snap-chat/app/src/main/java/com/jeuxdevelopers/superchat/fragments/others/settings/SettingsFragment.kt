package com.jeuxdevelopers.superchat.fragments.others.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jeuxdevelopers.superchat.activities.AuthenticationActivity
import com.jeuxdevelopers.superchat.databinding.FragmentSettingsBinding
import com.jeuxdevelopers.superchat.dialogs.SetPriceDialog
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.enums.PaymentType
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.repositories.AuthRepository
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils


class SettingsFragment : Fragment() {

    private lateinit var waitingDialog: WaitingDialog

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var viewModel: SettingsViewModel

    private lateinit var navigationListener: MainNavigationListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {

        waitingDialog = WaitingDialog(requireContext())

        navigationListener = requireContext() as MainNavigationListener

        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        initClickListeners()
    }


    private fun initViews() {
        val user = MyPreferences(requireContext()).getCurrentUser()
        when (user.paymentMethod.paymentType) {
            PaymentType.NONE -> {
                binding.tvPaymentMethod.text = Constants.UNAVAILABLE
            }
            PaymentType.PAYPAL -> {
                binding.tvPaymentMethod.text = Constants.getPaymentTypesList()[0]
            }
            PaymentType.BANK_TRANSFER -> {
                binding.tvPaymentMethod.text = Constants.getPaymentTypesList()[1]
            }
        }

        val userBalance = "${String.format("%.2f",user.balance)}$"
        binding.tvUserCoins.text = userBalance
        binding.tvNameLogout.text = user.name

        val userCredit = "${String.format("%.2f",user.credit)}$"
        binding.tvCredit.text = userCredit
    }

    private fun initClickListeners() {

        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnPaymentMethod.setOnClickListener { navigationListener.fromSettingsFragmentToSetPaymentFragment() }
        binding.tvBuyCredits.setOnClickListener { navigationListener.fromSettingsFragmentToBuyCreditFragment() }
        binding.btnBuyCredit.setOnClickListener { binding.tvBuyCredits.performClick() }
        binding.tvTitleBuy.setOnClickListener { binding.tvBuyCredits.performClick() }
        binding.tvLogout.setOnClickListener {
            //UsersRepository().removeConnectionListener()
            UsersRepository().updateUserConnectionState(
                false,
                requireContext(),
                MyPreferences(requireContext()).getCurrentUser().userId
            )
            Firebase.auth.signOut()
            MyPreferences(requireContext()).saveCurrentUser(UserModel())
            startActivity(Intent(requireActivity(), AuthenticationActivity::class.java))
            requireActivity().finishAffinity()
        }
        binding.tvNameLogout.setOnClickListener { binding.tvLogout.performClick() }

        binding.setPriceText.setOnClickListener { showSetPriceDialog() }

        binding.tvHelp.setOnClickListener { navigationListener.fromSettingsFragmentToHelpFragment() }
        binding.tvHelp2.setOnClickListener { binding.tvHelp.performClick() }

        binding.tvPrivacy.setOnClickListener { navigationListener.fromSettingsFragmentToPrivacyPolicyFragment() }
        binding.tvTermsOfUse.setOnClickListener { navigationListener.fromSettingsFragmentToTermsOfUseFragment() }
    }

    private fun showSetPriceDialog() {

        val userModel = viewModel.getCurrentUser()

        val priceDialog = SetPriceDialog(requireContext(), userModel)

        priceDialog.setPriceListener(object : SetPriceDialog.SetPriceListener {

            override fun onSaveClick(pricePerImage: Double, pricePerCharacter: Double) {

                saveUserPrice(pricePerImage, pricePerCharacter)
            }

            override fun onCloseClick() {

            }

        })

        priceDialog.show()

    }

    private fun saveUserPrice(pricePerImage: Double, pricePerCharacter: Double) {

        waitingDialog.show("Updating Prices")

        viewModel.saveUserPrice(pricePerImage, pricePerCharacter).observe(viewLifecycleOwner, {

            if (it) {

                Utils.showShortToast(requireContext(), "Prices Updated Successfully")

            } else {

                Utils.showShortToast(requireContext(), "Couldn't Update, Please Tray Again")

            }

            waitingDialog.dismiss()

        })

    }

    companion object {
        const val TAG = "SettingsFragment"
    }

    override fun onResume() {
        super.onResume()
        initViews()
    }
}