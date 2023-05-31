package com.padicare.ui.addPost

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.padicare.MainActivity
import com.padicare.R
import com.padicare.databinding.ActivityAddPostBinding
import com.padicare.model.User
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory
import com.padicare.utils.createCustomTempFile
import com.padicare.utils.uriToFile
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
class AddPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPostBinding
    private lateinit var addPostViewModel: AddPostViewModel
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.photoLayout.setOnClickListener { optionImage() }
        binding.btnUpload.setOnClickListener { uploadPost() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AddPostActivity.REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
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

    private fun setupViewModel() {
        addPostViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                this
                ,CredentialPreferences.getInstance(dataStore))
        )[AddPostViewModel::class.java]

        addPostViewModel.getUser().observe(this, {
            this.user = User(username = it.name, token = it.token)
        })
        addPostViewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        addPostViewModel.isLoading.observe(this, {
            showLoading(it)
        })

        addPostViewModel.successMessage.observe(this, {
            binding.tfDescription.text = null
            binding.tfTitle.text = null
            binding.imgStory.setImageResource(R.drawable.ic_baseline_image_24)
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.success))
                .setMessage(it)
                .setPositiveButton("Back", {_,_, ->
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }).create().show()
        })
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddPostActivity)
                getFile = myFile
                binding.imgStory.setImageURI(uri)
            }
        }
    }

    private fun uploadPost() {
        val desc = binding.tfDescription.text.toString()
        val title = binding.tfTitle.text.toString()
        when {
            title.isEmpty() -> {
                binding.titleLayout.error = getString(R.string.title_required)
            }
            desc.isEmpty() -> {
                binding.descLayout.error = getString(R.string.desc_required)
            }
            getFile == null -> {
                Toast.makeText(this, getString(R.string.img_required), Toast.LENGTH_SHORT).show()
            }
            else -> {
                addPostViewModel.addPost(title, desc, user.token!!, getFile as File)
            }

        }
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
                this@AddPostActivity,
                "com.padicare",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                binding.imgStory.setImageBitmap(BitmapFactory.decodeFile(myFile.path))
            }
        }
    }


    private fun allPermissionsGranted() = AddPostActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }



}