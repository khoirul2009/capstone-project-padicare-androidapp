package com.padicare.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.padicare.model.LoginResponse
import com.padicare.model.LoginResult
import com.padicare.network.ApiConfig
import com.padicare.repository.CredentialPreferences
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: CredentialPreferences) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage

    fun login(username: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(
            username = username,
            password = password
        )
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    val name = response.body()?.loginResult?.name
                    val token = response.body()?.loginResult?.token
                    viewModelScope.launch {
                        if(name !== null && token !== null) {
                            pref.saveCredential(name, token)
                        }
                    }
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()?.string().toString())
                        _errorMessage.value = jsonObject.getString("message")
                    } catch (e : Exception) {
                        Log.e(TAG, "onFailure: ${e.message.toString()}")
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
            }

        })
    }
    fun getUser(): LiveData<LoginResult> {
        return pref.getCredentials().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            pref.clearCredential()
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}