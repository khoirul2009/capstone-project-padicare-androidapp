package com.padicare.ui.search

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.padicare.adapter.LoadingStateAdapter
import com.padicare.adapter.PostAdapter
import com.padicare.databinding.ActivitySearchBinding
import com.padicare.ui.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupViewModel()

        binding.rvPost.layoutManager = LinearLayoutManager(this)
        binding.closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[SearchViewModel::class.java]

        val searchQuery = intent.getStringExtra("search")
        val search = searchQuery.toString().substring(0, Integer.min(searchQuery.toString().length, 20))
        if(searchQuery.toString().length >= 20) {
            binding.searchQuery.text = "$search..."
        } else {
            binding.searchQuery.text = "$search"
        }
        searchQuery.let {
            viewModel.getPost(it.toString()).observe(this, {
                val adapter = PostAdapter()
                binding.rvPost.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                adapter.submitData(lifecycle,it)

            })
        }
    }

}