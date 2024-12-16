package com.jeuxdevelopers.superchat.utils


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jeuxdevelopers.superchat.models.UserModel

class MyPreferences @SuppressLint("CommitPrefEdits") constructor(context: Context) {
    companion object {
        private const val KEY_CURRENT_USER = "KEY_CURRENT_USER"

        private const val PREF_NAME = "SUPER-CHAT"
    }

    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    init {
        preferences = context.getSharedPreferences(PREF_NAME, 0)
        editor = preferences!!.edit()
    }

    fun saveCurrentUser(userModel: UserModel?) {
        val gson = Gson()
        val json = gson.toJson(userModel)
        editor!!.putString(KEY_CURRENT_USER, json)
        editor!!.commit()
    }

    fun getCurrentUser(): UserModel {
        val gson = Gson()
        val json = preferences!!.getString(KEY_CURRENT_USER, null)
        return if (gson.fromJson(json, UserModel::class.java) != null) {
            gson.fromJson(json, UserModel::class.java)
        } else {
            UserModel()
        }
    }

}