package com.dicoding.picodiploma.loginwithanimation.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private val _isDarkMode = MutableLiveData<Boolean>()
    val isDarkMode: LiveData<Boolean> get() = _isDarkMode

    init {
        _isDarkMode.value = sharedPreferences.getBoolean("DARK_MODE", false)
    }

    fun setDarkMode(isDarkMode: Boolean) {
        sharedPreferences.edit().putBoolean("DARK_MODE", isDarkMode).apply()
        _isDarkMode.value = isDarkMode
    }
}
