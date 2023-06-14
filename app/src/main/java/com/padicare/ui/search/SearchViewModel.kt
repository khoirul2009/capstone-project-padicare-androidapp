package com.padicare.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.padicare.model.PostItem
import com.padicare.repository.PostRepository

class SearchViewModel(private val postRepository: PostRepository): ViewModel() {
    fun getPost(search: String) : LiveData<PagingData<PostItem>> = postRepository.getPosts(search).cachedIn(viewModelScope)
}