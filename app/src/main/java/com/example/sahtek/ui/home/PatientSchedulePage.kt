package com.example.sahtek.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.viewmodel.AppointmentUi

@Composable
internal fun PatientSchedulePage(
    innerPadding: PaddingValues,
    appointments: List<AppointmentUi>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            SectionHeader(
                title = "Schedule",
                subtitle = "Manage reminders for appointments, medications, and follow-up actions."
            )
        }

        if (appointments.isEmpty()) {
            item {
                DashboardStatusCard(
                    title = "No upcoming appointments",
                    message = "You don't have any appointments scheduled yet. Search for a doctor to book one."
                )
            }
        } else {
            items(appointments) { appointment ->
                TimelineCard(
                    time = appointment.time,
                    title = "Appointment: ${appointment.doctortName}",
                    description = "Specialty: ${appointment.doctorSpeciality}\nDate: ${appointment.date}\nStatus: ${appointment.status}"
                )
            }
        }
        
        item {
            Text(
                text = "Daily Routine",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            TimelineCard(
                time = "08:00",
                title = "Medication reminder",
                description = "Take your morning treatment and update your symptoms if needed."
            )
        }
        item {
            TimelineCard(
                time = "14:00",
                title = "Hydration check",
                description = "Stay on track with the care plan recommended by your doctor."
            )
        }
    }
}
