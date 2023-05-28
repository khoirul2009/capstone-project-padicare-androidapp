package com.padicare.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.padicare.R
import com.padicare.databinding.ActivityRegisterBinding
import com.padicare.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this
        )[RegisterViewModel::class.java]

        registerViewModel.errorMessage.observe(this, {message ->
            AlertDialog.Builder(this).apply {
                setTitle("Error!")
                setMessage(message)
                setNeutralButton("Ok", null)
                create()
                show()
            }
        })

        registerViewModel.message.observe(this, { message ->
            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage(message)
                setPositiveButton("Lanjut") { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                create()
                show()
            }
        })

        registerViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    private fun setupAction() {

        binding.btnRegister.setOnClickListener {
            val fullname = binding.inputFullname.text.toString()
            val email = binding.inputEmail.text.toString()
            val username = binding.inputUsername.text.toString()
            val password = binding.inputPassword.text.toString()

            resetError()
            when {
                email.isEmpty() -> {
                    binding.tfEmail.error = getString(R.string.email_required)
                }
                fullname.isEmpty() -> {
                    binding.tfFullname.error = getString(R.string.fullname_required)
                }
                username.isEmpty() -> {
                    binding.tfUsername.error = getString(R.string.username_required)
                }
                password.isEmpty() -> {
                    binding.tfPassword.error = getString(R.string.password_required)
                }
                password.length < 8 -> {
                    binding.tfPassword.error = getString(R.string.error_password_8)
                }
                else -> {
                    registerViewModel.register(fullname, email, username, password)

                }
            }
        }

        binding.toLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
    private fun resetError() {
        binding.tfEmail.error = null
        binding.tfFullname.error = null
        binding.tfUsername.error = null
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