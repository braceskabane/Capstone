package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.detailbuilding.DetailBuildingViewModel
import com.dicoding.picodiploma.loginwithanimation.view.detailprofile.DetailProfileViewModel
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

@Suppress("UNCHECKED_CAST")
class BuildingViewModelFactory(private val repository: BuildingRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailProfileViewModel::class.java) -> {
                DetailProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailBuildingViewModel::class.java) -> {
                DetailBuildingViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context) =
            BuildingViewModelFactory(Injection.provideBuildingRepository(context))
    }
}