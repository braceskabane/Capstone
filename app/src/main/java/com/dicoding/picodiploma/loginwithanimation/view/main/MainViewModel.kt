package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.SettingsRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.response.DataItem
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    private val buildingRepository: BuildingRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserModel?>()
    val userSession: LiveData<UserModel?> get() = _userSession

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _buildings = MutableLiveData<List<ListBuildingItem>>()
    val buildings: LiveData<List<ListBuildingItem>> get() = _buildings

    private val _buildingsPagingData = MutableLiveData<PagingData<ListBuildingItem>>()
    val buildingsPagingData: LiveData<PagingData<ListBuildingItem>> get() = _buildingsPagingData

    private val _searchResults = MutableLiveData<List<DataItem>>()
    val searchResults: LiveData<List<DataItem>> get() = _searchResults

    val isDarkMode: LiveData<Boolean> = settingsRepository.isDarkMode

    init {
        loadUserSession()
        loadBuildingsPaging()
    }

    fun getSession(): LiveData<UserModel> {
        return buildingRepository.getSession().asLiveData()
    }

    fun fetchBuildings() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = buildingRepository.getBuilding()
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

    private fun loadUserSession() {
        viewModelScope.launch {
            _userSession.value = buildingRepository.getSession().firstOrNull()
        }
    }

    fun searchBuildings(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = buildingRepository.getSearch(keyword)
                if (response.error == false && response.data != null) {
                    _searchResults.value = response.data.filterNotNull()
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

    private fun loadBuildingsPaging() {
        viewModelScope.launch {
            buildingRepository.getBuildings()
                .cachedIn(viewModelScope)
                .collectLatest { pagingData ->
                    _buildingsPagingData.value = pagingData
                }
        }
    }

    fun setDarkMode(isDarkMode: Boolean) {
        settingsRepository.setDarkMode(isDarkMode)
    }
}
