package com.padicare.repository

import com.padicare.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("posts")
    suspend fun getPost(
        @Query("page") page : Int,
        @Query("size") size : Int
    ) : GetPostsResponse

    @Multipart
    @POST("posts")
    fun createPost(
        @Header("Authorization") authorization: String,
        @Part("description") description: RequestBody,
        @Part("title") title : RequestBody,
        @Part file: MultipartBody.Part
    ) : Call<CreatePostResponse>

    @GET("user/{id}")
    fun getUser(
        @Path("id") id: String
    ) : Call<GetUserResponse>
}