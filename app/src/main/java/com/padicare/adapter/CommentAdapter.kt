package com.padicare.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.padicare.databinding.CommentItemBinding
import com.padicare.model.CommentItem

class CommentAdapter : PagingDataAdapter<CommentItem, CommentAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(private val binding: CommentItemBinding) : RecyclerView.ViewHolder(binding.root)   {
        fun bind(commentItem: CommentItem) {
            if(commentItem.user.photoUrl !== null) {
                Glide.with(binding.root.context)
                    .load(commentItem.user.photoUrl)
                    .into(binding.circleImageView)
            }
            binding.tvUsername.text = commentItem.user.username
            binding.tvComment.text = commentItem.comment
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentItem>() {
            override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
                return oldItem.id == newItem.id
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
        val binding = CommentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
}