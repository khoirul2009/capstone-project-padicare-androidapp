package com.padicare.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.padicare.model.PostItem
import com.padicare.repository.ApiServices

class PostPagingSource(private val apiServices: ApiServices, private val search: String?) : PagingSource<Int, PostItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, PostItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostItem> {
        try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiServices.getPost(page = page, size = 5, search = search).listPost

            return LoadResult.Page(
                data = responseData,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}