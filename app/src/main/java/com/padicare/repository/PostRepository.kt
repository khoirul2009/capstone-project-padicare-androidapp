package com.padicare.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.padicare.data.PostPagingSource
import com.padicare.model.PostItem

class PostRepository(private val apiServices: ApiServices) {

    fun getPosts(search: String? = ""): LiveData<PagingData<PostItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                PostPagingSource(apiServices, search = search)
            }

        ).liveData
    }
}