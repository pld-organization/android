package com.example.sahtek.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sahtek.reservation.repository.DoctorAvailabilityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorAvailabilityViewModel(
    private val repository: DoctorAvailabilityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorAvailabilityUiState())
    val uiState: StateFlow<DoctorAvailabilityUiState> = _uiState.asStateFlow()

    fun createSchedules(token: String, schedules: List<CreateScheduleRequestDto>) {
        val validationError = when {
            token.isBlank() -> "Authentication token is missing. Please log in again."
            schedules.isEmpty() -> "Add at least one enabled availability slot before saving."
            else -> schedules.firstNotNullOfOrNull(::validateSchedule)
        }

        if (validationError != null) {
            _uiState.value = DoctorAvailabilityUiState(errorMessage = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = DoctorAvailabilityUiState(isLoading = true)

            var createdCount = 0
            var lastSuccessMessage: String? = null
            val authToken = if (token.startsWith("Bearer ")) token else "Bearer $token"

            for (schedule in schedules) {
                when (val result = repository.createDoctorSchedule(authToken, schedule)) {
                    is DoctorAvailabilityResult.Success -> {
                        createdCount += 1
                        lastSuccessMessage = result.message
                    }

                    is DoctorAvailabilityResult.Error -> {
                        val errorMessage = if (createdCount > 0) {
                            "Saved $createdCount slot(s) before failure. ${result.message}"
                        } else {
                            result.message
                        }
                        _uiState.value = DoctorAvailabilityUiState(errorMessage = errorMessage)
                        return@launch
                    }
                }
            }

            val successMessage = if (createdCount == 1) {
                lastSuccessMessage ?: "Availability saved successfully."
            } else {
                "$createdCount availability slots saved successfully."
            }

            _uiState.value = DoctorAvailabilityUiState(successMessage = successMessage)
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }

    private fun validateSchedule(schedule: CreateScheduleRequestDto): String? {
        if (schedule.doctorId.isBlank()) {
            return "Doctor profile is still loading. Try again in a moment."
        }

        if (schedule.dayOfWeek.isBlank()) {
            return "Day of week is required."
        }

        // Updated regex to allow both HH:mm and HH:mm:ss
        if (!TIME_REGEX.matches(schedule.startTime)) {
            return "Start time must use HH:mm format."
        }

        if (!TIME_REGEX.matches(schedule.endTime)) {
            return "End time must use HH:mm format."
        }

        // Compare using prefix to ignore seconds for validation if necessary, 
        // or just compare strings if both have same format.
        if (schedule.startTime >= schedule.endTime) {
            return "End time must be after start time for ${schedule.dayOfWeek}."
        }

        if (schedule.appointmentType !in ALLOWED_APPOINTMENT_TYPES) {
            return "Unsupported appointment type: ${schedule.appointmentType}."
        }

        return null
    }

    private companion object {
        val TIME_REGEX = Regex("^([01]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d)?$")
        val ALLOWED_APPOINTMENT_TYPES = setOf("ATTENDANCE", "ONLINE")
    }
}
