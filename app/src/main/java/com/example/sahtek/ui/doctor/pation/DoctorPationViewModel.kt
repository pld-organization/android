package com.example.sahtek.ui.doctor.pation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.reservation.ReservationApiService
import com.example.sahtek.reservation.ReservationResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DoctorPatientTone {
    Info,
    Safe,
    Warning
}

data class DoctorPatientUi(
    val id: String,
    val order: Int,
    val date: String,
    val fullName: String,
    val consultationType: String,
    val resultLabel: String,
    val resultTone: DoctorPatientTone,
    val statusLabel: String,
    val statusTone: DoctorPatientTone,
    val note: String? = null
)

data class DoctorPatientsUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val allPatients: List<DoctorPatientUi> = emptyList(),
    val filteredPatients: List<DoctorPatientUi> = emptyList(),
    val errorMessage: String? = null
)

class DoctorPationViewModel(
    private val apiService: ReservationApiService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorPatientsUiState())
    val uiState: StateFlow<DoctorPatientsUiState> = _uiState.asStateFlow()

    fun loadPatients(doctorId: String) {
        if (doctorId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val token = sessionManager.getAuthToken() ?: ""
            
            try {
                val response = apiService.getDoctorReservations("Bearer $token", doctorId)
                if (response.isSuccessful) {
                    val reservations = response.body() ?: emptyList()
                    val patientList = mapReservationsToUi(reservations)
                    _uiState.update { it.copy(
                        isLoading = false,
                        allPatients = patientList,
                        filteredPatients = filterPatients(patientList, it.searchQuery)
                    )}
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load patients") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun mapReservationsToUi(reservations: List<ReservationResponseDto>): List<DoctorPatientUi> {
        return reservations.mapIndexed { index, res ->
            DoctorPatientUi(
                id = res.id,
                order = index + 1,
                date = res.reservationDay,
                fullName = "Patient ${res.patientId.takeLast(4)}", // We don't have patient names in the reservation DTO yet
                consultationType = "Appointment",
                resultLabel = "Reason: ${res.reason}",
                resultTone = DoctorPatientTone.Info,
                statusLabel = res.reservationStatus,
                statusTone = when (res.reservationStatus) {
                    "COMPLETED" -> DoctorPatientTone.Safe
                    "CANCELLED" -> DoctorPatientTone.Warning
                    else -> DoctorPatientTone.Info
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        val normalizedQuery = query.trimStart()
        _uiState.update { current ->
            current.copy(
                searchQuery = normalizedQuery,
                filteredPatients = filterPatients(
                    patients = current.allPatients,
                    query = normalizedQuery
                )
            )
        }
    }

    private fun filterPatients(
        patients: List<DoctorPatientUi>,
        query: String
    ): List<DoctorPatientUi> {
        if (query.isBlank()) return patients

        val normalizedQuery = query.trim()
        return patients.filter { patient ->
            patient.fullName.contains(normalizedQuery, ignoreCase = true) ||
            patient.resultLabel.contains(normalizedQuery, ignoreCase = true)
        }
    }
}
