package com.example.sahtek.ui.doctor.pation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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
    val searchQuery: String = "",
    val allPatients: List<DoctorPatientUi> = emptyList(),
    val filteredPatients: List<DoctorPatientUi> = emptyList()
)

class DoctorPationViewModel : ViewModel() {
    private val patients = samplePatients()

    private val _uiState = MutableStateFlow(
        DoctorPatientsUiState(
            allPatients = patients,
            filteredPatients = patients
        )
    )
    val uiState: StateFlow<DoctorPatientsUiState> = _uiState.asStateFlow()

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
            patient.fullName.contains(normalizedQuery, ignoreCase = true)
        }
    }

    private companion object {
        fun samplePatients() = listOf(
            DoctorPatientUi(
                id = "1",
                order = 1,
                date = "27 Dec, 2023",
                fullName = "Ahmed Benali",
                consultationType = "IRL",
                resultLabel = "No risk detected",
                resultTone = DoctorPatientTone.Safe,
                statusLabel = "Pending",
                statusTone = DoctorPatientTone.Warning
            ),
            DoctorPatientUi(
                id = "2",
                order = 2,
                date = "02 Mar, 2026",
                fullName = "Mohamed Kherfi",
                consultationType = "Online",
                resultLabel = "Moderate risk detected",
                resultTone = DoctorPatientTone.Warning,
                statusLabel = "Completed",
                statusTone = DoctorPatientTone.Safe,
                note = "Meetings confirmed"
            ),
            DoctorPatientUi(
                id = "3",
                order = 3,
                date = "02 Mar, 2026",
                fullName = "Lina Haddad",
                consultationType = "IRL",
                resultLabel = "Normal result",
                resultTone = DoctorPatientTone.Info,
                statusLabel = "Completed",
                statusTone = DoctorPatientTone.Safe
            ),
            DoctorPatientUi(
                id = "4",
                order = 4,
                date = "02 Mar, 2026",
                fullName = "Samir Meziane",
                consultationType = "IRL",
                resultLabel = "Moderate risk detected",
                resultTone = DoctorPatientTone.Warning,
                statusLabel = "Pending",
                statusTone = DoctorPatientTone.Warning
            )
        )
    }
}
