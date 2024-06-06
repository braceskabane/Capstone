package com.dicoding.picodiploma.loginwithanimation.data

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import com.dicoding.picodiploma.loginwithanimation.data.lokal.room.BuildingDao
import com.dicoding.picodiploma.loginwithanimation.data.lokal.room.BuildingRoomDatabase
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.BuildingResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailBuildingResponse
import com.dicoding.picodiploma.loginwithanimation.data.utils.AppExecutor
import kotlinx.coroutines.flow.Flow

class BuildingRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    application: Application
) {
    //////////////////////////////////////////
    // Lokal
    private val mBuildDao: BuildingDao
    private val appExecutor: AppExecutor = AppExecutor()
    init {
        val db = BuildingRoomDatabase.getDatabase(application)
        mBuildDao = db.userDao()
    }
    fun getFavoriteUserById(id: String): LiveData<FavoriteBuilding> {
        return mBuildDao.getFavoriteBuildingById(id)
    }
    fun getAllBuilding(): LiveData<List<FavoriteBuilding>> = mBuildDao.getAllBuilding()
    fun insert(fav: FavoriteBuilding) {
        appExecutor.diskIO.execute {
            mBuildDao.insert(fav)
        }
    }
    fun delete(fav: FavoriteBuilding) {
        appExecutor.diskIO.execute {
            mBuildDao.delete(fav)
        }
    }

    //////////////////////////////////////////
    // Remote
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    // Menampilkan semua Building yang ada
    suspend fun getBuilding(): BuildingResponse {
        return apiService.getBuilding()
    }

    suspend fun getDetailBuilding(location: String): DetailBuildingResponse {
        return apiService.getDetailBuilding(location)
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            application: Application
        ) = BuildingRepository(userPreference, apiService, application)
    }
}