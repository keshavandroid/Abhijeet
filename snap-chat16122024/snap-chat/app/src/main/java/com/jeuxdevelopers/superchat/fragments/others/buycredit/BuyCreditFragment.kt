package com.jeuxdevelopers.superchat.fragments.others.buycredit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.FragmentBuyCreditBinding
import com.jeuxdevelopers.superchat.dialogs.BuyCreditFromBalanceDialog
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.enums.CreditPurchase
import com.jeuxdevelopers.superchat.utils.Utils


class BuyCreditFragment : Fragment() {

    private var selectedCard = 1

    private lateinit var viewModel: BuyCreditViewModel
    private lateinit var waitingDialog: WaitingDialog
    private lateinit var binding: FragmentBuyCreditBinding
    private lateinit var creditPurchase: CreditPurchase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBuyCreditBinding
            .inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {

        viewModel = ViewModelProvider(this)
            .get(BuyCreditViewModel::class.java)
        viewModel.startBillingConnection()
        creditPurchase = CreditPurchase.idle
        waitingDialog = WaitingDialog(requireContext())

        initViews()

        initClickListeners()

    }

    private fun initClickListeners() {

        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }

        binding.firstAmountCard.setOnClickListener {
            selectFirstCard()
            creditPurchase = CreditPurchase.purchase_thirty
        }

        binding.secondAmountCard.setOnClickListener {
            selectSecondCard()
            creditPurchase = CreditPurchase.purchase_seventy
        }

        binding.thirdAmountCard.setOnClickListener {
            selectThirdCard()
            creditPurchase = CreditPurchase.purchase_seventy
        }

        binding.btnBuy.setOnClickListener {

            when (selectedCard) {
                1 -> {
                    addCreditAmountToUser(30.0, false)
                }

                2 -> {

                    addCreditAmountToUser(70.0, false)
                }

                else -> {

                    addCreditAmountToUser(120.0, false)

                }
            }

        }

        binding.btnBuyFromBalance.setOnClickListener {
            val dialog = BuyCreditFromBalanceDialog(requireContext())
            dialog.setOnBalanceSelectListener(object :
                BuyCreditFromBalanceDialog.BalanceSelectListener {
                override fun onBalanceSelect(amount: Double) {
                    addCreditAmountToUser(amount, true)
                }
            })
            dialog.show()
        }

    }

    private fun initViews() {

        viewModel.getCurrentCredit().observe(viewLifecycleOwner, {

            val credit = "$it $"

            binding.tvCurrentCredit.text = credit

        })


    }

    private fun selectFirstCard() {

        selectedCard = 1

        binding.firstAmountCard.setCardBackgroundColor(
            ContextCompat
                .getColor(requireContext(), R.color.colorYellow)
        )

        binding.firstAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

        binding.secondAmountCard.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

        binding.secondAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

        binding.thirdAmountCard.setCardBackgroundColor(
            ContextCompat
                .getColor(requireContext(), R.color.colorWhite)
        )

        binding.thirdAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

    }

    private fun selectSecondCard() {

        selectedCard = 2

        binding.firstAmountCard.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

        binding.firstAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

        binding.secondAmountCard.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorYellow
            )
        )

        binding.secondAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

        binding.thirdAmountCard.setCardBackgroundColor(
            ContextCompat
                .getColor(requireContext(), R.color.colorWhite)
        )

        binding.thirdAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

    }

    private fun selectThirdCard() {

        selectedCard = 3

        binding.firstAmountCard.setCardBackgroundColor(
            ContextCompat
                .getColor(requireContext(), R.color.colorWhite)
        )

        binding.firstAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

        binding.secondAmountCard.setCardBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

        binding.secondAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorBlack
            )
        )

        binding.thirdAmountCard.setCardBackgroundColor(
            ContextCompat
                .getColor(requireContext(), R.color.colorYellow)
        )

        binding.thirdAmountText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorWhite
            )
        )

    }

    private fun addCreditAmountToUser(amount: Double, isFromBalance: Boolean) {
//        if (creditPurchase == CreditPurchase.idle){
//            Log.d(TAG, "addCreditAmountToUser: please select some purchase")
//            Utils.showShortToast(requireContext(),"please select some purchase")
//        }else{
//            viewModel.purchaseWithInAppPurchase(creditPurchase,requireActivity())
//        }

// Dummy add credit directly
        waitingDialog.show("Buying Credits")

        viewModel.addCreditToUser(amount, isFromBalance).observe(viewLifecycleOwner, {

            if (it) {

                Utils.showShortToast(requireContext(), "Credit Added Successfully")

            } else {

                Utils.showShortToast(requireContext(), "Couldn't Add, Please Try Again")

            }

            waitingDialog.dismiss()

        })

    }

    companion object {
        const val TAG = "BuyCreditFragment"
    }
}