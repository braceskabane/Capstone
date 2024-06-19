package com.dicoding.picodiploma.loginwithanimation.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.SettingsRepository
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.addproperty.AddPropertyViewModel
import com.dicoding.picodiploma.loginwithanimation.view.detailbuilding.DetailBuildingViewModel
import com.dicoding.picodiploma.loginwithanimation.view.detailprofile.DetailProfileViewModel
import com.dicoding.picodiploma.loginwithanimation.view.main.MainViewModel

@Suppress("UNCHECKED_CAST")
class BuildingViewModelFactory(
    private val buildingRepository: BuildingRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(buildingRepository, settingsRepository) as T
            }
            modelClass.isAssignableFrom(DetailProfileViewModel::class.java) -> {
                DetailProfileViewModel(buildingRepository) as T
            }
            modelClass.isAssignableFrom(DetailBuildingViewModel::class.java) -> {
                DetailBuildingViewModel(buildingRepository) as T
            }
            modelClass.isAssignableFrom(AddPropertyViewModel::class.java) -> {
                AddPropertyViewModel(buildingRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context): BuildingViewModelFactory {
            val buildingRepository = Injection.provideBuildingRepository(context)
            val settingsRepository = Injection.provideSettingsRepository(context)
            return BuildingViewModelFactory(buildingRepository, settingsRepository)
        }
    }
}
