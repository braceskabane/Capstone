package com.dicoding.picodiploma.loginwithanimation.view

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.data.FavoriteRepository
import com.dicoding.picodiploma.loginwithanimation.view.detailprofile.FavProfileViewModel

class FavoriteViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavProfileViewModel::class.java)) {
            val repository = FavoriteRepository(application)
            @Suppress("UNCHECKED_CAST")
            return FavProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}