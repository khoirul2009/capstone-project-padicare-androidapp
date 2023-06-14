package com.padicare.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.padicare.di.Injection
import com.padicare.repository.CredentialPreferences
import com.padicare.repository.ThemePreference
import com.padicare.ui.addPost.AddPostViewModel
import com.padicare.ui.forum.ForumViewModel
import com.padicare.ui.home.HomeViewModel
import com.padicare.ui.launchScreen.LaunchViewModel
import com.padicare.ui.login.LoginViewModel
import com.padicare.ui.post.PostViewModel
import com.padicare.ui.profile.ProfileViewModel
import com.padicare.ui.search.SearchViewModel

class ViewModelFactory(private val context: Context, private val pref: CredentialPreferences? = null, private val themePref: ThemePreference? = null) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(pref!!) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref!!) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(pref!!, themePref!!) as T
            }
            modelClass.isAssignableFrom(ForumViewModel::class.java) -> {
                ForumViewModel(Injection.provideRepository()) as T
            }
            modelClass.isAssignableFrom(AddPostViewModel::class.java) -> {
                AddPostViewModel(pref!!) as T
            }
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(Injection.provideRepository2(), pref!!) as T
            }
            modelClass.isAssignableFrom(LaunchViewModel::class.java) -> {
                LaunchViewModel(themePref!!) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(Injection.provideRepository()) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}