package com.example.sahtek.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sahtek.reservation.repository.DoctorAvailabilityRepository

class DoctorAvailabilityViewModelFactory(
    private val repository: DoctorAvailabilityRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorAvailabilityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorAvailabilityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
