package com.dicoding.picodiploma.loginwithanimation.data

import androidx.lifecycle.liveData
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.response.AddResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.BuildingResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailBuildingResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ProfileResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.TokenResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class BuildingRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }
    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getBuilding(): BuildingResponse {
        return apiService.getBuilding()
    }


    suspend fun getDetailBuilding(id: String): DetailBuildingResponse {
        return apiService.getDetailBuilding(id)
    }

    suspend fun profile(): ProfileResponse {
        return apiService.profile()
    }

    suspend fun refresh(refreshToken: String): TokenResponse {
        return apiService.refresh(refreshToken)
    }

    fun uploadBuilding(
        files: List<File>,
        propertyName: String,
        year: String,
        landArea: String,
        buildingArea: String,
        location: String,
        floor: String,
        bedroom: String,
        bathroom: String,
        description: String
    ) = liveData {
        emit(ResultState.Loading)

        val propertyNameBody = propertyName.toRequestBody("text/plain".toMediaType())
        val yearBody = year.toRequestBody("text/plain".toMediaType())
        val landAreaBody = landArea.toRequestBody("text/plain".toMediaType())
        val buildingAreaBody = buildingArea.toRequestBody("text/plain".toMediaType())
        val locationBody = location.toRequestBody("text/plain".toMediaType())
        val floorBody = floor.toRequestBody("text/plain".toMediaType())
        val bedroomBody = bedroom.toRequestBody("text/plain".toMediaType())
        val bathroomBody = bathroom.toRequestBody("text/plain".toMediaType())
        val descriptionBody = description.toRequestBody("text/plain".toMediaType())

        val multipartBodyList = files.map { file ->
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData(
                "propertyImage",
                file.name,
                requestImageFile
            )
        }

        try {
            val successResponse = apiService.uploadBuilding(
                multipartBodyList,
                propertyNameBody,
                yearBody,
                landAreaBody,
                buildingAreaBody,
                locationBody,
                floorBody,
                bedroomBody,
                bathroomBody,
                descriptionBody
            )
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, AddResponse::class.java)
            emit(errorResponse.message?.let { ResultState.Error(it) })
        }
    }

    companion object {
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
        ) = BuildingRepository(userPreference, apiService)
    }
}