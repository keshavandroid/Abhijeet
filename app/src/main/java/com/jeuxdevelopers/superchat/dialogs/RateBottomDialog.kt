package com.jeuxdevelopers.superchat.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.BottomDialogRateBinding
import com.jeuxdevelopers.superchat.models.RateModel
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import kotlinx.coroutines.*
import java.util.*

class RateBottomDialog(context: Context, private val otherUserId: String) : Dialog(context) {
    private var listener: RatesListener? = null
    private lateinit var binding: BottomDialogRateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_dialog_rate,
            null,
            false
        )
        setContentView(binding.root)
        Objects.requireNonNull(window)?.setBackgroundDrawable(
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
        initViews()
        initClickListeners()
    }

    private fun initViews() {
       val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
         val user =  UsersRepository().getUserFromDatabaseById(otherUserId)
            val imageCost = "${String.format("%.2f", user.pricePerCharacter)}$ per image"
            val textCost = "${String.format("%.2f", user.pricePerImage)}$ per character"
            binding.characterAmountText.text = textCost
            binding.imageAmountText.text = imageCost
        }



    }

    private fun initClickListeners() {
        binding.btnAgree.setOnClickListener {
            if (listener != null) {
                listener!!.onAgreeClick()
            }
            dismiss()
        }
        binding.btnClose.setOnClickListener {
            if (listener != null) {
                listener!!.onCloseClick()
            }
            dismiss()
        }
        binding.buyBalanceText.setOnClickListener {
            if (listener!=null){
                listener!!.onBuyBalanceClick()
            }
            dismiss()
        }
    }

    fun setOnRatesListener(listener: RatesListener) {
        this.listener = listener
    }

    interface RatesListener {
        fun onAgreeClick()
        fun onCloseClick()
        fun onBuyBalanceClick()
    }
}