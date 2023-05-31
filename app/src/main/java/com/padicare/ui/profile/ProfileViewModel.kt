package com.padicare.ui.profile

import android.util.Log
import androidx.lifecycle.*
import com.padicare.model.GetUserResponse
import com.padicare.model.LoginResult
import com.padicare.model.UserData
import com.padicare.network.ApiConfig
import com.padicare.repository.CredentialPreferences
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(private val pref: CredentialPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    private val _userData = MutableLiveData<UserData?>()
    val userData : LiveData<UserData?> = _userData
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
                        val jsonObject = JSONObject(response.errorBody()?.string())
                        _errorMessage.value = jsonObject.getString("message")
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
    fun logout() {
        viewModelScope.launch {
            pref.clearCredential()
        }
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getCredentials().asLiveData()
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}