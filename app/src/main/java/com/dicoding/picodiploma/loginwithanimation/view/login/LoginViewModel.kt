package com.dicoding.picodiploma.loginwithanimation.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> get() = _loginResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isDataValid = MutableLiveData<Boolean>()
    val isDataValid: LiveData<Boolean> get() = _isDataValid

    val passwordError: MutableLiveData<String?> = MutableLiveData()
    val emailError: MutableLiveData<String?> = MutableLiveData()

    fun validatePassword(password: String) {
        when {
            password.length < 6 -> {
                passwordError.value = "Password tidak boleh kurang dari 6 karakter"
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

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
            Log.d("LoginViewModel", "Token saved: ${user.accessToken}")
        }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                _isLoading.value = false
                if (response.error == true) {
                    _error.value = response.message ?: "Unknown error"
                    Log.e("LoginViewModel", "Login error: ${response.message}")
                } else {
                    _loginResponse.value = response
                    Log.d("LoginViewModel", "Login success: ${response.message}")
                    response.data?.accessToken?.let { accessToken ->
                        response.data.refreshToken?.let { refreshToken ->
                            saveSession(UserModel(accessToken = accessToken, refreshToken = refreshToken))
                        }
                    }
                }
            } catch (e: HttpException) {
                _isLoading.value = false
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                _error.value = errorBody.message ?: "Network error"
                Log.e("LoginViewModel", "Network error: ${errorBody.message}")
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Network error: ${e.message}"
                Log.e("LoginViewModel", "Network error: ${e.message}")
            }
        }
    }
}

//fun login(email: String, password: String) {
//    _isLoading.value = true
//    viewModelScope.launch {
//        try {
//            Log.d("LoginViewModel", "Sending data: Email=$email, Password=$password")
//            val response = repository.login(email, password)
//            _isLoading.value = false
//            Log.d("LoginViewModel", "Response received: $response")
//
//            if (response.user != null && response.tokens != null) {
//                _loginResponse.value = response
//
//                val user = response.user
//                val tokens = response.tokens
//                val userModel = UserModel(
//                    id = user.id!!,
//                    name = user.name!!,
//                    email = user.email!!,
//                    accessToken = tokens.access?.token!!,
//                    refreshToken = tokens.refresh?.token!!
//                )
//                saveSession(userModel)
//
//                Log.d("LoginViewModel", "Login success: ${user.name}")
//            } else {
//                val errorMessage = "Login failed: User or tokens are null"
//                _error.value = errorMessage
//                Log.e("LoginViewModel", errorMessage)
//            }
//        } catch (e: HttpException) {
//            _isLoading.value = false
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
//            val errorMessage = errorBody?.message ?: "Network error"
//            _error.value = errorMessage
//            Log.e("LoginViewModel", "Network error: $errorMessage")
//        } catch (e: Exception) {
//            _isLoading.value = false
//            val errorMessage = "Network error: ${e.message}"
//            _error.value = errorMessage
//            Log.e("LoginViewModel", "Network error: $errorMessage")
//        }
//    }
//}