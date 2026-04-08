package com.example.sahtek.reservation

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ReservationApiService {
    @POST("create")
    suspend fun createReservation(
        @Header("Authorization") token: String,
        @Body request: CreateReservationRequestDto
    ): CreateReservationResponseDto
}
