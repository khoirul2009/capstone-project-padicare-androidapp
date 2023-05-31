package com.padicare.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.padicare.di.Injection
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.addPost.AddPostViewModel
import com.padicare.ui.forum.ForumViewModel
import com.padicare.ui.login.LoginViewModel
import com.padicare.ui.profile.ProfileViewModel

class ViewModelFactory(private val context: Context, private val pref: CredentialPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(pref) as T
            }
            modelClass.isAssignableFrom(ForumViewModel::class.java) -> {
                ForumViewModel(Injection.provideRepository()) as T
            }
            modelClass.isAssignableFrom(AddPostViewModel::class.java) -> {
                AddPostViewModel(pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}