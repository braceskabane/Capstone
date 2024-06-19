package com.dicoding.picodiploma.loginwithanimation.data.api

import com.dicoding.picodiploma.loginwithanimation.data.response.AddResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.BuildingResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailBuildingResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.ProfileResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.SearchResponse
import com.dicoding.picodiploma.loginwithanimation.data.response.TokenResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.File


interface ApiService {
    @Multipart
    @POST("post")
    suspend fun uploadBuilding(
        @Part files: List<MultipartBody.Part>,
        @Part("propertyName") propertyName: RequestBody,
        @Part("year") year: RequestBody,
        @Part("landArea") landArea: RequestBody,
        @Part("buildingArea") buildingArea: RequestBody,
        @Part("location") location: RequestBody,
        @Part("floor") floor: RequestBody,
        @Part("bedroom") bedroom: RequestBody,
        @Part("bathroom") bathroom: RequestBody,
        @Part("description") description: RequestBody
    ): AddResponse

    @GET("post")
    suspend fun getBuilding(): BuildingResponse

    @GET("post")
    suspend fun getBuildings(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): BuildingResponse

    @GET("post/search")
    suspend fun getSearch(
        @Query("q") q: String,
    ): SearchResponse

    @GET("post/{id}")
    suspend fun getDetailBuilding(
        @Path("id")id: String
    ): DetailBuildingResponse

    ///////////////////////////////////////////////////////////////////////////////
    @GET("users/me")
    suspend fun profile(): ProfileResponse

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse
}