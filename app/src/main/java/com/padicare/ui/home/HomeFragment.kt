package com.padicare.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.padicare.databinding.FragmentHomeBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory
import com.padicare.ui.addPost.AddPostActivity
import com.padicare.ui.result.ResultActivity
import com.padicare.ui.search.SearchActivity
import com.padicare.utils.convertDateTimeToTime
import com.padicare.utils.createCustomTempFile
import com.padicare.utils.rotateFile
import com.padicare.utils.uriToFile
import java.io.File

class HomeFragment : Fragment() {


    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var currentPhotoPath: String
    private lateinit var token: String
    private lateinit var imageUri: String

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root





        val pref = activity?.let { CredentialPreferences.getInstance(it.dataStore) }
        homeViewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), pref!!)
        )[HomeViewModel::class.java]

        homeViewModel.getUpdatedPost()

        homeViewModel.postData.observe(requireActivity(), {
            if(it !== null) {
                Glide.with(requireContext())
                    .load(it.photoUrl)
                    .into(binding.postImage)
                binding.postDate.text = convertDateTimeToTime(it.createdAt).toString()
                binding.postTitle.text = it.title

            }
        })



        binding.searchButton.setOnClickListener {
            Toast.makeText(activity, "Search clicked", Toast.LENGTH_SHORT).show()
        }
        binding.searchButton.setOnClickListener {
            binding.searchView.show()
        }
        binding.btnScan.setOnClickListener {
            optionImage()
        }



        binding.searchView
            .getEditText()
            .setOnEditorActionListener { v, actionId, event ->
                val query = binding.searchView.getText().toString()
                val intent = Intent(activity, SearchActivity::class.java)
                intent.putExtra("search", binding.searchView.text.toString())
                if(query != "") {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Masukkan teks terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
                binding.searchView.hide()
                binding.topBar.visibility = MaterialCardView.VISIBLE

                false
            }

        return root
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file, true)
                homeViewModel.scanImage(file, token)
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)
        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.padicare",
                it
            )
            imageUri = photoURI.toString()
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun optionImage() {

        val builder = AlertDialog.Builder(requireActivity())
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
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, requireContext())
                imageUri = uri.toString()
                homeViewModel.scanImage(myFile, token)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getUser().observe(requireActivity(), {
            homeViewModel.getUserFromApi(it.userId)
            token = it.token
        })
        homeViewModel.userData.observe(requireActivity(), {
            binding.username.text = it?.username
            if(it?.photoUrl != null) {
                Glide.with(requireActivity())
                    .load(it.photoUrl)
                    .into(binding.userImage)
            }
        })

        homeViewModel.scanData.observe(requireActivity(), {
            it?.getContextIfNotHandled()?.let{
                val intent = Intent(requireContext(), ResultActivity::class.java)
                intent.putExtra("result", it)
                intent.putExtra("imageUri", imageUri)
                startActivity(intent)

            }
        })

        homeViewModel.isLoading.observe(requireActivity(), {
            showLoading(it)
        })

        homeViewModel.errorMessage.observe(requireActivity(), {
            it.getContextIfNotHandled()?.let{ errorMsg ->
                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show()
            }
        })

        homeViewModel.successMessage.observe(requireActivity(), {
            it?.getContextIfNotHandled()?.let{
                Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
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