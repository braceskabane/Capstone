package com.dicoding.picodiploma.loginwithanimation.view.detailprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.ProfileResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailProfileViewModel(private val repository: BuildingRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserModel?>()
    val userSession: LiveData<UserModel?> get() = _userSession

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _profileResponse = MutableLiveData<ProfileResponse?>()
    val profileResponse: LiveData<ProfileResponse?> get() = _profileResponse

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun fetchUserProfile() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.profile()
                _isLoading.value = false
                if (response.error == false && response.data != null) {
                    _profileResponse.value = response
                } else {
                    _error.value = response.message ?: "Unknown error"
                }
            } catch (e: HttpException) {
                _isLoading.value = false
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ProfileResponse::class.java)
                _error.value = errorBody.message ?: "Network error"
            } catch (e: Exception) {
                _isLoading.value = false
                _error.value = "Network error: ${e.message}"
            }
        }
    }

    fun logout() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.logout()
            _isLoading.value = false
        }
    }
}
