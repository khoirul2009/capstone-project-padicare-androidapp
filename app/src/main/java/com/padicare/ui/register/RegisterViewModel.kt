package com.padicare.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.padicare.model.RegisterrResponse
import com.padicare.network.ApiConfig
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message : LiveData<String> = _message

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage : LiveData<String> = _errorMessage



    fun register(fullname: String, email: String, username: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(fullname,username,email,password)
        client.enqueue(object : Callback<RegisterrResponse> {
            override fun onResponse(
                call: Call<RegisterrResponse>,
                response: Response<RegisterrResponse>
            ) {
                _isLoading.value = false
                if(response.isSuccessful) {
                    _message.value = response.body()?.message
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()?.string().toString())
                        _errorMessage.value = jsonObject.getString("message")
                    } catch (e : Exception) {
                        _errorMessage.value = e.message.toString()
                    }
                }
            }

            override fun onFailure(call: Call<RegisterrResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message.toString()
            }

        })

    }
}