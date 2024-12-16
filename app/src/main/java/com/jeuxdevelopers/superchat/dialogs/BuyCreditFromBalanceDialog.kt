package com.jeuxdevelopers.superchat.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.DialogBuyCreditFromBalanceBinding
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils

class BuyCreditFromBalanceDialog(context: Context) : Dialog(context) {
    companion object{
        const val TAG = "BuyFromBalanceDialog"
    }
    private var listener: BalanceSelectListener? = null
    private lateinit var binding: DialogBuyCreditFromBalanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_buy_credit_from_balance,
            null,
            false
        )
        setContentView(binding.root)

        window?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    context,
                    android.R.color.transparent
                )
            )
        )
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        init()
    }

    private fun init() {
      initClickListeners()
    }

    private fun initClickListeners() {
        val userModel = MyPreferences(context).getCurrentUser()
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnAdd.setOnClickListener {
           val amount = binding.etAmount.text.toString().trim()

            if (amount.isEmpty() || amount.toDoubleOrNull() == null){
                Log.d(TAG, "initClickListeners: please enter amount")
                binding.etAmount.error = "Please enter amount"
            }else if (amount.toDouble() <=0){
                Log.d(TAG, "initClickListeners: please enter valid amount")
                binding.etAmount.error = "Please enter valid amount"
            }else if (amount.toDouble()> userModel.balance){
                Log.d(TAG, "initClickListeners: You don't have enough balance")
                binding.etAmount.error = "You don't have enough balance"
                Utils.showShortToast(context,"You don't have enough balance")
            }else{
                if (listener!=null){
                    listener!!.onBalanceSelect(amount.toDouble())
                }
                dismiss()
            }
        }
    }

    fun setOnBalanceSelectListener(listener: BalanceSelectListener) {
        this.listener = listener
    }

    interface BalanceSelectListener {
        fun onBalanceSelect(amount: Double)
    }
}