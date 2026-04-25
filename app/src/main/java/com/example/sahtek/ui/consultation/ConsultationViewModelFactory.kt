package com.example.sahtek.ui.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sahtek.reservation.repository.ReservationRepository

class ConsultationViewModelFactory(
    private val repository: ReservationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConsultationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ConsultationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
