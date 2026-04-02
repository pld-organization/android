package com.example.sahtek.ui.home

import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.ui.home.repository.RealPatientRepository
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModel
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModelFactory
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekBorder

private data class PatientHomeTab(
    val label: String,
    val icon: ImageVector
)

private val patientTabs = listOf(
    PatientHomeTab("Overview", Icons.Filled.Dashboard),
    PatientHomeTab("Appointments", Icons.Filled.ContentPaste),
    PatientHomeTab("Patients", Icons.Filled.Groups),
    PatientHomeTab("Schedule", Icons.Filled.CalendarMonth),
    PatientHomeTab("Consultation", Icons.Filled.MedicalServices)
)

@Composable
fun PatientHomeScreen(
    patientName: String = "Sara",
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSearchDoctorsClick: () -> Unit = {},
    onUploadAnalysisClick: () -> Unit = {},
    onAppointmentsClick: () -> Unit = {}
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current.applicationContext
    val repository = remember {
        RealPatientRepository(
            apiService = RetrofitClient.patientApiService,
            sessionManager = SessionManager(context)
        )
    }
    val factory = remember(repository) { PatientHomeViewModelFactory(repository) }
    val homeViewModel: PatientHomeViewModel = viewModel(factory = factory)
    val uiState by homeViewModel.uiState.collectAsState()
    val resolvedPatientName = uiState.patientname.ifBlank { patientName }
    val patientInitial = resolvedPatientName.firstOrNull()?.uppercaseChar()?.toString() ?: "S"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            PatientHomeTopBar(
                patientInitial = patientInitial,
                unreadNotifications = uiState.unreadnotification,
                onNotificationsClick = onNotificationsClick,
                onProfileClick = onProfileClick
            )
        },
        bottomBar = {
            PatientBottomBar(
                selectedIndex = selectedTabIndex,
                onSelect = { selectedTabIndex = it }
            )
        }
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> PatientOverviewPage(
                patientName = resolvedPatientName,
                innerPadding = innerPadding,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                nextAppointment = uiState.nextAppointment,
                latestAiResult = uiState.latestAiResult,
                quickStats = uiState.quickStats,
                onSearchDoctorsClick = onSearchDoctorsClick,
                onUploadAnalysisClick = onUploadAnalysisClick,
                onAppointmentsClick = onAppointmentsClick
            )

            1 -> PatientAppointmentsPage(
                innerPadding = innerPadding,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                nextAppointment = uiState.nextAppointment,
                onAddAppointmentClick = onSearchDoctorsClick
            )

            2 -> PatientPatientsPage(innerPadding = innerPadding)
            3 -> PatientSchedulePage(innerPadding = innerPadding)
            else -> PatientConsultationPage(
                innerPadding = innerPadding,
                onSearchDoctorsClick = onSearchDoctorsClick
            )
        }
    }
}

@Composable
private fun PatientHomeTopBar(
    patientInitial: String,
    unreadNotifications: Int,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val subtitle = if (unreadNotifications > 0) {
        "Patient dashboard • $unreadNotifications unread"
    } else {
        "Patient dashboard"
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Sahtek Online",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Sahtek Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = patientInitial,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PatientBottomBar(
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
            patientTabs.forEachIndexed { index, item ->
                AnimatedBottomNavItem(
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
private fun AnimatedBottomNavItem(
    item: PatientHomeTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 480f),
        label = "iconScale"
    )
    val selectionProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 240),
        label = "selectionProgress"
    )
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 26.dp else 0.dp,
        animationSpec = tween(durationMillis = 220),
        label = "indicatorWidth"
    )
    val containerHeight by animateDpAsState(
        targetValue = if (selected) 42.dp else 38.dp,
        animationSpec = tween(durationMillis = 220),
        label = "containerHeight"
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
