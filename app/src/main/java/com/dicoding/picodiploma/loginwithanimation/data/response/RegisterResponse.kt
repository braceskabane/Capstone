package com.dicoding.picodiploma.loginwithanimation.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class RegisterResponse(
	@field:SerializedName("message")
	val message: String? = null,
	@field:SerializedName("user")
	val user: User? = null,
	@field:SerializedName("error")
	val error: Error? = null
) : Parcelable

@Parcelize
data class Error(
	@field:SerializedName("status")
	val status: Int? = null,
	@field:SerializedName("message")
	val message: String? = null
) : Parcelable


