package com.padicare.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.padicare.databinding.PostItemBinding
import com.padicare.model.PostItem
import com.padicare.ui.post.PostActivity
import com.padicare.utils.convertDateTimeToTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PostAdapter : PagingDataAdapter<PostItem, PostAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)   {
        fun bind(postItem: PostItem) {
            if(postItem.user.photoUrl !== null) {
                Glide.with(binding.root.context)
                    .load(postItem.user.photoUrl)
                    .into(binding.userImage)
            }
            binding.tvUsername.text = postItem.user.username
            binding.tvDate.text = convertDateTimeToTime(postItem.createdAt).toString()
            Glide.with(binding.root.context)
                .load(postItem.photoUrl)
                .into(binding.postImage)
            binding.tvTitle.text = postItem.title
            val thumbnail = postItem.description.substring(0, Integer.min(postItem.description.length, 50))
            if(postItem.description.length >= 50) {
                binding.tvDesc.text = "$thumbnail . Lihat selengkapnya..."
            } else {
                binding.tvDesc.text = "$thumbnail"
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, PostActivity::class.java)
                intent.putExtra("POST", postItem)
                binding.root.context.startActivity(intent)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if(data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostItem>() {
            override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}