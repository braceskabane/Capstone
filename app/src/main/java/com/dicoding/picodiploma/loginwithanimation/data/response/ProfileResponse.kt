package com.dicoding.picodiploma.loginwithanimation.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProfileResponse(
	val data: Data? = null,
	val error: Boolean? = null,
	val message: String? = null
) : Parcelable

@Parcelize
data class Data(
	val password: String? = null,
	val role: String? = null,
	val updatedAt: String? = null,
	val name: String? = null,
	val createdAt: String? = null,
	val id: String? = null,
	val email: String? = null
) : Parcelable
