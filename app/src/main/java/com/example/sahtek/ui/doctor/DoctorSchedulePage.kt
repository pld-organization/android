package com.example.sahtek.ui.doctor

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.authservice.SessionManager
import com.example.sahtek.network.RetrofitClient
import com.example.sahtek.reservation.ReservationResponseDto
import com.example.sahtek.ui.theme.SahtekTextPrimary
import com.example.sahtek.ui.theme.SahtekTextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
internal fun DoctorSchedulePage(
    doctorId: String,
    innerPadding: PaddingValues,
    onSetAvailabilityClick: () -> Unit = {}
) {
    val context = LocalContext.current.applicationContext
    val sessionManager = remember { SessionManager(context) }
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var direction by remember { mutableStateOf(1) } // 1 for right, -1 for left
    
    val allAppointments = remember { mutableStateListOf<ReservationResponseDto>() }

    LaunchedEffect(doctorId) {
        if (doctorId.isNotBlank()) {
            val token = sessionManager.getAuthToken() ?: ""
            try {
                val response = RetrofitClient.reservationApiService.getDoctorReservations("Bearer $token", doctorId)
                if (response.isSuccessful) {
                    allAppointments.clear()
                    allAppointments.addAll(response.body() ?: emptyList())
                }
            } catch (e: Exception) { }
        }
    }

    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
    val apiDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp)
            .background(Color(0xFFF8FAFC))
    ) {
        Text(
            text = "My Schedule",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Day",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold,
                    color = SahtekTextPrimary
                )
            }

            Button(
                onClick = onSetAvailabilityClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA)),
                shape = RoundedCornerShape(14.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Set Availability",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Schedule Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Day Navigation Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        AnimatedContent(
                            targetState = currentDate,
                            transitionSpec = {
                                if (direction > 0) {
                                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> -width } + fadeOut()
                                    )
                                } else {
                                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> width } + fadeOut()
                                    )
                                }
                            }, label = ""
                        ) { date ->
                            Text(
                                text = date.format(dateFormatter),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SahtekTextPrimary
                            )
                        }
                        
                        val filteredCount = allAppointments.count { it.reservationDay == currentDate.format(apiDateFormatter) || it.reservationDay.uppercase() == currentDate.dayOfWeek.name }
                        Text(
                            text = "$filteredCount appointments this day",
                            style = MaterialTheme.typography.bodySmall,
                            color = SahtekTextSecondary
                        )
                    }
                    Row {
                        IconButton(onClick = { 
                            direction = -1
                            currentDate = currentDate.minusDays(1) 
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Day",
                                modifier = Modifier.size(28.dp),
                                tint = SahtekTextPrimary
                            )
                        }
                        IconButton(onClick = { 
                            direction = 1
                            currentDate = currentDate.plusDays(1) 
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Day",
                                modifier = Modifier.size(28.dp),
                                tint = SahtekTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = currentDate,
                    transitionSpec = {
                        if (direction > 0) {
                            (slideInHorizontally { width -> width } + fadeIn(tween(300))).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut(tween(300))
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn(tween(300))).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut(tween(300))
                            )
                        }
                    },
                    modifier = Modifier.weight(1f), label = ""
                ) { targetDate ->
                    val dateStr = targetDate.format(apiDateFormatter)
                    val dayName = targetDate.dayOfWeek.name
                    val appointments = allAppointments.filter { it.reservationDay == dateStr || it.reservationDay.uppercase() == dayName }
                    
                    if (appointments.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No appointments scheduled", color = SahtekTextSecondary)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(appointments, key = { it.id }) { appointment ->
                                AppointmentItem(
                                    data = appointment,
                                    onDelete = { 
                                        allAppointments.remove(appointment)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentItem(data: ReservationResponseDto, onDelete: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut() + slideOutHorizontally()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.reservationTime.take(5),
                modifier = Modifier.width(65.dp),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = SahtekTextPrimary
            )

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFFBFDBFE) // Light blue background
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Patient ${data.patientId.takeLast(4)}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SahtekTextPrimary
                        )
                        Text(
                            text = data.reason,
                            style = MaterialTheme.typography.labelSmall,
                            color = SahtekTextSecondary.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { /* TODO */ }, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp),
                                tint = SahtekTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { 
                                isVisible = false
                                onDelete() 
                            }, 
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                modifier = Modifier.size(20.dp),
                                tint = SahtekTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
