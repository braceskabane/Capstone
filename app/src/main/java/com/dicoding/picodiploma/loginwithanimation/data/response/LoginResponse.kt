package com.dicoding.picodiploma.loginwithanimation.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    @field:SerializedName("user")
    val user: User? = null,
    @field:SerializedName("message")
    val message: String? = null,
    @field:SerializedName("error")
    val error: Error? = null
) : Parcelable

@Parcelize
data class User(
    @field:SerializedName("access_token")
    val accessToken: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("_id")
    val id: String? = null,
    @field:SerializedName("email")
    val email: String? = null,
    @field:SerializedName("picture")
    val picture: String? = null,
    @field:SerializedName("status")
    val status: String? = null
) : Parcelable



