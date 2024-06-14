package com.dicoding.picodiploma.loginwithanimation.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailBuildingResponse(
    @field:SerializedName("propertyImage")
    val propertyImage: List<String?>? = null,
    @field:SerializedName("propertyName")
    val propertyName: String? = null,
    @field:SerializedName("location")
    val location: String? = null,
    @field:SerializedName("created_at")
    val createdAt: String? = null,
    @field:SerializedName("bedroom")
    val bedroom: String? = null,
    @field:SerializedName("bathroom")
    val bathroom: String? = null,
    @field:SerializedName("landArea")
    val landArea: String? = null,
    @field:SerializedName("buildingArea")
    val buildingArea: String? = null,
    @field:SerializedName("floor")
    val floor: String? = null,
    @field:SerializedName("year")
    val year: String? = null,
//	@field:SerializedName("price")
//	val price: String? = null,
    @field:SerializedName("description")
    val description: String? = null,
    @field:SerializedName("id")
    val id: String? = null,
) : Parcelable