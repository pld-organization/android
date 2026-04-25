package com.example.sahtek.ui.doctor.pation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.reservation.ReservationApiService
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.ui.home.viewmodel.PatientApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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
    private val reservationApiService: ReservationApiService,
    private val patientApiService: PatientApiService,
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
                val response = reservationApiService.getDoctorReservations("Bearer $token", doctorId)
                if (response.isSuccessful) {
                    val reservations = response.body() ?: emptyList()
                    val patientList = mapReservationsToUi(token, reservations)
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

    private suspend fun mapReservationsToUi(token: String, reservations: List<ReservationResponseDto>): List<DoctorPatientUi> {
        return reservations.mapIndexed { index, res ->
            val patientName = fetchPatientName(token, res.patientId ?: "")
            DoctorPatientUi(
                id = res.id ?: "res_$index",
                order = index + 1,
                date = formatReservationDate(res.reservationDay),
                fullName = patientName,
                consultationType = "Appointment",
                resultLabel = "Reason: ${res.reason ?: "No reason"}",
                resultTone = DoctorPatientTone.Info,
                statusLabel = res.reservationStatus,
                statusTone = when (res.reservationStatus) {
                    "COMPLETED", "CONFIRMED" -> DoctorPatientTone.Safe
                    "CANCELLED" -> DoctorPatientTone.Warning
                    else -> DoctorPatientTone.Info
                }
            )
        }
    }

    private fun formatReservationDate(rawDate: String?): String {
        if (rawDate == null) return "N/A"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
            val date = inputFormat.parse(rawDate)
            date?.let { outputFormat.format(it) } ?: rawDate
        } catch (e: Exception) {
            // If it's a day name like "MONDAY"
            rawDate.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    private suspend fun fetchPatientName(token: String, patientId: String): String {
        if (patientId.isBlank()) return "Unknown Patient"
        return try {
            val response = patientApiService.getPatientById("Bearer $token", patientId)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    "${body.firstName} ${body.lastName}"
                } else {
                    "Patient ${patientId.takeLast(4)}"
                }
            } else {
                "Patient ${patientId.takeLast(4)}"
            }
        } catch (e: Exception) {
            Log.e("DoctorPationViewModel", "Error fetching patient name", e)
            "Patient ${patientId.takeLast(4)}"
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
