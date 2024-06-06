package com.dicoding.picodiploma.loginwithanimation.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailBuildingResponse(
    @field:SerializedName("id")
    val id: String? = null,
    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,
    @field:SerializedName("name")
    val name: String? = null,
    @field:SerializedName("type")
    val type: Double? = null,
    @field:SerializedName("location")
    val location: String? = null,
    @field:SerializedName("createdAt")
    val createdAt: String? = null,
    @field:SerializedName("bedRoom")
    val bedRoom: String? = null,
    @field:SerializedName("bathRoom")
    val bathRoom: String? = null,
    @field:SerializedName("surfaceArea")
    val surfaceArea: String? = null,
    @field:SerializedName("buildingArea")
    val buildingArea: String? = null,
    @field:SerializedName("priceProperty")
    val priceProperty: String? = null
) : Parcelable