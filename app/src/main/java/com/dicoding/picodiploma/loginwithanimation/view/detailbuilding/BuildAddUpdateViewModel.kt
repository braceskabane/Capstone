package com.dicoding.picodiploma.loginwithanimation.view.detailbuilding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BuildAddUpdateViewModel @JvmOverloads constructor(application: Application) : AndroidViewModel(application) {

    private val mBuildingRepository: BuildingRepository

    init {
        val userPreference = UserPreference.getInstance(application.applicationContext.dataStore)
        val token = runBlocking { userPreference.getSession().first().accessToken }
        val apiService = ApiConfig.getApiService(token)
        mBuildingRepository = BuildingRepository.getInstance(userPreference, apiService, application)
    }

    fun insert(build: FavoriteBuilding) {
        viewModelScope.launch {
            mBuildingRepository.insert(build)
        }
    }

    fun delete(build: FavoriteBuilding) {
        viewModelScope.launch {
            mBuildingRepository.delete(build)
        }
    }

    fun getFavoriteUserById(id: String): LiveData<FavoriteBuilding> {
        return mBuildingRepository.getFavoriteUserById(id)
    }
}