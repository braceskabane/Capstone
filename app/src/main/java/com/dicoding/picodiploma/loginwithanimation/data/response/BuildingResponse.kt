package com.dicoding.picodiploma.loginwithanimation.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class BuildingResponse(
	@field:SerializedName("listBuilding")
	val listBuilding: List<ListBuildingItem> = emptyList(),
	@field:SerializedName("error")
	val error: Boolean? = null,
	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
data class ListBuildingItem(
	@field:SerializedName("photoBuilding")
	val photoBuilding: List<String>? = null,
	@field:SerializedName("photoContact")
	val photoContact: String? = null,
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
	@field:SerializedName("id")
	val id: String? = null,
) : Parcelable





