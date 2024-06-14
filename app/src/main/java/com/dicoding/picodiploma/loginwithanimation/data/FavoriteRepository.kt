package com.dicoding.picodiploma.loginwithanimation.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import com.dicoding.picodiploma.loginwithanimation.data.lokal.room.BuildingDao
import com.dicoding.picodiploma.loginwithanimation.data.lokal.room.BuildingRoomDatabase
import com.dicoding.picodiploma.loginwithanimation.data.utils.AppExecutor

class FavoriteRepository(application: Application) {
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
            try {
                mBuildDao.insert(fav)
            } catch (e: Exception) {
                Log.e("BuildingRepository", "Error inserting building: ${e.message}", e)
            }
        }
    }

    fun delete(fav: FavoriteBuilding) {
        appExecutor.diskIO.execute {
            try {
                mBuildDao.delete(fav)
            } catch (e: Exception) {
                Log.e("BuildingRepository", "Error deleting building: ${e.message}", e)
            }
        }
    }
}