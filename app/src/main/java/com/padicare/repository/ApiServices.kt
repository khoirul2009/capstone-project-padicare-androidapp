package com.padicare.repository

import com.padicare.model.LoginResponse
import com.padicare.model.RegisterrResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServices {
    @POST("login")
    @FormUrlEncoded
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterrResponse>
}