package com.padicare.ui.launchScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.padicare.MainActivity
import com.padicare.databinding.ActivityLaunchBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.repository.ThemePreference
import com.padicare.ui.ViewModelFactory
import com.padicare.ui.login.LoginActivity
import com.padicare.ui.login.LoginViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var launchViewModel: LaunchViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "themes")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupViewModel()

        Handler(Looper.getMainLooper()).postDelayed({
        loginViewModel.getUser().observe(this, {
            if(it.isLogin == true) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        })
        }, 2000)

        launchViewModel.getThemeSettings().observe(this, { isDarkModeActive: Boolean ->
                if(isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        )
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, CredentialPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]

        launchViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, themePref = ThemePreference.getInstance(dataStore))
        )[LaunchViewModel::class.java]
    }
}

