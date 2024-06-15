package com.dicoding.picodiploma.loginwithanimation.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TokenResponse(
	val access: Access? = null,
	val refresh: Refresh? = null
) : Parcelable

@Parcelize
data class Access(
	val expires: String? = null,
	val token: String? = null
) : Parcelable

@Parcelize
data class Refresh(
	val expires: String? = null,
	val token: String? = null
) : Parcelable


//@Parcelize
//data class AuthResponse(
//	val tokens: Tokens? = null,
//	val user: User? = null
//) : Parcelable
//
//@Parcelize
//data class Tokens(
//	val access: Access? = null,
//	val refresh: Refresh? = null
//) : Parcelable
//
//@Parcelize
//data class User(
//	val role: String? = null,
//	val name: String? = null,
//	val id: String? = null,
//	val email: String? = null,
//	val isEmailVerified: Boolean? = null
//) : Parcelable
//
//@Parcelize
//data class Access(
//	val expires: String? = null,
//	@field:SerializedName("access_token")
//	val token: String? = null
//) : Parcelable
//
//@Parcelize
//data class Refresh(
//	val expires: String? = null,
//	@field:SerializedName("refresh_token")
//	val token: String? = null
//) : Parcelable
////////////////////////////////////////
@Parcelize
data class ErrorResponse(
	val stack: String? = null,
	val code: Int? = null,
	val message: String? = null
) : Parcelable

