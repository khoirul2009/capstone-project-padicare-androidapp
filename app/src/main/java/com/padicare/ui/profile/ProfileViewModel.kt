package com.padicare.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padicare.repository.CredentialPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(private val pref: CredentialPreferences) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            pref.clearCredential()
        }
    }
}