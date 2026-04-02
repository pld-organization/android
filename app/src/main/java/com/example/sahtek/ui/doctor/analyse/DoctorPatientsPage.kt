package com.example.sahtek.ui.doctor.analyse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.FeatureSummaryCard
import com.example.sahtek.ui.home.SectionHeader

@Composable
internal fun DoctorPatientsPage(innerPadding: PaddingValues) {
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
                subtitle = "Access current cases, follow-up records, and recent patient activity."
            )
        }
        item {
            FeatureSummaryCard(
                title = "High-priority follow-up",
                subtitle = "Amina Boudjemaa",
                detail = "Needs post-consultation review and medication adjustment confirmation.",
                onClick = {}
            )
        }
        item {
            FeatureSummaryCard(
                title = "New uploaded file",
                subtitle = "Nabil Haddad",
                detail = "A new chest report has been uploaded and is ready for your review.",
                onClick = {}
            )
        }
    }
}
