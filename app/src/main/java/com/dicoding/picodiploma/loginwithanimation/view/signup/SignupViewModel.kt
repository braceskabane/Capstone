package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    val passwordError: MutableLiveData<String?> = MutableLiveData()
    val emailError: MutableLiveData<String?> = MutableLiveData()

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> get() = _isDataValid

    fun validatePassword(password: String) {
        when {
            password.length < 8 -> {
                passwordError.value = "Password tidak boleh kurang dari 8 karakter"
            }
            !password.any { it.isDigit() } -> {
                passwordError.value = "Password harus mengandung setidaknya satu angka"
            }
            !password.any { it.isLetterOrDigit().not() } -> {
                passwordError.value = "Password harus mengandung setidaknya satu karakter unik"
            }
            else -> {
                passwordError.value = null
            }
        }
    }

    fun validateEmail(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.value = "Email tidak valid"
        } else {
            emailError.value = null
        }
    }

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> get() = _registerResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("SignupViewModel", "Sending data: Name=$name, Email=$email, Password=$password")
                val response = repository.register(name, email, password)
                Log.d("SignupViewModel", "Response received: ${Gson().toJson(response)}")

                if (response.user != null) {
                    _registerResponse.value = response
                    Log.d("SignupViewModel", "Register success: ${response.message}")
                } else if (response.error != null) {
                    val errorMessage = response.error.message ?: "Unknown error"
                    _error.value = errorMessage
                    Log.e("SignupViewModel", "Register error: $errorMessage")
                } else {
                    _error.value = "Unknown error"
                    Log.e("SignupViewModel", "Register error: Unknown error")
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                val errorMessage = errorBody.error?.message ?: "Network error"
                _error.value = errorMessage
                Log.e("SignupViewModel", "Network error: $errorMessage")
            } catch (e: Exception) {
                val errorMessage = "Network error: ${e.message}"
                _error.value = errorMessage
                Log.e("SignupViewModel", "Network error: $errorMessage")
            }
        }
    }
}
//fun register(name: String, email: String, password: String) {
//    viewModelScope.launch {
//        try {
//            Log.d("SignupViewModel", "Sending data: Name=$name, Email=$email, Password=$password")
//            when (val result = repository.register(name, email, password)) {
//                is ResultState.Success -> {
//                    val response = result.data
//                    _registerResponse.value = response
//                    Log.d("SignupViewModel", "Register success: ${response.error?.message}")
//                }
//                is ResultState.Error -> {
//                    _error.value = result.error
//                    Log.e("SignupViewModel", "Network error: ${result.error}")
//                }
//                is ResultState.Loading -> {
//
//                }
//            }
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            Log.e("SignupViewModel", "HTTP Exception: $jsonInString")
//
//            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
//            val errorMessage = errorBody.error?.message
//            _error.value = errorMessage ?: "Network error"
//            Log.e("SignupViewModel", "Network error: $errorMessage")
//        } catch (e: Exception) {
//            _error.value = "Network error: ${e.message}"
//            Log.e("SignupViewModel", "Network error: ${e.message}")
//        }
//    }
//}
//    fun register(name: String, email: String, password: String) {
//        viewModelScope.launch {
//            try {
//                Log.d("SignupViewModel", "Sending data: Name=$name, Email=$email, Password=$password")
//                when (val response = repository.register(name, email, password)) {
//                    is RegisterResponse -> { // Handle both success and error responses
//                        if (response.status == null) {  // Assuming null status indicates error
//                            _error.value = response.message ?: "Unknown error"
//                            Log.e("SignupViewModel", "Register error: ${response.message}")
//                        } else {
//                            _registerResponse.value = response
//                            Log.d("SignupViewModel", "Register success: ${response.message}")
//                        }
//                    }
//                }
////                Log.d("SignupViewModel", "Sending data: Name=$name, Email=$email, Password=$password")
////
////                val response = repository.register(name, email, password)
////                if (response.error!!) {
////                    _error.value = response.message!!
////                    Log.e("SignupViewModel", "Register error: ${response.message}")
////                } else {
////                    _registerResponse.value = response
////                }
//            } catch (e: HttpException) {
//                val jsonInString = e.response()?.errorBody()?.string()
//                Log.e("SignupViewModel", "HTTP Exception: $jsonInString")
//
//                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
//                val errorMessage = errorBody.message
//                _error.value = errorMessage!!
//                Log.e("SignupViewModel", "Network error: $errorMessage")
//            } catch (e: Exception) {
//                _error.value = "Network error: ${e.message}"
//                Log.e("SignupViewModel", "Network error: ${e.message}")
//            }
//        }
//    }
//fun register(name: String, email: String, password: String) {
//    viewModelScope.launch {
//        try {
//            val response = repository.register(name, email, password)
//            val isSuccess = response.status?.toBoolean() ?: false
//            if (isSuccess) {
//                _registerResponse.value = response
//            } else {
//                _error.value = response.message ?: "Unknown error"
//                Log.e("SignupViewModel", "Register error: ${response.message}")
//            }
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
//            val errorMessage = errorBody.message
//            _error.value = errorMessage ?: "Network error"
//            Log.e("SignupViewModel", "Network error: $errorMessage")
//        } catch (e: Exception) {
//            _error.value = "Network error: ${e.message}"
//            Log.e("SignupViewModel", "Network error: ${e.message}")
//        }
//    }
//}