package com.dicoding.picodiploma.loginwithanimation.data.pref

data class UserModel(
    val accessToken: String,
    val refreshToken: String,
    val isLogin: Boolean = false
)