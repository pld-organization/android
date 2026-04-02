package com.example.sahtek.ui.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.CompactInfoCard
import com.example.sahtek.ui.home.FeatureSummaryCard
import com.example.sahtek.ui.home.ProfessionalActionCard
import com.example.sahtek.ui.home.SectionHeader

@Composable
internal fun DoctorOverviewPage(
    innerPadding: PaddingValues,
    doctorName: String,
    speciality: String,
    establishment: String,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onLoadMoreClick: () -> Unit
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
                title = "Doctor Dashboard",
                subtitle = "$doctorName • $speciality at $establishment"
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Today's Visits",
                    value = "08",
                    supporting = "5 upcoming"
                )
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Pending Reviews",
                    value = "03",
                    supporting = "2 urgent"
                )
            }
        }
        item {
            ProfessionalActionCard(
                title = "Ready for your next patient?",
                subtitle = "Search records quickly, review consultations, and manage today’s appointments in one place.",
                primaryLabel = "Search Patient",
                secondaryLabel = "Open Filter",
                onPrimaryClick = onSearchClick,
                onSecondaryClick = onFilterClick
            )
        }
        item {
            FeatureSummaryCard(
                title = "Morning consultation block",
                subtitle = "4 patients confirmed",
                detail = "Your next visit starts at 08:00. Keep consultation notes and imaging review ready.",
                onClick = onLoadMoreClick
            )
        }
        item {
            FeatureSummaryCard(
                title = "Clinical reminder",
                subtitle = speciality,
                detail = "Your profile is connected to $establishment. Review pending files and follow-up tasks from one place.",
                onClick = onLoadMoreClick
            )
        }
    }
}
