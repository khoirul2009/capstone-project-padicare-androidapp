package com.padicare.ui.forum

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.padicare.R
import com.padicare.adapter.LoadingStateAdapter
import com.padicare.adapter.PostAdapter
import com.padicare.databinding.FragmentForumBinding
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory
import com.padicare.ui.addPost.AddPostActivity
import com.padicare.ui.profile.ProfileViewModel

class ForumFragment : Fragment() {

    companion object {
        fun newInstance() = ForumFragment()
    }
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")

    private lateinit var viewModel: ForumViewModel

    private var _binding : FragmentForumBinding? = null

    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForumBinding.inflate(inflater, container, false)

        val root = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = activity?.let { CredentialPreferences.getInstance(it.dataStore) }

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), pref!!)
        ).get(ForumViewModel::class.java)

        binding.btnAddPost.setOnClickListener {
            startActivity(Intent(activity, AddPostActivity::class.java))
        }

        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())


        getData()

        binding.lySwip.setOnRefreshListener {
            getData()
            binding.lySwip.isRefreshing = false
        }


    }

    private fun getData() {
        val adapter = PostAdapter()
        binding.rvPost.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.getPost().observe(viewLifecycleOwner, {
            adapter.submitData(lifecycle,it)
        })

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}