package com.example.sahtek.navigation

sealed class Screen(val route: String){
    object Roleselection: Screen("role_selection")
    object Login: Screen("login")
    object Pationsignup: Screen("patio_signup")
    object doctor: Screen("doctor_signup")

    object Homepatient: Screen("home_patient")
    object AvailableSchedules: Screen("available_schedules")
    object PatientProfile: Screen("patient_profile")
    object Homedoctor: Screen("home_doctor")
    object DoctorProfile: Screen("doctor_profile")

    object EditProfile: Screen("edit_profile")

    // Analysis Feature
    object PatientAnalysisHome: Screen("patient_analysis_home")
    object AddPatientAnalysis: Screen("add_patient_analysis")
    object PatientAnalysisDetails: Screen("patient_analysis_details/{resultId}") {
        fun createRoute(resultId: String) = "patient_analysis_details/$resultId"
    }
}
