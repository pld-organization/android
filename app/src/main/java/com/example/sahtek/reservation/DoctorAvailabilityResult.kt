package com.example.sahtek.reservation

sealed class DoctorAvailabilityResult {
    data class Success(val message: String) : DoctorAvailabilityResult()
    data class Error(val message: String) : DoctorAvailabilityResult()
}
