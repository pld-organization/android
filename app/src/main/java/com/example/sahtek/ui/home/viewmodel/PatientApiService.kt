package com.example.sahtek.ui.home.viewmodel

import com.example.sahtek.ui.profile.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface PatientApiService {
    @GET("/auth/profile")
    suspend fun getPatientProfile(
        @Header("Authorization") authorization: String
    ): Response<PatientProfileResponse>

    @PATCH("/auth/profile")
    suspend fun updatePatientProfile(
        @Header("Authorization") authorization: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>


}
