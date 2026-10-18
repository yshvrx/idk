package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("productivity_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isOnboarded = MutableStateFlow(prefs.getBoolean(KEY_IS_ONBOARDED, false))
    val isOnboarded: StateFlow<Boolean> = _isOnboarded.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setUserName(name: String) {
        val trimmed = name.trim()
        prefs.edit().putString(KEY_USER_NAME, trimmed).apply()
        _userName.value = trimmed
    }

    fun completeOnboarding(name: String) {
        val trimmed = name.trim()
        prefs.edit()
            .putString(KEY_USER_NAME, trimmed)
            .putBoolean(KEY_IS_ONBOARDED, true)
            .apply()
        _userName.value = trimmed
        _isOnboarded.value = true
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
    }

    fun isInitialSeedDone(): Boolean {
        return prefs.getBoolean(KEY_INITIAL_SEED_DONE, false)
    }

    fun setInitialSeedDone() {
        prefs.edit().putBoolean(KEY_INITIAL_SEED_DONE, true).apply()
    }

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_IS_ONBOARDED = "key_is_onboarded"
        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_INITIAL_SEED_DONE = "key_initial_seed_done"
    }
}
