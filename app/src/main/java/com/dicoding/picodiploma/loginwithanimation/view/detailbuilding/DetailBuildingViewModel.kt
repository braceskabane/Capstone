package com.dicoding.picodiploma.loginwithanimation.view.detailbuilding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailBuildingResponse
import kotlinx.coroutines.launch

class DetailBuildingViewModel(private val repository: BuildingRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserModel?>()
    val userSession: LiveData<UserModel?> get() = _userSession

    private val _buildingDetail = MutableLiveData<DetailBuildingResponse?>()
    val buildingDetail: LiveData<DetailBuildingResponse?> get() = _buildingDetail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun fetchBuildingDetail(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDetailBuilding(id)
                _buildingDetail.value = response
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}