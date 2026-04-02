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
internal fun PatientPatientsPage(innerPadding: PaddingValues) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            SectionHeader(
                title = "Patients",
                subtitle = "A professional record space for your medical profile, uploaded files, and case history."
            )
        }
        item {
            FeatureSummaryCard(
                title = "Medical profile",
                subtitle = "Core health details",
                detail = "Blood group, allergies, current treatments, and long-term history.",
                onClick = {}
            )
        }
        item {
            FeatureSummaryCard(
                title = "Documents",
                subtitle = "Analyses and radiology",
                detail = "Keep your reports organized and accessible for doctors and consultations.",
                onClick = {}
            )
        }
    }
}
