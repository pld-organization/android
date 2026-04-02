package com.example.sahtek.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sahtek.data.auth.model.AuthResult
import com.example.sahtek.data.auth.model.PatientSignupRequest
import com.example.sahtek.data.auth.repository.AuthRepository
import com.example.sahtek.data.auth.repository.FakeAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientSignupViewModel(
    private val repository: AuthRepository = FakeAuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            firstName = value,
            errorMessage = null
        )
    }

    fun onLastNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            lastName = value,
            errorMessage = null
        )
    }

    fun onGenderChange(value: String) {
        _uiState.value = _uiState.value.copy(
            selectedGender = value,
            errorMessage = null
        )
    }

    fun onBloodTypeChange(value: String) {
        _uiState.value = _uiState.value.copy(
            bloodType = value,
            errorMessage = null
        )
    }

    fun onPhoneNumberChange(value: String) {
        _uiState.value = _uiState.value.copy(
            phoneNumber = value,
            errorMessage = null
        )
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            errorMessage = null
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            errorMessage = null
        )
    }

    fun signup() {
        val firstName = _uiState.value.firstName.trim()
        val lastName = _uiState.value.lastName.trim()
        val gender = _uiState.value.selectedGender.trim()
        val bloodType = _uiState.value.bloodType.trim()
        val phoneNumber = _uiState.value.phoneNumber.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (firstName.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "First name is required")
            return
        }

        if (lastName.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Last name is required")
            return
        }

        if (phoneNumber.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Phone number is required")
            return
        }

        if (bloodType.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Blood type is required")
            return
        }

        if (gender.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Gender is required")
            return
        }

        if (email.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email is required")
            return
        }

        if (password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password is required")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isSignupSuccess = false
            )

            val result = repository.signupPatient(
                PatientSignupRequest(
                    email = email,
                    password = password,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    bloodType = bloodType,
                    gender = gender
                )
            )

            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSignupSuccess = true
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun resetSignupState() {
        _uiState.value = _uiState.value.copy(isSignupSuccess = false)
    }
}
