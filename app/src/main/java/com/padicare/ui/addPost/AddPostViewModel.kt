package com.padicare.ui.addPost

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.padicare.model.CreatePostResponse
import com.padicare.model.LoginResult
import com.padicare.network.ApiConfig
import com.padicare.repository.CredentialPreferences
import com.padicare.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddPostViewModel(private val pref: CredentialPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage : LiveData<String> = _successMessage

    fun addPost(title: String, descripiton: String, token: String, photo: File) {
        _isLoading.value = true
        val file = reduceFileImage(file = photo)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        val client = ApiConfig.getApiService().createPost(
            title = title.toRequestBody("text/plain".toMediaType()),
            description = descripiton.toRequestBody("text/plain".toMediaType()),
            authorization = "Bearer ${token}",
            file = imageMultipart
        )
        client.enqueue(object : Callback<CreatePostResponse> {
            override fun onResponse(
                call: Call<CreatePostResponse>,
                response: Response<CreatePostResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if(response.isSuccessful) {
                    _successMessage.value = responseBody?.message
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        _errorMessage.value = jsonObject.getString("message")
                    } catch (e : Exception) {
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<CreatePostResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }
    fun getUser(): LiveData<LoginResult> {
        return pref.getCredentials().asLiveData()
    }
    companion object {
        private const val TAG = "AddStoryViewModel"
    }
}