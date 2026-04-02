package com.example.sahtek.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.viewmodel.AppointmentUi
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekBlueLight
import com.example.sahtek.ui.theme.SahtekBorder

private data class DoctorAvailabilityUi(
    val name: String,
    val speciality: String,
    val accent: Color
)

@Composable
internal fun PatientAppointmentsPage(
    innerPadding: PaddingValues,
    isLoading: Boolean,
    errorMessage: String?,
    nextAppointment: AppointmentUi?,
    onAddAppointmentClick: () -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val doctors = remember(nextAppointment) { buildDoctorAvailabilityList(nextAppointment) }
    val filteredDoctors = remember(doctors, searchQuery) {
        if (searchQuery.isBlank()) {
            doctors
        } else {
            doctors.filter { doctor ->
                doctor.name.contains(searchQuery, ignoreCase = true) ||
                    doctor.speciality.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, SahtekBlueLight.copy(alpha = 0.7f))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, top = 16.dp, end = 18.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Available Doctors",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Find a specialist and book an appointment in a few taps.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                AppointmentSearchRow(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            if (isLoading) {
                item {
                    DashboardStatusCard(
                        title = "Loading doctors",
                        message = "Preparing the latest available appointments."
                    )
                }
            }

            errorMessage?.let { message ->
                item {
                    DashboardStatusCard(
                        title = "Could not refresh appointments",
                        message = message
                    )
                }
            }

            if (filteredDoctors.isEmpty()) {
                item {
                    DashboardStatusCard(
                        title = "No doctor found",
                        message = "Try another doctor name or specialty."
                    )
                }
            } else {
                items(
                    items = filteredDoctors,
                    key = { doctor -> "${doctor.name}-" }
                ) { doctor ->
                    DoctorAvailabilityCard(
                        doctor = doctor,
                        onAddAppointmentClick = onAddAppointmentClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentSearchRow(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(22.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search doctors by name",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(1.dp, SahtekBorder.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Filter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DoctorAvailabilityCard(
    doctor: DoctorAvailabilityUi,
    onAddAppointmentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DoctorAvatar(name = doctor.name, accent = doctor.accent)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = doctor.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = doctor.speciality,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }

                AddAppointmentButton(onClick = onAddAppointmentClick)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ConsultationModeChip(
                    icon = Icons.Filled.LocalHospital,
                    label = "Clinic"
                )
                ConsultationModeChip(
                    icon = Icons.Filled.Videocam,
                    label = "Video"
                )
            }
        }
    }
}

@Composable
private fun DoctorAvatar(
    name: String,
    accent: Color
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(accent, accent.copy(alpha = 0.65f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initialsOf(name),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AddAppointmentButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .shadow(12.dp, RoundedCornerShape(999.dp))
            .clip(RoundedCornerShape(999.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF62B5FF), SahtekBlue)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add appointment",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Add Appointment",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ConsultationModeChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SahtekBlueLight.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = SahtekBlueDark,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SahtekBlueDark,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun buildDoctorAvailabilityList(nextAppointment: AppointmentUi?): List<DoctorAvailabilityUi> {
    val liveAppointmentCard = nextAppointment?.let {
        DoctorAvailabilityUi(
            name = it.doctortName.ifBlank { "Dahmani Mohamed" },
            speciality = normalizeSpeciality(it.doctorSpeciality),
            accent = SahtekBlueDark
        )
    }

    val fallbackDoctors = listOf(
        DoctorAvailabilityUi(
            name = "Dahmani Mohamed",
            speciality = "General medicine",
            accent = Color(0xFF173D7A)
        ),
        DoctorAvailabilityUi(
            name = "Nadia Benali",
            speciality = "Cardiology",
            accent = Color(0xFF3B5BA9)
        ),
        DoctorAvailabilityUi(
            name = "Sofiane Merah",
            speciality = "Dermatology",
            accent = Color(0xFF4C79C9)
        ),
        DoctorAvailabilityUi(
            name = "Lina Cheriet",
            speciality = "Pediatrics",
            accent = Color(0xFF5D91E4)
        )
    )

    return buildList {
        liveAppointmentCard?.let { add(it) }
        fallbackDoctors.forEach { fallback ->
            val alreadyAdded = any { existing -> existing.name.equals(fallback.name, ignoreCase = true) }
            if (!alreadyAdded) {
                add(fallback)
            }
        }
    }
}

private fun normalizeSpeciality(value: String): String {
    val cleaned = value.trim()
    return if (cleaned.isBlank() || cleaned.equals("nothing", ignoreCase = true)) {
        "General medicine"
    } else {
        cleaned
    }
}

private fun initialsOf(name: String): String {
    val parts = name.split(" ").filter { it.isNotBlank() }
    return parts.take(2).joinToString(separator = "") { it.first().uppercaseChar().toString() }.ifBlank {
        "DR"
    }
}
