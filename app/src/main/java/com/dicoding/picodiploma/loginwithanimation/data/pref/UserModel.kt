package com.dicoding.picodiploma.loginwithanimation.data.pref

data class UserModel(
    val id: String,
    val email: String,
    val name: String,
    val accessToken: String,
    val picture: String,
    val status: String,
    val isLogin: Boolean = false
)