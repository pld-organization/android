package com.example.sahtek.ui.doctor

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.ui.doctor.pation.DoctorPatientsPage
import com.example.sahtek.ui.home.repository.RealPatientRepository
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModel
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModelFactory
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekBlueLight
import com.example.sahtek.ui.theme.SahtekBorder

private data class DoctorTab(
    val label: String,
    val icon: ImageVector
)

private val doctorTabs = listOf(
    DoctorTab("Overview", Icons.Filled.Dashboard),
    DoctorTab("Appointments", Icons.Filled.ContentPaste),
    DoctorTab("Patients", Icons.Filled.Groups),
    DoctorTab("Schedule", Icons.Filled.CalendarMonth),
    DoctorTab("Consultation", Icons.Filled.MedicalServices)
)

@Composable
fun DoctorHomeScreen(
    doctorName: String = "Dr. Yasmina Cherif",
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onLoadMoreClick: () -> Unit = {},
    onSetAvailabilityClick: () -> Unit = {},
    onConsultationsClick: (String) -> Unit = {},
    onTokenExpired: (() -> Unit)? = null
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current.applicationContext
    val sessionManager = remember { SessionManager(context) }
    val repository = remember {
        RealPatientRepository(
            apiService = RetrofitClient.patientApiService,
            reservationApiService = RetrofitClient.reservationApiService,
            sessionManager = sessionManager
        )
    }
    val factory = remember(repository, onTokenExpired) { PatientHomeViewModelFactory(repository, onTokenExpired) }
    val homeViewModel: PatientHomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()
    val resolvedDoctorName = uiState.patientname.ifBlank { doctorName }
    val resolvedSpeciality = uiState.speciality.ifBlank { "General practice" }
    val resolvedEstablishment = uiState.establishment.ifBlank { "Healthcare center" }

    val appointments = remember { mutableStateListOf<ReservationResponseDto>() }
    LaunchedEffect(uiState.id) {
        if (uiState.id.isNotBlank()) {
            val token = sessionManager.getAuthToken() ?: ""
            if (token.isNotEmpty()) {
                try {
                    val response = RetrofitClient.reservationApiService.getDoctorReservations("Bearer $token", uiState.id)
                    if (response.isSuccessful) {
                        appointments.clear()
                        appointments.addAll(response.body() ?: emptyList())
                    } else if (response.code() == 401) {
                        // Token expired, trigger callback
                        onTokenExpired?.invoke()
                    }
                } catch (e: Exception) {
                    Log.e("DoctorHomeScreen", "Failed to fetch doctor reservations", e)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DoctorHomeTopBar(
                doctorName = resolvedDoctorName,
                onNotificationsClick = onNotificationsClick,
                onProfileClick = onProfileClick
            )
        },
        bottomBar = {
            DoctorBottomBar(
                selectedIndex = selectedTabIndex,
                onSelect = { selectedTabIndex = it }
            )
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> DoctorOverviewPage(
                innerPadding = innerPadding,
                doctorName = resolvedDoctorName,
                speciality = resolvedSpeciality,
                establishment = resolvedEstablishment,
                todaysVisits = appointments.size,
                upcomingVisits = appointments.count { it.reservationStatus == "PENDING" },
                pendingReviews = appointments.count { it.reservationStatus == "PENDING" },
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick,
                onLoadMoreClick = onLoadMoreClick
            )

            1 -> DoctorAppointmentsPage(
                doctorId = uiState.id,
                innerPadding = innerPadding,
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick,
                onLoadMoreClick = onLoadMoreClick
            )

            2 -> DoctorPatientsPage(
                doctorId = uiState.id,
                innerPadding = innerPadding,
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick,
                onLoadMoreClick = onLoadMoreClick
            )
            3 -> DoctorSchedulePage(
                doctorId = uiState.id,
                innerPadding = innerPadding,
                onSetAvailabilityClick = onSetAvailabilityClick
            )
            else -> DoctorConsultationPage(
                innerPadding = innerPadding,
                onMyConsultationsClick = {
                    val userId = uiState.id
                    if (userId.isNotBlank()) {
                        onConsultationsClick(userId)
                    }
                }
            )
        }
    }
}

@Composable
private fun DoctorHomeTopBar(
    doctorName: String,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val avatarText = doctorName
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifBlank { "DR" }

    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = SahtekBlueLight,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S+",
                        color = SahtekBlueDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = "Sahtek Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = doctorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SahtekBlueDark,
                    modifier = Modifier.clickable(onClick = onProfileClick)
                ) {
                    Box(
                        modifier = Modifier.size(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = avatarText,
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorBottomBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 8.dp,
        shadowElevation = 18.dp,
        border = BorderStroke(1.dp, SahtekBorder.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            doctorTabs.forEachIndexed { index, item ->
                DoctorBottomNavItem(
                    modifier = Modifier.weight(1f),
                    item = item,
                    selected = selectedIndex == index,
                    onClick = { onSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun DoctorBottomNavItem(
    item: DoctorTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 480f),
        label = "doctorIconScale"
    )
    val selectionProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "doctorSelectionProgress"
    )
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 26.dp else 0.dp,
        animationSpec = tween(durationMillis = 220),
        label = "doctorIndicatorWidth"
    )
    val containerHeight by animateDpAsState(
        targetValue = if (selected) 42.dp else 38.dp,
        animationSpec = tween(durationMillis = 220),
        label = "doctorContainerHeight"
    )
    val iconContainerColor = lerp(
        start = Color.Transparent,
        stop = MaterialTheme.colorScheme.secondary,
        fraction = selectionProgress
    )
    val textColor = lerp(
        start = MaterialTheme.colorScheme.onSurfaceVariant,
        stop = MaterialTheme.colorScheme.primary,
        fraction = selectionProgress
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(indicatorWidth)
                .height(3.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(
                    if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
                )
        )

        Box(
            modifier = Modifier
                .height(containerHeight)
                .clip(RoundedCornerShape(18.dp))
                .background(iconContainerColor)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
                tint = textColor
            )
        }

        Text(
            text = item.label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
