package com.dicoding.picodiploma.loginwithanimation.view.detailprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.FavoriteRepository
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding

class FavProfileViewModel(private val repository: FavoriteRepository) : ViewModel() {
    private val _builds = MutableLiveData<List<FavoriteBuilding>>()
    val builds: LiveData<List<FavoriteBuilding>> = _builds

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllBuilding() {
        _isLoading.value = true
        repository.getAllBuilding().observeForever { buildingList ->
            _builds.value = buildingList
            _isLoading.value = false
        }
    }
}