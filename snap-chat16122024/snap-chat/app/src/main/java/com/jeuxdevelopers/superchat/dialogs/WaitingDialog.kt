package com.jeuxdevelopers.superchat.dialogs


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.jeuxdevelopers.superchat.R
import com.jeuxdevelopers.superchat.databinding.DialogWaitingBinding
import java.util.Objects

class WaitingDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogWaitingBinding
    private var status: String? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_waiting,
            null,
            false
        )
        setContentView(binding.root)
        Objects.requireNonNull(window)?.setBackgroundDrawable(
            ColorDrawable(
                context.resources.getColor(
                    android.R.color.transparent,
                    null,
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
        if (status != null) {
            binding.tvStatusLoading.text = status
        }
    }


    fun show(status: String?) {
        this.status = status
        show()
    }

    fun setStatusTextView(status: String?) {
        binding.tvStatusLoading.text = status
    }

}