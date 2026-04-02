package com.example.sahtek.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.home.FeatureSummaryCard
import com.example.sahtek.ui.home.SectionHeader
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekTextSecondary

private data class DoctorStatCardModel(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val accentColor: Color
)

private data class DoctorAppointmentModel(
    val time: String,
    val patientName: String,
    val secondaryLabel: String? = null,
    val actionLabel: String,
    val actionColor: Color,
    val actionTextColor: Color = Color.White
)

private val doctorStats = listOf(
    DoctorStatCardModel("Today's\nAppointments", "8", Icons.Filled.CalendarMonth, SahtekBlue),
    DoctorStatCardModel("Upcoming", "5", Icons.Filled.AccessTime, Color(0xFF7C5CFF)),
    DoctorStatCardModel("Completed", "2", Icons.Filled.CheckCircle, Color(0xFF20B26B)),
    DoctorStatCardModel("Cancelled", "1", Icons.Filled.Cancel, Color(0xFFFF6464))
)

private val doctorAppointments = listOf(
    DoctorAppointmentModel(
        time = "08:00",
        patientName = "Salmi Ahmed",
        actionLabel = "Completed",
        actionColor = Color(0xFF1F2430)
    ),
    DoctorAppointmentModel(
        time = "08:30",
        patientName = "Amina Boudjemaa",
        actionLabel = "Cancelled",
        actionColor = Color(0xFFF07C82)
    ),
    DoctorAppointmentModel(
        time = "09:00",
        patientName = "Nabil Haddad",
        secondaryLabel = "Online",
        actionLabel = "Join",
        actionColor = SahtekBlue
    ),
    DoctorAppointmentModel(
        time = "09:30",
        patientName = "Walid Benkhaled",
        secondaryLabel = "IRL",
        actionLabel = "Start",
        actionColor = SahtekBlue
    )
)

@Composable
internal fun DoctorAppointmentsPage(
    innerPadding: PaddingValues,
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
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Appointments",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage and track patient appointments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            DoctorStatsSection()
        }

        item {
            DoctorSearchRow(
                onSearchClick = onSearchClick,
                onFilterClick = onFilterClick
            )
        }

        item {
            DoctorAppointmentListCard()
        }

        item {
            Button(
                onClick = onLoadMoreClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SahtekBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Load More",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun DoctorStatsSection() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Today's Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "June 19, 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DoctorStatCard(
                        modifier = Modifier.weight(1f),
                        item = doctorStats[0]
                    )
                    DoctorStatCard(
                        modifier = Modifier.weight(1f),
                        item = doctorStats[1]
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DoctorStatCard(
                        modifier = Modifier.weight(1f),
                        item = doctorStats[2]
                    )
                    DoctorStatCard(
                        modifier = Modifier.weight(1f),
                        item = doctorStats[3]
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorStatCard(
    item: DoctorStatCardModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            color = item.accentColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = item.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = item.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DoctorSearchRow(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onSearchClick),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = SahtekTextSecondary
                )
                Text(
                    text = "Search patient by name",
                    color = SahtekTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Surface(
            modifier = Modifier.clickable(onClick = onFilterClick),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = SahtekTextSecondary
                )
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SahtekTextSecondary
                )
            }
        }
    }
}

@Composable
private fun DoctorAppointmentListCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            doctorAppointments.forEach { appointment ->
                DoctorAppointmentRow(item = appointment)
            }
        }
    }
}

@Composable
private fun DoctorAppointmentRow(item: DoctorAppointmentModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = item.time,
                tint = SahtekTextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.patientName,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item.secondaryLabel?.let { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = item.actionColor
            ) {
                Text(
                    text = item.actionLabel,
                    color = item.actionTextColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

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