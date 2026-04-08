package com.example.sahtek.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.reservation.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReservationViewModel(
    private val repository: ReservationRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationUiState())
    val uiState: StateFlow<ReservationUiState> = _uiState.asStateFlow()

    fun createReservation(
        doctorId: String,
        patientId: String,
        reservationDate: String,
        reservationTime: String,
        reason: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
            
            val token = sessionManager.getAuthToken() ?: ""
            val request = CreateReservationRequestDto(
                doctorId = doctorId,
                patientId = patientId,
                reservationDate = reservationDate,
                reservationTime = reservationTime,
                reason = reason,
                reservationStatus = "PENDING"
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
