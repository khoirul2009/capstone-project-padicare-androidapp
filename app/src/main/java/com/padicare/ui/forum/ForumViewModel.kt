package com.padicare.ui.forum

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.padicare.model.PostItem
import com.padicare.repository.PostRepository

class ForumViewModel(private val postRepository: PostRepository) : ViewModel() {


   fun getPost() : LiveData<PagingData<PostItem>> = postRepository.getPosts().cachedIn(viewModelScope)

}