package com.example.sahtek.data.doctor.model

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for Doctor information.
 * Based on the typical response from the auth service.
 */
data class DoctorDto(
    @SerializedName(value = "_id", alternate = ["id"]) val id: String = "",
    @SerializedName(value = "name", alternate = ["fullName", "doctorName"]) val displayName: String? = null,
    @SerializedName(value = "firstName", alternate = ["firstname"]) val firstName: String? = null,
    @SerializedName(value = "lastName", alternate = ["lastname"]) val lastName: String? = null,
    @SerializedName(value = "speciality", alternate = ["specialty"]) val speciality: String? = null,
    val establishment: String? = null,
    val email: String? = null
) {
    val fullName: String
        get() {
            val explicitName = displayName?.trim().orEmpty()
            val resolvedName = listOf(firstName, lastName)
                .filter { !it.isNullOrBlank() }
                .joinToString(" ")

            return if (explicitName.isNotBlank()) {
                explicitName
            } else if (resolvedName.isNotBlank()) {
                "Dr. $resolvedName"
            } else if (id.isNotBlank()) {
                "Doctor ${id.takeLast(6)}"
            } else {
                "Doctor"
            }
        }

    val hasHumanName: Boolean
        get() = !displayName.isNullOrBlank() || !firstName.isNullOrBlank() || !lastName.isNullOrBlank()
}
