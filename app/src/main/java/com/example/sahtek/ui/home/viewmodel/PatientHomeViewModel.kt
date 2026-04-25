package com.example.sahtek.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sahtek.exception.TokenExpiredException
import com.example.sahtek.ui.home.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PationHome(
    val id: String = "",
    val isLoading: Boolean = false,
    val patientname: String = "",
    val email: String = "",
    val role: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val speciality: String = "",
    val establishment: String = "",
    val unreadnotification: Int = 0,
    val nextAppointment: AppointmentUi? = null,
    val appointments: List<AppointmentUi> = emptyList(),
    val latestAiResult: AiResultUi? = null,
    val quickStats: HomeQuickStats = HomeQuickStats(),
    val errorMessage: String? = null
)

data class AppointmentUi(
    val id: String = "",
    val doctortName: String,
    val doctorSpeciality: String,
    val date: String,
    val time: String,
    val status: String = "PENDING",
    val doctorId: String = "",
    val patientId: String = ""
)

data class AiResultUi(
    val title: String,
    val summary: String,
    val confidence: String
)

data class HomeQuickStats(
    val upcomingCount: Int = 0,
    val reportsCount: Int = 0,
    val historyCount: Int = 0
)

class PatientHomeViewModel(
    private val repository: PatientRepository,
    private val onTokenExpired: (() -> Unit)? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PationHome(isLoading = true))
    val uiState: StateFlow<PationHome> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                _uiState.value = repository.getPatientHome()
            } catch (e: TokenExpiredException) {
                // Token expired, trigger callback to redirect to login
                onTokenExpired?.invoke()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error loading patient home"
                )
            }
        }
    }

    fun refreshHome() {
        loadHomeData()
    }
}
