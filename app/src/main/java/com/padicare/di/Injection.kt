package com.padicare.di

import android.content.Context
import com.padicare.network.ApiConfig
import com.padicare.repository.PostRepository


object Injection {
    fun provideRepository(): PostRepository {
        val apiService = ApiConfig.getApiService()
        return PostRepository(apiService)
    }
}