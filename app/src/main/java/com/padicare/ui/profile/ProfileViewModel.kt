package com.padicare.ui.profile

import android.util.Log
import androidx.lifecycle.*
import com.padicare.model.DefaultResponse
import com.padicare.model.GetUserResponse
import com.padicare.model.LoginResult
import com.padicare.model.UserData
import com.padicare.network.ApiConfig
import com.padicare.repository.CredentialPreferences
import com.padicare.repository.ThemePreference
import com.padicare.utils.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileViewModel(private val pref: CredentialPreferences, private val themePreference: ThemePreference) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _userData = MutableLiveData<UserData?>()
    val userData : LiveData<UserData?> = _userData

    private val _successMessage = MutableLiveData<String>()
    val successMessage : LiveData<String> = _successMessage
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
                        _errorMessage.value = jsonObject?.getString("message")
                    } catch (e : Exception) {
                        _errorMessage.value = e.message.toString()
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })
    }
    fun updateUser(id: String, name : String, email: String, phoneNumber: String, password : String?, token : String) {
        val client = ApiConfig.getApiService().editUser(id, name, email, phoneNumber, password, "Bearer $token")
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if(response.isSuccessful) {
                    _successMessage.value = responseBody?.message
                } else {
                    try {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value = jsonObject?.getString("message")
                    } catch (e : Exception) {
                        _errorMessage.value = e.message.toString()
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
    fun changePhotoProfile(image: File, token: String, id: String) {
        _isLoading.value = true
        val file = reduceFileImage(file = image)
        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart : MultipartBody.Part = MultipartBody.Part.createFormData("photo", file.name, requestImageFile)
        val client = ApiConfig.getApiService().editPhoto("Bearer $token", imageMultipart, id)
        client.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if(response.isSuccessful) {
                    _successMessage.value = responseBody?.message
                } else {
                    try {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value = jsonObject?.getString("message")
                    } catch (e : Exception) {
                        _errorMessage.value = e.message.toString()
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
    fun logout(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().logout("Bearer $token")
        client.enqueue(object: Callback<DefaultResponse> {
            override fun onResponse(
                call: Call<DefaultResponse>,
                response: Response<DefaultResponse>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful) {
                    _successMessage.value = responseBody?.message
                } else {
                    try {
                        val jsonObject = response.errorBody()?.string()?.let { JSONObject(it) }
                        _errorMessage.value = jsonObject?.getString("message")
                    } catch (e : Exception) {
                        _errorMessage.value = e.message.toString()
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })

        viewModelScope.launch {
            pref.clearCredential()
        }
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            themePreference.saveThemeSetting(isDarkModeActive)
        }
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return themePreference.getThemeSetting().asLiveData()
    }


    fun getUser(): LiveData<LoginResult> {
        return pref.getCredentials().asLiveData()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}