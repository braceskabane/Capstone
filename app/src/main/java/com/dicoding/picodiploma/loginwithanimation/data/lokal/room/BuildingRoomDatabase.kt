package com.dicoding.picodiploma.loginwithanimation.data.lokal.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding

@Database(entities = [FavoriteBuilding::class], version = 2)
abstract class BuildingRoomDatabase : RoomDatabase() {
    abstract fun userDao(): BuildingDao

    companion object {
        @Volatile
        private var INSTANCE: BuildingRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): BuildingRoomDatabase {
            if (INSTANCE == null) {
                synchronized(BuildingRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        BuildingRoomDatabase::class.java, "user_database"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                }
            }
            return INSTANCE as BuildingRoomDatabase
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE favorite_building ADD COLUMN new_column TEXT")
            }
        }
//        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE favorite_building ADD COLUMN another_new_column TEXT")
//            }
//        }
//        private val MIGRATION_1_3: Migration = object : Migration(1, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//            }
//        }
    }
}