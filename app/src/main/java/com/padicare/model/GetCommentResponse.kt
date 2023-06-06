package com.padicare.model

import com.google.gson.annotations.SerializedName

data class GetCommentResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("listComment")
	val listComment: List<ListCommentItem>
)

data class User(

	@field:SerializedName("photoUrl")
	val photoUrl: Any,

	@field:SerializedName("username")
	val username: String
)

data class ListCommentItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("comment")
	val comment: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("postId")
	val postId: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("user")
	val user: User,

	@field:SerializedName("updatedAt")
	val updatedAt: String
)
