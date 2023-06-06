package com.padicare.ui.post

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.padicare.model.CommentItem
import com.padicare.model.DefaultResponse
import com.padicare.model.LoginResult
import com.padicare.network.ApiConfig
import com.padicare.repository.CommentRepository
import com.padicare.repository.CredentialPreferences
import com.padicare.ui.addPost.AddPostViewModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel(private val commentRepository: CommentRepository, private val pref: CredentialPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage : LiveData<String> = _successMessage
    fun getComment(idPost: String, token: String) : LiveData<PagingData<CommentItem>> = commentRepository.getComment(idPost, token).cachedIn(viewModelScope)
    fun getUser(): LiveData<LoginResult> {
        return pref.getCredentials().asLiveData()
    }
    fun addComment(idPost: String, token: String, comment: String) {
        _isLoading.value = false
        val client = ApiConfig.getApiService().addComment(idPost, "Bearer $token", comment)
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    _successMessage.value = response.body()?.message
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        _errorMessage.value = jsonObject.getString("message")
                    } catch (e : Exception) {
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }
    companion object {
        private const val TAG = "PostViewModel"
    }
}