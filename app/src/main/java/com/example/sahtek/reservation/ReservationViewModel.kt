package com.example.sahtek.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.reservation.repository.ReservationRepository
import com.example.sahtek.ui.home.AvailableScheduleUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ReservationViewModel(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager
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
                    id = slot.id,
                    day = formatDate(slot.dayOfWeek),
                    hour = "${slot.startTime} to ${slot.endTime}"
                )
            }
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                availableSlots = uiSlots
            )
        }
    }

    private fun formatDate(dayOfWeek: String): String {
        return try {
            val inputFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dayOfWeek)
            outputFormat.format(date ?: return dayOfWeek)
        } catch (e: Exception) {
            dayOfWeek
        }
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
            // Adding :00 to seconds if not present, as backend might require HH:mm:ss
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
