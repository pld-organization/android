package com.example.sahtek.reservation

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApiService {
    @POST("reservation/create")
    suspend fun createReservation(
        @Header("Authorization") token: String,
        @Body request: CreateReservationRequestDto
    ): Response<CreateReservationResponseDto>

    @POST("reservation/create-schedule")
    suspend fun createDoctorSchedule(
        @Header("Authorization") token: String,
        @Body request: CreateScheduleRequestDto
    ): Response<ScheduleMutationResponseDto>

    @GET("reservation/doctor/{doctorId}")
    suspend fun getDoctorReservations(
        @Header("Authorization") token: String,
        @Path("doctorId") doctorId: String
    ): Response<List<ReservationResponseDto>>

    @GET("reservation/patient/{patientId}")
    suspend fun getPatientReservations(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: String
    ): Response<List<ReservationResponseDto>>

    @GET("reservation/available/{doctorId}")
    suspend fun getAvailableTimeSlots(
        @Header("Authorization") token: String,
        @Path("doctorId") doctorId: String
    ): Response<List<AvailableTimeSlotDto>>

    @POST("reservation/cancel/{reservationId}")
    suspend fun cancelReservation(
        @Header("Authorization") token: String,
        @Path("reservationId") reservationId: String
    ): Response<CancelReservationResponseDto>


    @GET("auth/doctors/ids")
    suspend fun getDoctorIds(
        @Header("Authorization") token: String
    ): Response<List<String>>
}
