package com.padicare.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.padicare.data.CommentPagingSource
import com.padicare.data.PostPagingSource
import com.padicare.model.CommentItem

class CommentRepository(private val apiServices: ApiServices) {
    fun getComment(idPost: String, token: String): LiveData<PagingData<CommentItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                CommentPagingSource(apiServices, id = idPost, token = token)
            }

        ).liveData
    }
}