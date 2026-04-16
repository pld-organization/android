package com.example.sahtek.ui.home

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.ReservationViewModel
import com.example.sahtek.reservation.ReservationViewModelFactory
import com.example.sahtek.reservation.repository.RealReservationRepository
import com.example.sahtek.ui.home.repository.RealPatientRepository
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModel
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModelFactory
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekBlueLight
import com.example.sahtek.ui.theme.SahtekBorder

@Composable
internal fun AvailableSchedulesScreen(
    doctorId: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current.applicationContext
    val sessionManager = remember { SessionManager(context) }
    
    // Patient ViewModel
    val patientRepository = remember {
        RealPatientRepository(
            apiService = RetrofitClient.patientApiService,
            reservationApiService = RetrofitClient.reservationApiService,
            sessionManager = sessionManager
        )
    }
    val patientFactory = remember(patientRepository) { PatientHomeViewModelFactory(patientRepository) }
    val homeViewModel: PatientHomeViewModel = viewModel(factory = patientFactory)
    val patientUiState by homeViewModel.uiState.collectAsState()

    // Reservation ViewModel
    val reservationRepository = remember { RealReservationRepository(RetrofitClient.reservationApiService) }
    val reservationFactory = remember(reservationRepository) { ReservationViewModelFactory(reservationRepository, sessionManager) }
    val reservationViewModel: ReservationViewModel = viewModel(factory = reservationFactory)
    val reservationUiState by reservationViewModel.uiState.collectAsState()

    val patientInitial = patientUiState.patientname.firstOrNull()?.uppercaseChar()?.toString() ?: "P"

    var selectedSchedule by remember { mutableStateOf<AvailableScheduleUi?>(null) }

    // Fetch available slots when screen loads or doctorId changes
    LaunchedEffect(doctorId) {
        reservationViewModel.fetchAvailableSlots(doctorId)
    }

    val schedules = reservationUiState.availableSlots

    // Handle success/error messages
    LaunchedEffect(reservationUiState.successMessage, reservationUiState.errorMessage) {
        reservationUiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            reservationViewModel.clearMessages()
        }
        reservationUiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            reservationViewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color(0xFFFBFCFF),
        topBar = {
            AvailableSchedulesTopBar(
                patientInitial = patientInitial,
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFBFCFF))
        ) {
            if (reservationUiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SahtekBlue)
                }
            } else if (schedules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "No Available Schedules",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "This doctor hasn't created any availability slots yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(
                        items = schedules,
                        key = { schedule -> schedule.id }
                    ) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onAddClick = { selectedSchedule = schedule }
                        )
                    }
                }
            }
        }
    }

    selectedSchedule?.let { schedule ->
        AppointmentDetailsSheet(
            schedule = schedule,
            onDismiss = { selectedSchedule = null },
            onConfirm = { _, _, description ->
                val targetDoctorId = if (doctorId != "all") doctorId else "79ca2c53-efc4-459a-93f6-2cb2675bc169"

                val day = "MONDAY"
                val time = "09:00"
                
                reservationViewModel.createReservation(
                    doctorId = targetDoctorId,
                    patientId = patientUiState.id,
                    reservationDay = day,
                    reservationTime = time,
                    reason = description
                )
                selectedSchedule = null
            }
        )
    }
}

@Composable
private fun AvailableSchedulesTopBar(
    patientInitial: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
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
                        text = "Available Schedules",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Select your time",
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
                            text = patientInitial,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentDetailsSheet(
    schedule: AvailableScheduleUi,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedFileName by rememberSaveable(schedule.id) { mutableStateOf("") }
    var selectedFileUri by rememberSaveable(schedule.id) { mutableStateOf("") }
    var description by rememberSaveable(schedule.id) { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedFileUri = uri.toString()
            selectedFileName = resolveFileName(context, uri)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = SahtekBorder)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Appointment Details",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = SahtekBlueLight.copy(alpha = 0.26f),
                border = BorderStroke(1.dp, SahtekBorder.copy(alpha = 0.55f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Selected schedule",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = schedule.day,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = schedule.hour,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SahtekBlueDark
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Add file",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedButton(
                    onClick = { filePickerLauncher.launch("*/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, SahtekBorder)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = "Attach file",
                        tint = SahtekBlueDark
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (selectedFileName.isBlank()) "Choose file" else "Change file",
                        color = SahtekBlueDark
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF9FBFF),
                    border = BorderStroke(1.dp, SahtekBorder.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = if (selectedFileName.isBlank()) "No file selected" else selectedFileName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedFileName.isBlank()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                label = { Text("Description") },
                placeholder = { Text("Write appointment description") },
                minLines = 4
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, SahtekBorder)
                ) {
                    Text(text = "Cancel")
                }

                Button(
                    onClick = {
                        onConfirm(
                            selectedFileName,
                            selectedFileUri,
                            description
                        )
                    },
                    enabled = description.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SahtekBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Confirm")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun resolveFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null
    )

    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && it.moveToFirst()) {
            return it.getString(nameIndex)
        }
    }

    return uri.lastPathSegment ?: "Selected file"
}
