package com.example.sahtek.ui.doctor.shedul

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.CreateScheduleRequestDto
import com.example.sahtek.reservation.DoctorAvailabilityViewModel
import com.example.sahtek.reservation.DoctorAvailabilityViewModelFactory
import com.example.sahtek.reservation.repository.RealDoctorAvailabilityRepository
import com.example.sahtek.ui.home.repository.RealPatientRepository
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModel
import com.example.sahtek.ui.home.viewmodel.PatientHomeViewModelFactory
import com.example.sahtek.ui.theme.SahtekBlue
import java.util.Locale
import java.util.UUID

enum class AppointmentType(val apiValue: String) {
    CONSULTATION("CONSULTATION"),
    ONLINE("ONLINE")
}

class TimeSlot(
    val id: String = UUID.randomUUID().toString(),
    startTime: String = "09:00",
    endTime: String = "17:00",
    type: AppointmentType = AppointmentType.CONSULTATION
) {
    var startTime by mutableStateOf(startTime)
    var endTime by mutableStateOf(endTime)
    var type by mutableStateOf(type)
}

class DayAvailability(
    val dayName: String,
    isEnabled: Boolean = false,
    slots: SnapshotStateList<TimeSlot> = mutableStateListOf(TimeSlot())
) {
    var isEnabled by mutableStateOf(isEnabled)
    val slots: SnapshotStateList<TimeSlot> = slots
}

@Composable
fun DoctorSetAvailabilityRoute(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context.applicationContext) }

    val patientRepository = remember {
        RealPatientRepository(
            apiService = RetrofitClient.patientApiService,
            reservationApiService = RetrofitClient.reservationApiService,
            sessionManager = sessionManager
        )
    }
    val patientFactory = remember(patientRepository) { PatientHomeViewModelFactory(patientRepository) }
    val patientViewModel: PatientHomeViewModel = viewModel(factory = patientFactory)
    val patientUiState by patientViewModel.uiState.collectAsState()

    val availabilityRepository = remember {
        RealDoctorAvailabilityRepository(RetrofitClient.reservationApiService)
    }
    val availabilityFactory = remember(availabilityRepository) {
        DoctorAvailabilityViewModelFactory(availabilityRepository)
    }
    val availabilityViewModel: DoctorAvailabilityViewModel = viewModel(factory = availabilityFactory)
    val availabilityUiState by availabilityViewModel.uiState.collectAsState()

    LaunchedEffect(availabilityUiState.successMessage, availabilityUiState.errorMessage) {
        availabilityUiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            availabilityViewModel.clearMessages()
            onBackClick()
        }

        availabilityUiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            availabilityViewModel.clearMessages()
        }
    }

    DoctorSetAvailabilityScreen(
        doctorId = patientUiState.id,
        isSaving = availabilityUiState.isLoading,
        onBackClick = onBackClick,
        onSaveClick = { schedules ->
            val token = sessionManager.getAuthToken() ?: ""
            availabilityViewModel.createSchedules(token, schedules)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorSetAvailabilityScreen(
    doctorId: String,
    isSaving: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: (List<CreateScheduleRequestDto>) -> Unit
) {
    val days = remember {
        mutableStateListOf(
            DayAvailability("Sunday"),
            DayAvailability("Monday"),
            DayAvailability("Tuesday"),
            DayAvailability("Wednesday"),
            DayAvailability("Thursday"),
            DayAvailability("Friday"),
            DayAvailability("Saturday")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Availability", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { onSaveClick(days.toScheduleRequests(doctorId)) },
                enabled = doctorId.isNotBlank() && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SahtekBlue)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                }

                Text(
                    text = if (isSaving) "Saving..." else "Save Availability",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Create weekly availability",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Each enabled slot will be sent to the reservation service with dayOfWeek, startTime, endTime, and appointmenttype.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (doctorId.isBlank()) {
                                "Loading doctor profile..."
                            } else {
                                "Doctor profile loaded."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (doctorId.isBlank()) Color(0xFFD97706) else Color(0xFF15803D)
                        )
                    }
                }
            }

            items(days, key = { it.dayName }) { day ->
                DayAvailabilityCard(day = day)
            }
        }
    }
}

@Composable
fun DayAvailabilityCard(day: DayAvailability) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (day.isEnabled) Color.White else Color(0xFFF1F5F9)
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (day.isEnabled) SahtekBlue.copy(alpha = 0.35f) else Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = day.isEnabled,
                    onCheckedChange = { day.isEnabled = it },
                    modifier = Modifier.scale(0.85f),
                    colors = SwitchDefaults.colors(checkedThumbColor = SahtekBlue)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(day.dayName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (day.isEnabled) {
                day.slots.forEach { slot ->
                    TimeSlotRow(
                        slot = slot,
                        canDelete = day.slots.size > 1,
                        onDelete = { day.slots.remove(slot) }
                    )
                }

                OutlinedButton(
                    onClick = { day.slots.add(TimeSlot()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Add Time Slot", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun TimeSlotRow(
    slot: TimeSlot,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = slot.startTime,
                onValueChange = { slot.startTime = it },
                modifier = Modifier.weight(1f),
                label = { Text("Start", fontSize = 12.sp) },
                placeholder = { Text("09:00") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
            OutlinedTextField(
                value = slot.endTime,
                onValueChange = { slot.endTime = it },
                modifier = Modifier.weight(1f),
                label = { Text("End", fontSize = 12.sp) },
                placeholder = { Text("17:00") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppointmentTypeButton(
                    selected = slot.type == AppointmentType.CONSULTATION,
                    icon = Icons.Default.MedicalServices,
                    label = "Consultation",
                    onClick = { slot.type = AppointmentType.CONSULTATION }
                )
                AppointmentTypeButton(
                    selected = slot.type == AppointmentType.ONLINE,
                    icon = Icons.Default.Videocam,
                    label = "Online",
                    onClick = { slot.type = AppointmentType.ONLINE }
                )
            }

            IconButton(
                onClick = onDelete,
                enabled = canDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete slot",
                    tint = if (canDelete) Color(0xFFDC2626) else Color.LightGray
                )
            }
        }

        Text(
            text = "Use 24-hour format, for example 09:00.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AppointmentTypeButton(
    selected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) SahtekBlue.copy(alpha = 0.12f) else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) SahtekBlue.copy(alpha = 0.35f) else Color.LightGray
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) SahtekBlue else Color.Gray,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                color = if (selected) SahtekBlue else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

private fun List<DayAvailability>.toScheduleRequests(doctorId: String): List<CreateScheduleRequestDto> =
    filter { it.isEnabled }
        .flatMap { day ->
            day.slots.map { slot ->
                CreateScheduleRequestDto(
                    doctorId = doctorId,
                    dayOfWeek = day.dayName.uppercase(Locale.ENGLISH),
                    startTime = "${slot.startTime.trim()}:00",
                    endTime = "${slot.endTime.trim()}:00",
                    appointmentType = slot.type.apiValue
                )
            }
        }
