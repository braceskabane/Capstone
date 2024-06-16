package com.dicoding.picodiploma.loginwithanimation.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class SearchResponse(
	val data: List<DataItem?>? = null,
	val error: Boolean? = null,
	val message: String? = null
) : Parcelable

@Parcelize
data class DataItem(
	val propertyImage: List<String?>? = null,
	val propertyName: String? = null,
	val year: String? = null,
	val landArea: String? = null,
	val buildingArea: String? = null,
	val description: String? = null,
	val createdAt: String? = null,
	val location: String? = null,
	val id: String? = null,
	val floor: String? = null,
	val bathroom: String? = null,
	val bedroom: String? = null
) : Parcelable
