package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: BuildingRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserModel?>()
    val userSession: LiveData<UserModel?> get() = _userSession

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _buildings = MutableLiveData<List<ListBuildingItem>>()
    val buildings: LiveData<List<ListBuildingItem>> get() = _buildings

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun fetchBuildings() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getBuilding()
                if (response.error == false && response.data != null) {
                    _buildings.value = response.data.filterNotNull()
                } else {
                    _error.value = response.message ?: "Unknown error"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
//    fun refreshTokenIfNeeded(token: String, onTokenRefreshed: (String) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val response = repository.refresh(token)
//                val newToken = response.refresh?.token
//                if (newToken != null) {
//                    onTokenRefreshed(newToken)
//                } else {
//                    Log.e("MainViewModel", "Failed to refresh token")
//                }
//            } catch (e: Exception) {
//                Log.e("MainViewModel", "Error refreshing token: ${e.message}")
//            }
//        }
//    }
}