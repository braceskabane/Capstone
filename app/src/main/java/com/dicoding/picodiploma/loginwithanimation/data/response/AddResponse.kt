package com.dicoding.picodiploma.loginwithanimation.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class AddResponse(
	val data: Add? = null,
	val error: Boolean? = null,
	val message: String? = null
) : Parcelable

@Parcelize
data class Add(
	val propertyImage: List<String?>? = null,
	val propertyName: String? = null,
	val year: String? = null,
	val landArea: String? = null,
	val description: String? = null,
	val buildingArea: String? = null,
	val createdAt: String? = null,
	val location: String? = null,
	val id: String? = null,
	val floor: String? = null,
	val bedroom: String? = null,
	val bathroom: String? = null
) : Parcelable
