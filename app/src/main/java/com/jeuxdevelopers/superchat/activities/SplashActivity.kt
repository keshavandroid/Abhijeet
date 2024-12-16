package com.jeuxdevelopers.superchat.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.core.ActivityScope
import com.google.firebase.ktx.Firebase
import com.jeuxdevelopers.superchat.databinding.ActivitySplashBinding
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (window != null && window.insetsController != null) {
                window.insetsController!!.hide(WindowInsets.Type.statusBars())
            }
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        init()
    }

    private fun init() {
        EmojiManager.install(IosEmojiProvider())
        activityScope.launch {
            delay(3000)
            when {
                MyPreferences(this@SplashActivity).getCurrentUser().userId.isNotEmpty() && Firebase.auth.uid != null -> {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finishAffinity()
                }
                else -> {
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            AuthenticationActivity::class.java
                        )
                    )
                    finishAffinity()
                }
            }
        }
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}