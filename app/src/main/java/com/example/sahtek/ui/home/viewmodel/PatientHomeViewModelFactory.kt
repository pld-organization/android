package com.example.sahtek.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sahtek.ui.home.repository.PatientRepository

class PatientHomeViewModelFactory(
    private val repository: PatientRepository,
    private val onTokenExpired: (() -> Unit)? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientHomeViewModel::class.java)) {
            return PatientHomeViewModel(repository, onTokenExpired) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
