package com.dicoding.picodiploma.loginwithanimation.view.detailbuilding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.FavoriteRepository
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import kotlinx.coroutines.launch

class BuildAddUpdateViewModel @JvmOverloads constructor(application: Application) : AndroidViewModel(application) {

    private val mBuildingRepository: FavoriteRepository = FavoriteRepository(application)

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
