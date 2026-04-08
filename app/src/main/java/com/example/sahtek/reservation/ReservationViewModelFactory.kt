package com.example.sahtek.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.authservice.SessionManager
import com.example.sahtek.reservation.repository.ReservationRepository

class ReservationViewModelFactory(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservationViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
