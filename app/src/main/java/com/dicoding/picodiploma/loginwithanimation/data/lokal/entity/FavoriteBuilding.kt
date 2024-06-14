package com.dicoding.picodiploma.loginwithanimation.data.lokal.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FavoriteBuilding(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "propertyName")
    var propertyName: String? = null,
    @ColumnInfo(name = "photoBuild")
    var photoBuild: String? = null,
//    @ColumnInfo(name = "price")
//    var price: String? = null,
    @ColumnInfo(name = "location")
    var location: String? = null,
    @ColumnInfo(name = "bedroom")
    var bedRoom: String? = null,
    @ColumnInfo(name = "bathroom")
    var bathRoom: String? = null,
    @ColumnInfo(name = "surfaceArea")
    var surfaceArea: String? = null,
    @ColumnInfo(name = "buildingArea")
    var buildingArea: String? = null
): Parcelable