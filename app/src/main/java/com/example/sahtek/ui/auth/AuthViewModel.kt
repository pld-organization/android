package com.example.sahtek.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.authservice.SessionManager
import com.example.sahtek.data.auth.model.AuthResult
import com.example.sahtek.data.auth.model.GoogleMobileAuthRequest
import com.example.sahtek.data.auth.model.LoginRequest
import com.example.sahtek.data.auth.repository.AuthRepository
import com.example.sahtek.data.auth.repository.FakeAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    private val repository: AuthRepository = FakeAuthRepository(
        sessionManager = sessionManager
    )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            errorMessage = null
        )
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(
            password = newPassword,
            errorMessage = null
        )
    }

    fun login(selectedRole: String = "") {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password.trim()

        if (email.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email is required"
            )
            return
        }

        if (password.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password is required"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isLoginSuccess = false,
                successMessage = null,
                currentUserRole = ""
            )

            val result = repository.login(
                LoginRequest(
                    email = email,
                    password = password
                )
            )

            when (result) {
                is AuthResult.Success -> {
                    val resolvedRole = result.role
                        ?.trim()
                        ?.uppercase()
                        ?.takeIf { it.isNotBlank() }
                        ?: selectedRole
                            .trim()
                            .uppercase()
                            .takeIf { it.isNotBlank() }
                        ?: sessionManager.getUserRole()
                            .orEmpty()
                            .trim()
                            .uppercase()

                    if (resolvedRole.isNotBlank()) {
                        sessionManager.saveUserRole(resolvedRole)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        successMessage = "Login successful",
                        currentUserRole = resolvedRole
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

    fun loginWithGoogle(idToken: String, role: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null,
                isLoginSuccess = false,
                currentUserRole = ""
            )

            val result = repository.googleMobileAuth(
                GoogleMobileAuthRequest(
                    role = role,
                    idToken = idToken
                )
            )

            when (result) {
                is AuthResult.Success -> {
                    val resolvedRole = result.role
                        ?.trim()
                        ?.uppercase()
                        ?.takeIf { it.isNotBlank() }
                        ?: role
                            .trim()
                            .uppercase()
                            .takeIf { it.isNotBlank() }
                        ?: sessionManager.getUserRole()
                            .orEmpty()
                            .trim()
                            .uppercase()

                    if (resolvedRole.isNotBlank()) {
                        sessionManager.saveUserRole(resolvedRole)
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoginSuccess = true,
                        successMessage = "Google login successful",
                        currentUserRole = resolvedRole
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

    fun resetLoginState() {
        _uiState.value = _uiState.value.copy(
            isLoginSuccess = false,
            currentUserRole = ""
        )
    }
}
