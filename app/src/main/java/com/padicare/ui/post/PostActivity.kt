package com.padicare.ui.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.padicare.databinding.ActivityPostBinding
import com.padicare.model.PostItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupData()

        binding.backButton.setOnClickListener {
            finish()
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