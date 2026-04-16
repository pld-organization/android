package com.example.sahtek.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.ui.home.repository.RealPatientRepository
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModel
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModelFactory
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekBlueLight
import com.example.sahtek.ui.theme.SahtekBorder
import com.example.sahtek.ui.theme.SahtekSurface

@Composable
fun PatientProfileRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current.applicationContext
    val repository = remember {
        RealPatientRepository(
            apiService = RetrofitClient.patientApiService,
            reservationApiService = RetrofitClient.reservationApiService,
            sessionManager = SessionManager(context)
        )
    }
    val factory = remember(repository) { PatientHomeViewModelFactory(repository) }
    val homeViewModel: PatientHomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                homeViewModel.refreshHome()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ProfileScreen(
        modifier = modifier,
        fullName = uiState.patientname.ifBlank { "Patient" },
        email = uiState.email.ifBlank { "Not added yet" },
        phoneNumber = uiState.phoneNumber.ifBlank { "Not added yet" },
        dateOfBirth = uiState.dateOfBirth.ifBlank { "Not added yet" },
        gender = uiState.gender
            .takeIf { it.isNotBlank() }
            ?.lowercase()
            ?.replaceFirstChar { it.uppercase() }
            ?: "Not added yet",
        onBackClick = onBackClick,
        onEditProfileClick = onEditProfileClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun DoctorProfileRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current.applicationContext
    val repository = remember {
        RealPatientRepository(
            apiService = RetrofitClient.patientApiService,
            reservationApiService = RetrofitClient.reservationApiService,
            sessionManager = SessionManager(context)
        )
    }
    val factory = remember(repository) { PatientHomeViewModelFactory(repository) }
    val homeViewModel: PatientHomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()

    // Refresh data every time the screen is shown
    LaunchedEffect(Unit) {
        homeViewModel.refreshHome()
    }

    DoctorProfileScreen(
        modifier = modifier,
        fullName = uiState.patientname.ifBlank { "Doctor" },
        email = uiState.email.ifBlank { "Not added yet" },
        speciality = uiState.speciality.ifBlank { "Not added yet" },
        establishment = uiState.establishment.ifBlank { "Not added yet" },
        role = uiState.role
            .takeIf { it.isNotBlank() }
            ?.lowercase()
            ?.replaceFirstChar { it.uppercase() }
            ?: "Doctor",
        onBackClick = onBackClick,
        onEditProfileClick = onEditProfileClick,
        onLogoutClick = onLogoutClick
    )
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    fullName: String = "Yahia Youssef",
    email: String = "yahia.youssef@gmail.com",
    phoneNumber: String = "+213 526 578 941",
    dateOfBirth: String = "12 May 1999",
    gender: String = "Male",
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val avatarLetter = fullName.firstOrNull()?.uppercaseChar()?.toString() ?: "P"

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(
                subtitle = "Patient information",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                ProfileHeroCard(
                    avatarLetter = avatarLetter,
                    fullName = fullName,
                    supportingText = "Patient account"
                )
            }

            item {
                ProfileDetailsCard(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    dateOfBirth = dateOfBirth,
                    gender = gender
                )
            }

            item {
                ProfileActionsCard(
                    onEditProfileClick = onEditProfileClick,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
fun DoctorProfileScreen(
    modifier: Modifier = Modifier,
    fullName: String = "Dr. Yasmina Cherif",
    email: String = "doctor@example.com",
    speciality: String = "Radiology",
    establishment: String = "Central Medical Clinic",
    role: String = "Doctor",
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val avatarLetter = fullName.firstOrNull()?.uppercaseChar()?.toString() ?: "D"

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(
                subtitle = "Doctor information",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                ProfileHeroCard(
                    avatarLetter = avatarLetter,
                    fullName = fullName,
                    supportingText = role
                )
            }

            item {
                DoctorProfileDetailsCard(
                    fullName = fullName,
                    email = email,
                    speciality = speciality,
                    establishment = establishment,
                    role = role
                )
            }

            item {
                ProfileActionsCard(
                    onEditProfileClick = onEditProfileClick,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    subtitle: String,
    onBackClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileHeroCard(
    avatarLetter: String,
    fullName: String,
    supportingText: String
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = SahtekSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(SahtekBlue, SahtekBlueDark)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarLetter,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileDetailsCard(
    fullName: String,
    email: String,
    phoneNumber: String,
    dateOfBirth: String,
    gender: String
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ProfileInfoRow(
                icon = Icons.Filled.Person,
                label = "Full Name",
                value = fullName
            )
            ProfileInfoRow(
                icon = Icons.Filled.Email,
                label = "Email",
                value = email
            )
            ProfileInfoRow(
                icon = Icons.Filled.Call,
                label = "Phone Number",
                value = phoneNumber
            )
            ProfileInfoRow(
                icon = Icons.Filled.CalendarMonth,
                label = "Date of Birth",
                value = dateOfBirth
            )
            ProfileInfoRow(
                icon = Icons.Filled.Person,
                label = "Gender",
                value = gender
            )
        }
    }
}

@Composable
private fun DoctorProfileDetailsCard(
    fullName: String,
    email: String,
    speciality: String,
    establishment: String,
    role: String
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Professional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            ProfileInfoRow(
                icon = Icons.Filled.Person,
                label = "Full Name",
                value = fullName
            )
            ProfileInfoRow(
                icon = Icons.Filled.Email,
                label = "Email",
                value = email
            )
            ProfileInfoRow(
                icon = Icons.Filled.MedicalServices,
                label = "Speciality",
                value = speciality
            )
            ProfileInfoRow(
                icon = Icons.Filled.Business,
                label = "Establishment",
                value = establishment
            )
            ProfileInfoRow(
                icon = Icons.Filled.Person,
                label = "Role",
                value = role
            )
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = SahtekBlueLight,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = SahtekBlueDark
            )
        }
        

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProfileActionsCard(
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahtekBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit profile"
                )
                Text(
                    text = "Edit Profile",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = SahtekBlueDark
                ),
                border = BorderStroke(1.dp, SahtekBorder)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Log out"
                )
                Text(
                    text = "Log Out",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
