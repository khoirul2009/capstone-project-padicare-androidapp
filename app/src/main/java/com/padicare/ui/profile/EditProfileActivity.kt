package com.padicare.ui.profile

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.padicare.R
import com.padicare.databinding.ActivityEditProfileBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.repository.ThemePreference
import com.padicare.ui.ViewModelFactory
import com.padicare.utils.createCustomTempFile
import com.padicare.utils.rotateFile
import com.padicare.utils.uriToFile
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
private val Context.dataStore2: DataStore<Preferences> by preferencesDataStore(name = "themes")
class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var token : String
    private lateinit var currentPhotoPath: String
    private  lateinit var userId : String
    private var getFile: File? = null

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

        binding.profileImage.setOnClickListener {
            optionImage()
        }

        binding.changeImage.setOnClickListener { submitPhoto() }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, pref =  CredentialPreferences.getInstance(dataStore) , themePref = ThemePreference.getInstance(dataStore2))
        )[ProfileViewModel::class.java]
        intent.getStringExtra("userId")?.let {
            viewModel.getUserFromApi(it)

        }

        viewModel.getUser().observe(this, {
            this.token = it.token
            this.userId = it.userId
        })
        viewModel.isLoading.observe(this, {
            showLoading(it)
        })
        viewModel.successMessage.observe(this, {
            showToast(it)
            binding.inputPassword.setText(null)
            viewModel.getUserFromApi(userId)
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
                if(user.photoUrl !== null) {
                    Glide.with(this)
                        .load(user.photoUrl)
                        .into(binding.profileImage)
                }
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

    private fun submitPhoto() {
        viewModel.changePhotoProfile(getFile as File, token, userId)
        getFile = null
    }

    private fun optionImage() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih metode")

        val options = arrayOf("Camera", "Gallery")
        builder.setItems(options) {dialog, which ->
            when(which) {
                0 -> {
                    startTakePhoto()
                }
                1 -> {
                    startGallery()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@EditProfileActivity,
                "com.padicare",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@EditProfileActivity)
                getFile = myFile
                binding.profileImage.setImageURI(uri)
                if(getFile !== null) {
                    binding.changeImage.isEnabled = true
                }
            }
        }
    }


    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file, true)
                getFile = file
                binding.profileImage.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
                if(getFile !== null) {
                    binding.changeImage.isEnabled = true
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
