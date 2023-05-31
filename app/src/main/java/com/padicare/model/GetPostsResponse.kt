package com.padicare.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class GetPostsResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("listPost")
	val listPost: List<PostItem>
)

@Parcelize
data class User(
	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("username")
	val username: String,

	val token: String? = null

) : Parcelable


@Parcelize
data class PostItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("like")
	val like: Int,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("userId")
	val userId: String,


	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("views")
	val views: String,

	@field:SerializedName("updatedAt")
	val updatedAt: String
) : Parcelable