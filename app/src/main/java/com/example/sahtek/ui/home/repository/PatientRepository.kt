package com.example.sahtek.ui.home.repository

import com.example.sahtek.ui.home.viewmodel.PationHome
import com.example.sahtek.ui.profile.ProfileResult
import com.example.sahtek.ui.profile.UpdateProfileRequest

interface PatientRepository{
    suspend fun getPatientHome(): PationHome

    suspend fun updateProfile(request: UpdateProfileRequest): ProfileResult
}
