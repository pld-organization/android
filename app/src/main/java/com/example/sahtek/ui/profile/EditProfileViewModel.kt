package com.example.sahtek.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sahtek.ui.home.repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileForEditing()
    }

    private fun loadProfileForEditing() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val homeData = repository.getPatientHome()
            if (homeData.errorMessage == null) {
                _uiState.update {
                    it.copy(
                        fullName = homeData.patientname,
                        email = homeData.email,
                        phoneNumber = homeData.phoneNumber,
                        dateOfBirth = homeData.dateOfBirth ?: "",
                        gender = homeData.gender ?: "",
                        speciality = homeData.speciality ?: "",
                        establishment = homeData.establishment ?: "",
                        isDoctor = homeData.role == "DOCTOR",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = homeData.errorMessage) }
            }
        }
    }

    fun onFullNameChange(newName: String) = _uiState.update { it.copy(fullName = newName) }
    fun onPhoneNumberChange(newPhone: String) = _uiState.update { it.copy(phoneNumber = newPhone) }
    fun onDateOfBirthChange(newDob: String) = _uiState.update { it.copy(dateOfBirth = newDob) }
    fun onGenderChange(newGender: String) = _uiState.update { it.copy(gender = newGender) }
    fun onSpecialityChange(newSpeciality: String) = _uiState.update { it.copy(speciality = newSpeciality) }
    fun onEstablishmentChange(newEstablishment: String) = _uiState.update { it.copy(establishment = newEstablishment) }
    fun onPasswordChange(newPass: String) = _uiState.update { it.copy(password = newPass) }

    fun saveProfile() {
        val currentState = _uiState.value
        if (currentState.fullName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Full Name is required") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val names = currentState.fullName.trim().split(" ", limit = 2)
            
            val request = UpdateProfileRequest(
                email = currentState.email,
                firstName = names.getOrNull(0),
                lastName = names.getOrNull(1) ?: "",
                phoneNumber = currentState.phoneNumber.takeIf { it.isNotBlank() },
                dateOfBirth = if (!currentState.isDoctor) currentState.dateOfBirth.takeIf { it.isNotBlank() } else null,
                gender = if (!currentState.isDoctor) currentState.gender.takeIf { it.isNotBlank() } else null,
                speciality = if (currentState.isDoctor) currentState.speciality.takeIf { it.isNotBlank() } else null,
                establishment = if (currentState.isDoctor) currentState.establishment.takeIf { it.isNotBlank() } else null,
                role = if (currentState.isDoctor) "DOCTOR" else "PATIENT",
                password = currentState.password.takeIf { it.isNotBlank() }
            )

            when (val result = repository.updateProfile(request)) {
                is ProfileResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is ProfileResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
    
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
