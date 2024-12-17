package com.jeuxdevelopers.superchat.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.SetPriceDialogBinding
import com.jeuxdevelopers.superchat.models.UserModel import java.util.concurrent.atomic.DoubleAccumulator

class SetPriceDialog(context: Context, private val userModel: UserModel) : Dialog(context) {
    companion object {
        const val TAG = "SetPriceDialog"
    }

    private var listener: SetPriceListener? = null
    private lateinit var binding: SetPriceDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.set_price_dialog,
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
        initViews()
        initClickListeners()
    }

    private fun initViews() {

        binding.etImageAmount.setText(userModel.pricePerImage.toString())

        binding.etCharacterAmount.setText(userModel.pricePerCharacter.toString())

    }

    private fun initClickListeners() {

        binding.btnSave.setOnClickListener {

            val imageAmount = binding.etImageAmount.text.toString().trim()
            val characterAmount = binding.etCharacterAmount.text.toString().trim()


            if (characterAmount.isEmpty() || characterAmount.toDoubleOrNull() == null) {
                Log.d(TAG, "initClickListeners: Please enter  character amount")
                binding.etCharacterAmount.error = "Please enter character amount"
            } else if (characterAmount.toDouble() <= 0) {
                Log.d(TAG, "initClickListeners: Please enter valid  character amount")
                binding.etCharacterAmount.error = "Please enter valid character amount"
            } else if (imageAmount.isEmpty() || imageAmount.toDoubleOrNull() == null) {
                Log.d(TAG, "initClickListeners: Please enter image amount")
                binding.etImageAmount.error = "Please enter image amount"
            } else if (imageAmount.toDouble() <= 0) {
                Log.d(TAG, "initClickListeners: Please enter valid image amount")
                binding.etImageAmount.error = "Please enter valid image amount"
            } else {
                Log.d(
                    TAG,
                    "initClickListeners: valid img ->${imageAmount.toDouble()} char->${characterAmount.toDouble()}"
                )
                if (listener!=null){
                    listener!!.onSaveClick(imageAmount.toDouble(),characterAmount.toDouble())
                }
                dismiss()
            }
        }

        binding.btnClose.setOnClickListener {
            if (listener != null) {
                listener!!.onCloseClick()
            }
            dismiss()
        }
    }

    fun setPriceListener(listener: SetPriceListener) {
        this.listener = listener
    }

    interface SetPriceListener {

        fun onCloseClick()

        fun onSaveClick(pricePerImage: Double, pricePerCharacter: Double)
    }
}