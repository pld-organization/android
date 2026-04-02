package com.example.sahtek.ui.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.SectionHeader
import com.example.sahtek.ui.home.TimelineCard

@Composable
internal fun DoctorSchedulePage(innerPadding: PaddingValues) {
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
                subtitle = "Track today’s timeline, consultation blocks, and reminders."
            )
        }
        item {
            TimelineCard(
                time = "08:00",
                title = "Consultation starts",
                description = "Begin the first patient visit and review the latest case notes."
            )
        }
        item {
            TimelineCard(
                time = "11:30",
                title = "Case review slot",
                description = "Reserve time to validate uploaded reports and write follow-up notes."
            )
        }
        item {
            TimelineCard(
                time = "15:00",
                title = "Remote consultation",
                description = "Online follow-up session with Nabil Haddad."
            )
        }
    }
}
