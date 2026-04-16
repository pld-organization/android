package com.example.sahtek.data.doctor.model

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DoctorApiService {
    @GET("auth/doctors/ids")
    suspend fun getAllDoctors(
        @Header("Authorization") token: String
    ): Response<JsonElement>

    @GET("auth/doctor/{doctorId}")
    suspend fun getDoctorById(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("doctorId") doctorId: String
    ): Response<JsonElement>
}
