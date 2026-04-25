package com.example.sahtek.ui.consultation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sahtek.reservation.repository.ReservationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConsultationViewModel(
    private val repository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConsultationUiState())
    val uiState: StateFlow<ConsultationUiState> = _uiState.asStateFlow()

    fun loadPatientMeetings(context: Context, patientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            val result = repository.getPatientMeetings(patientId)
            result.onSuccess { meetings ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meetings = meetings,
                    errorMessage = if (meetings.isEmpty()) "No consultations found" else null
                )
                // Schedule reminders for online meetings
                meetings.forEach { meeting ->
                    if (meeting.meetingUrl.isNotBlank()) {
                        com.example.sahtek.ui.notification.MeetingReminderHelper.scheduleReminder(
                            context = context,
                            reservationId = meeting.reservationId,
                            doctorName = "votre médecin",
                            meetingUrl = meeting.meetingUrl,
                            dateString = meeting.day,
                            timeString = meeting.startTime
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to load consultations"
                )
            }
        }
    }

    fun loadDoctorMeetings(context: Context, doctorId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            val result = repository.getDoctorMeetings(doctorId)
            result.onSuccess { meetings ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meetings = meetings,
                    errorMessage = if (meetings.isEmpty()) "No consultations found" else null
                )
                // Schedule reminders for online meetings
                meetings.forEach { meeting ->
                    if (meeting.meetingUrl.isNotBlank()) {
                        com.example.sahtek.ui.notification.MeetingReminderHelper.scheduleReminder(
                            context = context,
                            reservationId = meeting.reservationId,
                            doctorName = "votre patient",
                            meetingUrl = meeting.meetingUrl,
                            dateString = meeting.day,
                            timeString = meeting.startTime
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.message ?: "Failed to load consultations"
                )
            }
        }
    }

    fun selectMeetingUrl(url: String) {
        _uiState.value = _uiState.value.copy(selectedMeetingUrl = url)
    }

    fun openMeeting(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Could not open meeting link: ${e.message}"
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
}
