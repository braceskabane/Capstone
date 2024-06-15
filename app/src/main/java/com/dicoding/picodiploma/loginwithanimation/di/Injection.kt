package com.dicoding.picodiploma.loginwithanimation.di

import android.app.Application
import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.lokal.pagging.BuildingsDatabase
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        return UserRepository.getInstance(pref, apiService)
    }
    fun provideBuildingRepository(context: Context): BuildingRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.accessToken)
        val database = BuildingsDatabase.getDatabase(context)
        return BuildingRepository.getInstance(pref, apiService, database)
    }
}