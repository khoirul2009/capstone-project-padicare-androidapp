package com.padicare.ui.result

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.padicare.databinding.ActivityResultBinding
import com.padicare.model.ResultScan

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupData()

        binding.resultImage.setOnClickListener {
            if(binding.resultImage.scaleType == ImageView.ScaleType.FIT_CENTER) {
                binding.resultImage.scaleType = ImageView.ScaleType.CENTER
            } else {
                binding.resultImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupData() {
        val result = intent.getParcelableExtra<ResultScan>("result")
        val image = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(image)
        binding.resultImage.setImageURI(imageUri)

        result?.let {
            binding.resultTitle.text = result.name
            binding.resultDefinisi.text= result.desc
            binding.resultSolusi.text = result.solution
        }
    }


}