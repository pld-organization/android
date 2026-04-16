package com.example.sahtek.data.doctor.repository

import com.example.sahtek.data.doctor.model.DoctorDto

interface DoctorRepository {
    suspend fun getDoctors(): Result<List<DoctorDto>>
}
