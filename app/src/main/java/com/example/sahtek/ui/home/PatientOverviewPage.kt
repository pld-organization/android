package com.example.sahtek.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.viewmodel.AiResultUi
import com.example.sahtek.ui.home.viewmodel.AppointmentUi
import com.example.sahtek.ui.home.viewmodel.HomeQuickStats
import com.example.sahtek.ui.theme.SahtekBlueDark

@Composable
internal fun PatientOverviewPage(
    patientName: String,
    innerPadding: PaddingValues,
    isLoading: Boolean,
    errorMessage: String?,
    nextAppointment: AppointmentUi?,
    latestAiResult: AiResultUi?,
    quickStats: HomeQuickStats,
    onSearchDoctorsClick: () -> Unit,
    onUploadAnalysisClick: () -> Unit,
    onAppointmentsClick: () -> Unit
) {
    val appointmentSubtitle = nextAppointment?.let {
        "${it.doctortName} • ${it.doctorSpeciality}"
    } ?: "No appointment booked yet"
    val appointmentDetail = nextAppointment?.let {
        "${it.date} at ${it.time}"
    } ?: "Book your next visit to see it here."
    val latestAiTitle = latestAiResult?.title ?: "Latest AI result"
    val latestAiDetail = latestAiResult?.summary
        ?: "Upload a new analysis to receive the latest AI summary."
    val reportsCount = quickStats.reportsCount.toString().padStart(2, '0')

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            HeroInsightCard(patientName = patientName)
        }

        if (isLoading) {
            item {
                DashboardStatusCard(
                    title = "Loading dashboard",
                    message = "Fetching your latest patient information."
                )
            }
        }

        errorMessage?.let { message ->
            item {
                DashboardStatusCard(
                    title = "Could not refresh dashboard",
                    message = message
                )
            }
        }

        item {
            ProfessionalActionCard(
                title = "Consult a Doctor",
                subtitle = "Speak to a specialist or start a new medical analysis securely.",
                primaryLabel = "Consult a Doctor",
                secondaryLabel = "Start Analysis",
                onPrimaryClick = onSearchDoctorsClick,
                onSecondaryClick = onUploadAnalysisClick
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "Next Visit",
                    value = nextAppointment?.date ?: "--",
                    supporting = nextAppointment?.time ?: "Not scheduled"
                )
                CompactInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "AI Reports",
                    value = reportsCount,
                    supporting = latestAiResult?.confidence ?: "Latest ready"
                )
            }
        }

        item {
            FeatureSummaryCard(
                title = "Upcoming appointment",
                subtitle = appointmentSubtitle,
                detail = appointmentDetail,
                onClick = onAppointmentsClick
            )
        }

        item {
            FeatureSummaryCard(
                title = latestAiTitle,
                subtitle = latestAiResult?.confidence ?: "AI summary",
                detail = latestAiDetail,
                onClick = onUploadAnalysisClick
            )
        }

        item {
            Text(
                text = "The AI provides guidance and analysis only; it does not replace your doctor’s professional decision.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HeroInsightCard(patientName: String) {
    val title = remember(patientName) {
        buildAnnotatedString {
            append("We combine ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("innovative technologies")
            }
            append(" with a human approach so ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$patientName can feel reassured and calm")
            }
            append(".")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFE9F3FF),
                        Color(0xFFC8DCFF)
                    )
                ),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(22.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "At Sahtek Online",
                style = MaterialTheme.typography.labelLarge,
                color = SahtekBlueDark
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "An intelligent medical platform that uses AI to analyze reports and help patients move faster toward reliable diagnosis.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
