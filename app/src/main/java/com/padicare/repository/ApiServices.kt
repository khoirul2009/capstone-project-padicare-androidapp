package com.padicare.repository


import com.padicare.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
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
        @Query("size") size : Int,
        @Query("search") search: String? = ""
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

    @DELETE("logout")
    fun logout(
        @Header("Authorization") authorization: String
    ) : Call<DefaultResponse>

    @GET("posts/{id}/comment")
    suspend fun getComments(
        @Path("id") id: String,
        @Query("page") page : Int,
        @Query("size") size : Int,
        @Header("Authorization") authorization: String,
    ) : GetCommentResponse

    @POST("posts/{id}/comment")
    @FormUrlEncoded
    fun addComment(
        @Path("id") id: String,
        @Header("Authorization") authorization: String,
        @Field("comment") comment : String
    ) : Call<DefaultResponse>

    @PUT("user/{id}")
    @FormUrlEncoded
    fun editUser(
        @Path("id") id: String,
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("phoneNumber") phoneNumber : String,
        @Field("password") password : String? = null,
        @Header("Authorization") authorization: String,
    ) : Call<DefaultResponse>

    @POST("scanImage")
    @Multipart
    fun scanImage(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part
    ) : Call<ScanResponse>

    @GET("posts")
    fun getUpdatedPost(
        @Query("page") page : Int,
        @Query("size") size : Int,
    ) : Call<GetPostsResponse>

    @POST("user/{id}/postPhoto")
    @Multipart
    fun editPhoto(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Path("id") id: String
    ) : Call<DefaultResponse>

}