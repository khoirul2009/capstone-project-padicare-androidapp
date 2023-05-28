package com.padicare.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.padicare.MainActivity
import com.padicare.R
import com.padicare.databinding.ActivityLoginBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory
import com.padicare.ui.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupViewModel()
        setupAction()

    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, CredentialPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.errorMessage.observe(this, { message ->
            AlertDialog.Builder(this).apply {
                setTitle("Error!")
                setMessage(message)
                setNeutralButton("Ok", null)
                create()
                show()
            }
        })

        loginViewModel.getUser().observe(this, { user ->
            if(user.isLogin == true) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        })
        loginViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    private fun setupAction() {

        binding.btnLogin.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()
            resetError()
            when {
                email.isEmpty() -> {
                    binding.tfEmail.error = getString(R.string.email_required)
                }
                password.isEmpty() -> {
                    binding.tfPassword.error = getString(R.string.password_required)
                }
                password.length < 8 -> {
                    binding.tfPassword.error = getString(R.string.error_password_8)
                }
                else -> {
                    // login
                    loginViewModel.login(email, password)
                }
            }
        }

        binding.toRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun resetError() {
        binding.tfEmail.error = null
        binding.tfPassword.error = null
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}