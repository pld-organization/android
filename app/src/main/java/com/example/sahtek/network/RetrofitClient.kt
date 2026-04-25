package com.example.sahtek.network

import com.example.sahtek.data.auth.model.AuthApiService
import com.example.sahtek.data.doctor.model.DoctorApiService
import com.example.sahtek.data.notification.api.NotificationApiService
import com.example.sahtek.reservation.ReservationApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.sahtek.ui.home.viewmodel.PatientApiService

object RetrofitClient {

    private const val BASE_URL = "https://authservice-version-90.onrender.com/"
    private const val RESERVATION_BASE_URL = "https://reservation-service-f8ik.onrender.com/"
    private const val NOTIFICATION_BASE_URL = "https://notification-bagz.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val authApiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    val doctorApiService: DoctorApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DoctorApiService::class.java)
    }

    val patientApiService: PatientApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PatientApiService::class.java)
    }

    val reservationApiService: ReservationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(RESERVATION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReservationApiService::class.java)
    }

    val notificationApiService: NotificationApiService by lazy {
        Retrofit.Builder()
            .baseUrl(NOTIFICATION_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationApiService::class.java)
    }
}
