package com.example.sahtek.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.authservice.SessionManager
import com.example.sahtek.R
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.ui.auth.DoctorSignupScreen
import com.example.sahtek.ui.auth.LoginScreen
import com.example.sahtek.ui.auth.PatientSignupScreen
import com.example.sahtek.ui.auth.RoleSelectionScreen
import com.example.sahtek.ui.doctor.DoctorHomeScreen
import com.example.sahtek.ui.doctor.shedul.DoctorSetAvailabilityRoute
import com.example.sahtek.ui.home.AvailableSchedulesScreen
import com.example.sahtek.ui.home.PatientHomeScreen
import com.example.sahtek.ui.home.repository.RealPatientRepository
import com.example.sahtek.ui.profile.DoctorProfileRoute
import com.example.sahtek.ui.profile.PatientProfileRoute
import com.example.sahtek.ui.profile.EditProfileScreen
import com.example.sahtek.ui.profile.EditProfileViewModel
import com.example.sahtek.ui.profile.EditProfileViewModelFactory
import androidx.compose.runtime.collectAsState
import com.example.sahtek.ui.analysis.PatientAnalysisViewModel
import com.example.sahtek.ui.analysis.PatientAnalysisHomeScreen
import com.example.sahtek.ui.analysis.AddPatientAnalysisScreen
import com.example.sahtek.ui.analysis.PatientAnalysisDetailsScreen
import com.example.sahtek.ui.animationpage.SplashAnimationPage
import com.example.sahtek.ui.notification.NotificationScreen
import com.example.sahtek.data.notification.repository.NotificationRepositoryImpl
import com.example.sahtek.ui.consultation.ConsultationScreen
import com.example.sahtek.ui.consultation.ConsultationViewModel
import com.example.sahtek.ui.consultation.ConsultationViewModelFactory
import com.example.sahtek.reservation.repository.RealReservationRepository

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    var loginRole by rememberSaveable { mutableStateOf("PATIENT") }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    // Shared ViewModel for Analysis feature to ensure data is updated across screens
    val analysisViewModel: PatientAnalysisViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashAnimationPage(
                onAnimationComplete = {
                    if (sessionManager.isLoggedIn()) {
                        val role = sessionManager.getUserRole() ?: "PATIENT"
                        loginRole = role.uppercase()
                        val destination = if (loginRole == "DOCTOR") Screen.Homedoctor.route else Screen.Homepatient.route
                        navController.navigate(destination) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Roleselection.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Roleselection.route) {
            RoleSelectionScreen(
                brandHeaderRes = R.drawable.ic_logo,
                patientImageRes = R.drawable.img_1,
                doctorImageRes = R.drawable.img1_doctor,
                onPatientClick = {
                    loginRole = "PATIENT"
                    navController.navigate(Screen.Pationsignup.route)
                },
                onDoctorClick = {
                    loginRole = "DOCTOR"
                    navController.navigate(Screen.doctor.route)
                },
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                role = loginRole,
                brandHeaderRes = R.drawable.ic_logo,
                onLoginClick = { resolvedRole ->
                    if (resolvedRole.equals("DOCTOR", ignoreCase = true)) {
                        loginRole = "DOCTOR"
                        navController.navigate(Screen.Homedoctor.route) {
                            popUpTo(Screen.Roleselection.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        loginRole = "PATIENT"
                        navController.navigate(Screen.Homepatient.route) {
                            popUpTo(Screen.Roleselection.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.Roleselection.route)
                },
                onGoogleClick = {}
            )
        }

        composable(Screen.Pationsignup.route) {
            PatientSignupScreen(
                brandHeaderRes = R.drawable.ic_logo,
                googleIconRes = R.drawable.img,
                onCreateAccountClick = {
                    loginRole = "PATIENT"
                    navController.navigate(Screen.Login.route)
                },
                onLoginClick = {
                    loginRole = "PATIENT"
                    navController.navigate(Screen.Login.route)
                },
                onGoogleClick = {}
            )
        }

        composable(Screen.doctor.route) {
            DoctorSignupScreen(
                brandHeaderRes = R.drawable.ic_logo,
                googleIconRes = R.drawable.img,
                onCreateAccountClick = {
                    loginRole = "DOCTOR"
                    navController.navigate(Screen.Login.route)
                },
                onLoginClick = {
                    loginRole = "DOCTOR"
                    navController.navigate(Screen.Login.route)
                },
                onGoogleClick = {}
            )
        }

        composable(Screen.Homepatient.route) {
            PatientHomeScreen(
                analysisViewModel = analysisViewModel,
                onNotificationsClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.PatientProfile.route)
                },
                onSearchDoctorsClick = {
                    // Navigate to a search page or default list
                    navController.navigate(Screen.AvailableSchedules.createRoute("all"))
                },
                onNavigateToAddAnalysis = {
                    navController.navigate(Screen.AddPatientAnalysis.route)
                },
                onNavigateToDetails = { resultId ->
                    navController.navigate(Screen.PatientAnalysisDetails.createRoute(resultId))
                },
                onAppointmentsClick = {
                    // connect later when My Appointments page exists
                },
                onDoctorClick = { doctorId ->
                    navController.navigate(Screen.AvailableSchedules.createRoute(doctorId))
                },
                onConsultationsClick = { userId ->
                    navController.navigate(Screen.Consultation.createRoute(userId, "PATIENT"))
                },
                onTokenExpired = {
                    // Clear session and redirect to login
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homepatient.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Homedoctor.route) {
            DoctorHomeScreen(
                onNotificationsClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.DoctorProfile.route)
                },
                onSearchClick = {
                    // connect later when Doctor search exists
                },
                onFilterClick = {
                    // connect later when Doctor filters exist
                },
                onLoadMoreClick = {
                    // connect later when pagination exists
                },
                onSetAvailabilityClick = {
                    navController.navigate(Screen.DoctorSetAvailability.route)
                },
                onConsultationsClick = { userId ->
                    navController.navigate(Screen.Consultation.createRoute(userId, "DOCTOR"))
                },
                onTokenExpired = {
                    // Clear session and redirect to login
                    sessionManager.clearSession()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homedoctor.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.DoctorProfile.route) {
            DoctorProfileRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLogoutClick = {
                    loginRole = "DOCTOR"
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homedoctor.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.PatientProfile.route) {
            PatientProfileRoute(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLogoutClick = {
                    loginRole = "PATIENT"
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homepatient.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.EditProfile.route){
            val repository = RealPatientRepository(
                apiService = RetrofitClient.patientApiService,
                reservationApiService = RetrofitClient.reservationApiService,
                sessionManager = sessionManager
            )
            val viewModel: EditProfileViewModel = viewModel(
                factory = EditProfileViewModelFactory(repository)
            )
            val uiState by viewModel.uiState.collectAsState()

            EditProfileScreen(
                uiState = uiState,
                onBackClick = { navController.popBackStack() },
                onFullNameChange = viewModel::onFullNameChange,
                onPhoneNumberChange = viewModel::onPhoneNumberChange,
                onDateOfBirthChange = viewModel::onDateOfBirthChange,
                onGenderChange = viewModel::onGenderChange,
                onPasswordChange = viewModel::onPasswordChange,
                onSaveClick = viewModel::saveProfile,
                onSpecialityChange = viewModel::onSpecialityChange,
                onEstablishmentChange = viewModel::onEstablishmentChange
            )
        }

        composable(
            route = Screen.AvailableSchedules.route,
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            AvailableSchedulesScreen(
                doctorId = doctorId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    navController.navigate(Screen.Notifications.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.PatientProfile.route)
                }
            )
        }

        composable(Screen.DoctorSetAvailability.route) {
            DoctorSetAvailabilityRoute(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Analysis Feature ---
        composable(Screen.PatientAnalysisHome.route) {
            PatientAnalysisHomeScreen(
                viewModel = analysisViewModel,
                onNavigateToAddAnalysis = {
                    navController.navigate(Screen.AddPatientAnalysis.route)
                },
                onNavigateToDetails = { resultId ->
                    navController.navigate(Screen.PatientAnalysisDetails.createRoute(resultId))
                }
            )
        }

        composable(Screen.AddPatientAnalysis.route) {
            AddPatientAnalysisScreen(
                viewModel = analysisViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.PatientAnalysisDetails.route,
            arguments = listOf(navArgument("resultId") { type = NavType.StringType })
        ) { backStackEntry ->
            val resultId = backStackEntry.arguments?.getString("resultId") ?: ""
            PatientAnalysisDetailsScreen(
                resultId = resultId,
                viewModel = analysisViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- Notification Feature ---
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Consultation Feature ---
        composable(
            route = Screen.Consultation.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("role") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "PATIENT"

            val consultationRepository = remember {
                RealReservationRepository(RetrofitClient.reservationApiService)
            }
            val consultationViewModel: ConsultationViewModel = viewModel(
                factory = ConsultationViewModelFactory(consultationRepository)
            )

            val context = androidx.compose.ui.platform.LocalContext.current
            // Load meetings based on role
            LaunchedEffect(userId, role) {
                if (role.equals("DOCTOR", ignoreCase = true)) {
                    consultationViewModel.loadDoctorMeetings(context, userId)
                } else {
                    consultationViewModel.loadPatientMeetings(context, userId)
                }
            }

            ConsultationScreen(
                viewModel = consultationViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}
