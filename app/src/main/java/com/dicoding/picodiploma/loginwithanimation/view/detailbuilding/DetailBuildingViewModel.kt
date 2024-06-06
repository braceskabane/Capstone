package com.dicoding.picodiploma.loginwithanimation.view.detailbuilding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel

class DetailBuildingViewModel(private val repository: BuildingRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userSession = MutableLiveData<UserModel?>()
    val userSession: LiveData<UserModel?> get() = _userSession

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}