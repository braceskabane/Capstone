package com.dicoding.picodiploma.loginwithanimation.data.lokal.pagging

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem

@Dao
interface BuildingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: List<ListBuildingItem>)

    @Query("SELECT * FROM buildings")
    fun getAllStory(): PagingSource<Int, ListBuildingItem>

    @Query("DELETE FROM buildings")
    suspend fun deleteAll()
}