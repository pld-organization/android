package com.example.sahtek.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sahtek.data.doctor.model.DoctorDto
import com.example.sahtek.data.doctor.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DoctorUiState {
    object Loading : DoctorUiState()
    data class Success(val doctors: List<DoctorDto>) : DoctorUiState()
    data class Error(val message: String) : DoctorUiState()
}

class DoctorViewModel(private val repository: DoctorRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DoctorUiState>(DoctorUiState.Loading)
    val uiState: StateFlow<DoctorUiState> = _uiState.asStateFlow()

    init {
        fetchDoctors()
    }

    fun fetchDoctors() {
        viewModelScope.launch {
            _uiState.value = DoctorUiState.Loading
            repository.getDoctors()
                .onSuccess { doctors ->
                    _uiState.value = DoctorUiState.Success(doctors)
                }
                .onFailure { error ->
                    _uiState.value = DoctorUiState.Error(error.message ?: "Unknown Error")
                }
        }
    }
}

class DoctorViewModelFactory(private val repository: DoctorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
