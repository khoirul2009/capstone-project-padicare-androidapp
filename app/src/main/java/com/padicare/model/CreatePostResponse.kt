package com.padicare.model

import com.google.gson.annotations.SerializedName

data class CreatePostResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
