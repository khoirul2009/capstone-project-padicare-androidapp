package com.padicare.model

import com.google.gson.annotations.SerializedName

data class GetUserResponse(

	@field:SerializedName("data")
	val data: UserData? = null,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class UserData(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)
