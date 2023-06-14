package com.padicare.ui.launchScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.padicare.repository.ThemePreference

class LaunchViewModel(private val preference: ThemePreference) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return preference.getThemeSetting().asLiveData()
    }

}