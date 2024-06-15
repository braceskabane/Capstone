package com.dicoding.picodiploma.loginwithanimation.data.lokal.pagging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem

@Database(
    entities = [ListBuildingItem::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BuildingsDatabase : RoomDatabase() {

    abstract fun buildingsDao(): BuildingsDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: BuildingsDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): BuildingsDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BuildingsDatabase::class.java, "build_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}