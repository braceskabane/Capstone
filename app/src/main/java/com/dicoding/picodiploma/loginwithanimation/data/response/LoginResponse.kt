package com.dicoding.picodiploma.loginwithanimation.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @field:SerializedName("data")
    val data: Token? = null,
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null
) : Parcelable

@Parcelize
data class Token(
    @field:SerializedName("access_token")
    val accessToken: String? = null,
    @field:SerializedName("refresh_token")
    val refreshToken: String? = null
) : Parcelable





