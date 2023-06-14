package com.padicare.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.padicare.databinding.FragmentProfileBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.repository.ThemePreference
import com.padicare.ui.ViewModelFactory
import com.padicare.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
    private val Context.dataStore2: DataStore<Preferences> by preferencesDataStore(name = "themes")
    companion object {
        fun newInstance() = ProfileFragment()
    }
    private lateinit var binding : FragmentProfileBinding


    private lateinit var viewModel: ProfileViewModel

    private lateinit var token: String

    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val pref = activity?.let { CredentialPreferences.getInstance(it.dataStore) }
        val themePref = activity?.let { ThemePreference.getInstance(it.dataStore2) }
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), pref!!, themePref)
        )[ProfileViewModel::class.java]


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuLogOut.setOnClickListener {
            viewModel.logout(token)
        }


        binding.menuEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        viewModel.getThemeSettings().observe(viewLifecycleOwner) {isDarkModeActive: Boolean ->
            if(isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }

        viewModel.successMessage.observe(requireActivity(), {
            Toast.makeText(requireContext(), it , Toast.LENGTH_SHORT).show()
        })

        viewModel.isLoading.observe(requireActivity(), {
            showLoading(it)
        })

        viewModel.getUser().observe(viewLifecycleOwner, {
            this.token = it.token
            this.userId = it.userId
            viewModel.getUserFromApi(it.userId)
            viewModel.userData.observe(viewLifecycleOwner, {
                binding.name.text = it?.name
                binding.email.text = it?.email
                if(it?.photoUrl !== null) {
                    Glide.with(requireActivity())
                        .load(it.photoUrl)
                        .into(binding.userImage)
                }
            })

        })
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }



}