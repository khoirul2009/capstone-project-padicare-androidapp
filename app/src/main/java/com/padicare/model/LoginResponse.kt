package com.padicare.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult? = null,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LoginResult(
	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("token")
	val token: String,

	var isLogin: Boolean? = null
)
