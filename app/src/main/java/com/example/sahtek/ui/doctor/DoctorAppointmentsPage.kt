package com.example.sahtek.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekTextSecondary

private data class DoctorStatCardModel(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
internal fun DoctorAppointmentsPage(
    doctorId: String,
    innerPadding: PaddingValues,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onLoadMoreClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val sessionManager = remember { SessionManager(context) }
    val appointments = remember { mutableStateListOf<ReservationResponseDto>() }

    LaunchedEffect(doctorId) {
        if (doctorId.isNotBlank()) {
            val token = sessionManager.getAuthToken() ?: ""
            try {
                val response = RetrofitClient.reservationApiService.getDoctorReservations("Bearer $token", doctorId)
                if (response.isSuccessful) {
                    appointments.clear()
                    appointments.addAll(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    val stats = listOf(
        DoctorStatCardModel("Total\nAppointments", appointments.size.toString(), Icons.Filled.CalendarMonth, SahtekBlue),
        DoctorStatCardModel("Upcoming", appointments.count { it.reservationStatus == "PENDING" }.toString(), Icons.Filled.AccessTime, Color(0xFF7C5CFF)),
        DoctorStatCardModel("Completed", appointments.count { it.reservationStatus == "COMPLETED" }.toString(), Icons.Filled.CheckCircle, Color(0xFF20B26B)),
        DoctorStatCardModel("Cancelled", appointments.count { it.reservationStatus == "CANCELLED" }.toString(), Icons.Filled.Cancel, Color(0xFFFF6464))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Appointments",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage and track patient appointments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            DoctorStatsSection(stats)
        }

        item {
            DoctorSearchRow(
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick
            )
        }

        if (appointments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "No appointments found.",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SahtekTextSecondary
                    )
                }
            }
        } else {
            items(appointments) { appointment ->
                DoctorAppointmentListCard(appointment)
            }
        }

        item {
            Button(
                onClick = onLoadMoreClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahtekBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Refresh",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DoctorStatsSection(stats: List<DoctorStatCardModel>) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Live Stats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DoctorStatCard(modifier = Modifier.weight(1f), item = stats[0])
                    DoctorStatCard(modifier = Modifier.weight(1f), item = stats[1])
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DoctorStatCard(modifier = Modifier.weight(1f), item = stats[2])
                    DoctorStatCard(modifier = Modifier.weight(1f), item = stats[3])
                }
            }
        }
    }
}

@Composable
private fun DoctorStatCard(
    item: DoctorStatCardModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = item.accentColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = item.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DoctorSearchRow(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = SahtekTextSecondary
                )
                Text(
                    text = "Search patient by name",
                    color = SahtekTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Surface(
            modifier = Modifier.clickable(onClick = onFilterClick),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = SahtekTextSecondary
                )
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SahtekTextSecondary
                )
            }
        }
    }
}

@Composable
private fun DoctorAppointmentListCard(appointment: ReservationResponseDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = SahtekTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = appointment.reservationTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Reason: ${appointment.reason}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary
                )
                Text(
                    text = "Date: ${appointment.reservationDay}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when (appointment.reservationStatus) {
                    "COMPLETED" -> Color(0xFF20B26B)
                    "CANCELLED" -> Color(0xFFFF6464)
                    else -> SahtekBlue
                }
            ) {
                Text(
                    text = appointment.reservationStatus,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
