package com.example.dicoding_events.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicoding_events.data.preference.SettingPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val pref: SettingPreferences
) : ViewModel() {

    fun getThemeSettings(): Flow<Boolean> = pref.getThemeSetting()

    fun getReminderSettings(): Flow<Boolean> = pref.getReminderSetting()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isReminderActive)
        }
    }
}

