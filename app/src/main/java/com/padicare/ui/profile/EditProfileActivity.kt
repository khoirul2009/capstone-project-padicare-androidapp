package com.padicare.ui.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.padicare.R
import com.padicare.databinding.ActivityEditProfileBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupViewModel()
        setupField()


        binding.btnEditProfile.setOnClickListener() {
            submitEdit()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, CredentialPreferences.getInstance(dataStore))
        )[ProfileViewModel::class.java]
        intent.getStringExtra("userId")?.let {
            viewModel.getUserFromApi(it)

        }

        viewModel.getUser().observe(this, {
            this.token = it.token
        })
        viewModel.isLoading.observe(this, {
            showLoading(it)
        })
        viewModel.successMessage.observe(this, {
            showToast(it)
            binding.inputPassword.setText(null)
        })
        viewModel.errorMessage.observe(this, {
            showToast(it)
        })
    }

    private fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun setupField() {
        viewModel.userData.observe(this, {user ->
            if (user != null) {
                binding.inputName.setText(user.name)
                binding.inputEmail.setText(user.email)
                binding.inputPhoneNumber.setText(user.phoneNumber)
            }
        })
    }

    private fun submitEdit() {
        val name = binding.inputName.text.toString()
        val email = binding.inputEmail.text.toString()
        val phoneNumber = binding.inputPhoneNumber.text.toString()
        val password = binding.inputPassword.text.toString()

        when {
            name.isEmpty() -> {
                binding.tfName.error = getString(R.string.name_alert)
            }
            email.isEmpty() -> {
                binding.tfEmail.error = getString(R.string.email_required)
            }
            phoneNumber.isEmpty() -> {
                binding.tfPhoneNumber.error = getString(R.string.password_required)
            }
            else -> {
                intent.getStringExtra("userId")?.let {
                    viewModel.updateUser(it, name, email, phoneNumber, password, token)
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
