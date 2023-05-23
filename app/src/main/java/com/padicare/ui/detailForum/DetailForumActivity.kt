package com.padicare.ui.detailForum

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.padicare.databinding.ActivityDetailForumBinding

class DetailForumActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailForumBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailForumBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}