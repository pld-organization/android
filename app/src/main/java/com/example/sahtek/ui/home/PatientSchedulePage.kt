package com.example.sahtek.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun PatientSchedulePage(innerPadding: PaddingValues) {

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
        item {
            TimelineCard(
                time = "18:30",
                title = "Consultation prep",
                description = "Review your uploaded analyses before the next appointment."
            )
        }
    }
}
