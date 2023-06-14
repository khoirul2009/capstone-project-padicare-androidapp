package com.padicare.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.padicare.model.*
import com.padicare.network.ApiConfig
import com.padicare.repository.CredentialPreferences
import com.padicare.utils.Event
import com.padicare.utils.reduceFileImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeViewModel(private val preferences: CredentialPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage : LiveData<Event<String>> = _errorMessage

    private val _userData = MutableLiveData<UserData?>()
    val userData : LiveData<UserData?> = _userData

    private val _successMessage = MutableLiveData<Event<String>>()
    val successMessage : LiveData<Event<String>> = _successMessage

    private val _scanData = MutableLiveData<Event<ResultScan?>?>()
    val scanData : LiveData<Event<ResultScan?>?> = _scanData

    private val _postData = MutableLiveData<PostItem?>()
    val postData : LiveData<PostItem?> = _postData
    fun getUserFromApi(id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(id)
        client.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if(response.isSuccessful) {
                    _userData.value = response.body()?.data
                } else {
                    try {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value = Event(jsonObject?.getString("message").toString())
                    } catch (e : Exception) {
                        _errorMessage.value = Event(e.message.toString())
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = Event(t.message.toString())
            }

        })
    }
    fun getUpdatedPost() {
        val client = ApiConfig.getApiService().getUpdatedPost(1, 1)
        client.enqueue(object : Callback<GetPostsResponse> {
            override fun onResponse(
                call: Call<GetPostsResponse>,
                response: Response<GetPostsResponse>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful) {
                    if(response.body()?.listPost !== null && response.body()?.listPost?.size!! > 0) {
                        _postData.value = response.body()?.listPost?.get(0)

                    }
                } else {
                    try {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value = Event(jsonObject?.getString("message").toString())
                    } catch (e : Exception) {
                        _errorMessage.value =  Event(e.message.toString())
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<GetPostsResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value =  Event(t.message.toString())
            }

        })
    }
    fun scanImage(image: File, token: String) {
        _isLoading.value = true
        val file = reduceFileImage(file = image)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData("img", file.name, requestImageFile)
        val client = ApiConfig.getApiService().scanImage(authorization = "Bearer $token", imageMultipart)
        client.enqueue(object: Callback<ScanResponse> {
            override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    _scanData.value = Event(response.body()?.result)
                } else {
                    try {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value =  Event(jsonObject?.getString("message").toString())
                    } catch (e : Exception) {
                        _errorMessage.value = Event(e.message.toString())
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = Event(t.message.toString())
            }

        })

    }
    fun getUser(): LiveData<LoginResult> {
        return preferences.getCredentials().asLiveData()
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}