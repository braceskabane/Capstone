package com.dicoding.picodiploma.loginwithanimation.data.lokal.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding

@Dao
interface BuildingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(build: FavoriteBuilding)

    @Update
    fun update(build: FavoriteBuilding)

    @Delete
    fun delete(build: FavoriteBuilding)

    @Query("SELECT * from favoritebuilding ORDER BY id ASC")
    fun getAllBuilding(): LiveData<List<FavoriteBuilding>>

    @Query("SELECT * FROM favoritebuilding WHERE id = :id")
    fun getFavoriteBuildingById(id: String): LiveData<FavoriteBuilding>
}
