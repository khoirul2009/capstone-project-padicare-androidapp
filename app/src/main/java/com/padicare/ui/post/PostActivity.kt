package com.padicare.ui.post

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.padicare.R
import com.padicare.adapter.CommentAdapter
import com.padicare.adapter.LoadingStateAdapter
import com.padicare.databinding.ActivityPostBinding
import com.padicare.model.PostItem
import com.padicare.model.User
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.ViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "credentials")
class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var viewModel: PostViewModel
    private val adapter = CommentAdapter()
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupData()
        setupViewModel()
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, 1))
        binding.postImage.setOnClickListener {
            if(binding.postImage.scaleType == ImageView.ScaleType.FIT_CENTER) {
                binding.postImage.scaleType = ImageView.ScaleType.CENTER
            } else {
                binding.postImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }


        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(this, CredentialPreferences.getInstance(dataStore))
        )[PostViewModel::class.java]

        val post = intent.getParcelableExtra<PostItem>(POST)

        viewModel.getUser().observe(this, {
            this.token = it.token
            if(post != null) {
                getDataComment(post.id, it.token)
            }

        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.isLoading.observe(this, {
            showLoading(it)
        })

        binding.imageButton3.setOnClickListener {
            val comment = binding.tfComment.text.toString()
            when {
                comment.isEmpty() -> {
                    binding.commentLayout.error = getString(R.string.comment_alert)
                }
                else -> {
                    if(post != null) {
                        viewModel.addComment(post.id, token, comment)
                        binding.tfComment.text = null
                    }

                }
            }


        }
        viewModel.successMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            if(post != null) {
                getDataComment(post.id, token)
            }
        })


    }
    fun getDataComment(postId: String, token: String) {
        viewModel.getComment(postId, token).observe(this, {
            adapter.submitData(lifecycle, it)
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupData() {
        val post = intent.getParcelableExtra<PostItem>(POST)
        binding.postAuthor.text = post?.user?.username
        Glide.with(binding.root.context)
            .load(post?.photoUrl)
            .into(binding.postImage)
        binding.postTime.text =  convertDateTimeToTime(post?.createdAt).toString()
        binding.postTitle.text = post?.title
        binding.postBody.text = post?.description

    }

    fun convertDateTimeToTime(mysqlDateTime: String?): String? {
        // Define the format of the MySQL datetime value
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", )
        val toFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        // Parse the MySQL datetime string into a LocalDateTime object
        return LocalDateTime.parse(mysqlDateTime, formatter).format(toFormatter)
    }

    companion object {
        private const val POST = "POST"
    }
}