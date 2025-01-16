package com.jeuxdevelopers.superchat.fragments.others.setPayment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.FragmentSetPaymentBinding
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.enums.PaymentType
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils


class SetPaymentFragment : Fragment() {
    private lateinit var binding: FragmentSetPaymentBinding
    private lateinit var viewModel: SetPaymentViewModel
    private var paymentType = PaymentType.NONE
    private lateinit var waitingDialog: WaitingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetPaymentBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(SetPaymentViewModel::class.java)
        waitingDialog = WaitingDialog(requireContext())
        initTypeSpinner()
        initSpinnerListener()
        initClickListeners()
        initViews()
        initObservers()
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
                if (!validation.status && validation.inputView != null) {
                    validation.inputView!!.error = validation.message
                } else if (!validation.status && validation.inputView == null) {
                    Utils.showShortToast(requireContext(), validation.message)
                } else if (validation.status) {
                    viewModel.setPaymentMethod(MyPreferences(requireContext()).getCurrentUser().userId,requireContext())
                }
            }
        }

        viewModel.isAddPayMethod.observe(viewLifecycleOwner) { isSet ->
            isSet?.let {
                if (it) {
                    Utils.showShortToast(requireContext(), "Payment method updated successfully")
                    requireActivity().onBackPressed()
                } else {
                    Utils.showShortToast(requireContext(), "Unable to set payment method")
                }
            }
        }
    }

    private fun initViews() {
        val paymentMethod = MyPreferences(requireContext()).getCurrentUser().paymentMethod
        binding.edPaymentID.setText(paymentMethod.paymentId)
        binding.edPhone.setText(paymentMethod.phoneNumber)
        if (paymentMethod.paymentType == PaymentType.BANK_TRANSFER) {
            binding.spinnerPaymentMethod.setSelection(1)
        } else {
            binding.spinnerPaymentMethod.setSelection(0)
        }
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.rlSpinner.setOnClickListener { binding.spinnerPaymentMethod.performClick() }
        binding.btnSave.setOnClickListener {
            viewModel.validate(
                binding.edPaymentID,
                binding.edPhone,
                paymentType
            )
        }
    }

    private fun initTypeSpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, Constants.getPaymentTypesList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
        binding.spinnerPaymentMethod.adapter = adapter
    }

    private fun initSpinnerListener() {
        binding.spinnerPaymentMethod.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    if (position == 0) {
                        paymentType = PaymentType.PAYPAL
                        binding.tvInputType.text = "Your Paypal ID"
                    } else {
                        paymentType = PaymentType.BANK_TRANSFER
                        binding.tvInputType.text = "Your IBAN"
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    companion object {
        const val TAG = "SetPaymentFragment"
    }
}