package com.dicoding.picodiploma.loginwithanimation.data.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutor {
    val diskIO: Executor = Executors.newSingleThreadExecutor()
}