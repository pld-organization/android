package com.example.sahtek.ui.doctor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.FeatureSummaryCard
import com.example.sahtek.ui.home.ProfessionalActionCard
import com.example.sahtek.ui.home.SectionHeader
import com.example.sahtek.ui.theme.SahtekBlue

@Composable
internal fun DoctorConsultationPage(
    innerPadding: PaddingValues,
    onMyConsultationsClick: () -> Unit = {}
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
                subtitle = "Manage active discussions, case triage, and patient requests."
            )
        }
        item {
            Button(
                onClick = onMyConsultationsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahtekBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "My Consultations",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        item {
            ProfessionalActionCard(
                title = "Clinical collaboration",
                subtitle = "Review active cases, coordinate care, and respond to consultation requests.",
                primaryLabel = "Open Cases",
                secondaryLabel = "Messages",
                onPrimaryClick = {},
                onSecondaryClick = {}
            )
        }
        item {
            FeatureSummaryCard(
                title = "Case waiting for response",
                subtitle = "Walid Benkhaled",
                detail = "The patient uploaded new details and is waiting for your next recommendation.",
                onClick = {}
            )
        }
        item {
            FeatureSummaryCard(
                title = "Team review",
                subtitle = "Radiology support request",
                detail = "A shared case needs your confirmation before the report is finalized.",
                onClick = {}
            )
        }
    }
}

