package com.example.sahtek.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.reservation.repository.ReservationRepository
import com.example.sahtek.ui.home.AvailableScheduleUi
import com.example.sahtek.ui.notification.ReservationNotificationHelper
import com.example.sahtek.data.notification.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ReservationViewModel(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    fun fetchAvailableSlots(doctorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val token = sessionManager.getAuthToken() ?: ""
            val availableSlots = repository.getAvailableTimeSlots(token, doctorId)
            
            val uiSlots = availableSlots.map { slot ->
                AvailableScheduleUi(
                    id = slot.id ?: "",
                    day = slot.dayOfWeek?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "N/A",
                    hour = "${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}",
                    rawDay = slot.dayOfWeek ?: "",
                    rawStartTime = slot.startTime ?: ""
                )
            }
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                availableSlots = uiSlots
            )
        }
    }

    private fun formatTime(time: String?): String {
        if (time == null) return "--:--"
        // time is likely HH:mm:ss or HH:mm
        return if (time.length >= 5) time.substring(0, 5) else time
    }

    fun createReservation(
        doctorId: String,
        patientId: String,
        reservationDay: String,
        reservationTime: String,
        reason: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            
            val token = sessionManager.getAuthToken() ?: ""
            // Ensure time has seconds if needed by backend, though HH:mm often works
            val formattedTime = if (reservationTime.length == 5) "$reservationTime:00" else reservationTime

            val request = CreateReservationRequestDto(
                doctorId = doctorId,
                patientId = patientId,
                reservationDay = reservationDay,
                reservationTime = formattedTime,
                reason = reason
            )

            val result = repository.createReservation(token, request)
            
            when (result) {
                is ReservationResult.Success -> {
                    // Trigger notification to doctor after successful reservation
                    viewModelScope.launch {
                        try {
                            ReservationNotificationHelper.notifyReservationCreated(
                                repository = notificationRepository,
                                reservationId = result.data.data?.reservationId ?: "",
                                doctorId = doctorId,
                                patientId = patientId,
                                reservationDay = reservationDay,
                                reservationTime = formattedTime,
                                meetingUrl = "", // TODO: Add meeting URL if available
                                reason = reason
                            )
                        } catch (e: Exception) {
                            // Log error but don't fail the reservation
                            // The reservation was successful, notification failure shouldn't affect it
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = result.data.message ?: "Reservation created successfully"
                    )
                }
                is ReservationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(successMessage = null, errorMessage = null)
    }
}
