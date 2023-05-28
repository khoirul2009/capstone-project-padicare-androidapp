package com.padicare.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.padicare.databinding.FragmentProfileBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory
import com.padicare.ui.login.LoginActivity

class ProfileFragment : Fragment() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
    companion object {
        fun newInstance() = ProfileFragment()
    }
    private var _binding : FragmentProfileBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val root: View = binding.root
        val pref = activity?.let { CredentialPreferences.getInstance(it.dataStore) }
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), pref!!)
        )[ProfileViewModel::class.java]

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.menuLogOut.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(activity, LoginActivity::class.java))
        }


    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}