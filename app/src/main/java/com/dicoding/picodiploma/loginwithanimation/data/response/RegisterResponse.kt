package com.dicoding.picodiploma.loginwithanimation.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegisterResponse(
	val error: Boolean? = null,
	val message: String? = null
) : Parcelable


