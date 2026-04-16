package com.example.sahtek.ui.doctor.pation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.authservice.SessionManager
import com.example.sahtek.reservation.ReservationApiService

class DoctorPationViewModelFactory(
    private val apiService: ReservationApiService,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorPationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorPationViewModel(apiService, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
