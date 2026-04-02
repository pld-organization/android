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
internal fun PatientConsultationPage(
    innerPadding: PaddingValues,
    onSearchDoctorsClick: () -> Unit
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
                title = "Consultation",
                subtitle = "Choose the right specialist, share your documents, and receive guided medical support."
            )
        }
        item {
            ProfessionalActionCard(
                title = "Need medical guidance?",
                subtitle = "Start a secure consultation and share your latest analysis with a verified doctor.",
                primaryLabel = "Find Specialist",
                secondaryLabel = "Share File",
                onPrimaryClick = onSearchDoctorsClick,
                onSecondaryClick = {}
            )
        }
        item {
            FeatureSummaryCard(
                title = "Current discussion",
                subtitle = "Radiology support",
                detail = "Your uploaded chest scan is ready for professional review and follow-up comments.",
                onClick = {}
            )
        }
    }
}
