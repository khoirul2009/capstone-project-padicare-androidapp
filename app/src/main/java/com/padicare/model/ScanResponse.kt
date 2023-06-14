package com.padicare.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class ScanResponse(

	@field:SerializedName("result")
	val result: ResultScan? = null,

	@field:SerializedName("error")
	val error: Boolean
)

@Parcelize
data class ResultScan(

	@field:SerializedName("solution")
	val solution: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("desc")
	val desc: String
) : Parcelable
