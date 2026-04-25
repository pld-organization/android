package com.example.sahtek.navigation

sealed class Screen(val route: String){
    object Splash: Screen("splash")
    object Roleselection: Screen("role_selection")
    object Login: Screen("login")
    object Pationsignup: Screen("patio_signup")
    object doctor: Screen("doctor_signup")

    object Homepatient: Screen("home_patient")
    object AvailableSchedules: Screen("available_schedules/{doctorId}") {
        fun createRoute(doctorId: String) = "available_schedules/$doctorId"
    }
    object PatientProfile: Screen("patient_profile")
    object Homedoctor: Screen("home_doctor")
    object DoctorProfile: Screen("doctor_profile")

    object EditProfile: Screen("edit_profile")

    // Doctor Schedule Feature
    object DoctorSetAvailability: Screen("doctor_set_availability")

    // Analysis Feature
    object PatientAnalysisHome: Screen("patient_analysis_home")
    object AddPatientAnalysis: Screen("add_patient_analysis")
    object PatientAnalysisDetails: Screen("patient_analysis_details/{resultId}") {
        fun createRoute(resultId: String) = "patient_analysis_details/$resultId"
    }

    // Notification Feature
    object Notifications: Screen("notifications")

    // Consultation Feature
    object Consultation : Screen("consultation/{userId}/{role}") {
        fun createRoute(userId: String, role: String) = "consultation/$userId/$role"
    }
}
