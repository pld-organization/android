package com.example.sahtek.ui.doctor.pation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sahtek.ui.theme.SahtekBlue
import com.example.sahtek.ui.theme.SahtekBlueDark
import com.example.sahtek.ui.theme.SahtekBlueLight
import com.example.sahtek.ui.theme.SahtekBorder
import com.example.sahtek.ui.theme.SahtekSuccess
import com.example.sahtek.ui.theme.SahtekTextPrimary
import com.example.sahtek.ui.theme.SahtekTextSecondary
import com.example.sahtek.ui.theme.SahtekWarning

private data class PatientChipColors(
    val container: Color,
    val content: Color
)

@Composable
internal fun DoctorPatientsPage(
    innerPadding: PaddingValues,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onLoadMoreClick: () -> Unit,
    viewModel: DoctorPationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text(
                    text = "Patients",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = SahtekTextPrimary
                )

                DoctorPatientSearchRow(
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onFilterClick = onFilterClick
                )
            }
        }

        if (uiState.filteredPatients.isEmpty()) {
            item {
                PatientEmptyState(searchQuery = uiState.searchQuery)
            }
        } else {
            items(
                items = uiState.filteredPatients,
                key = { patient -> patient.id }
            ) { patient ->
                DoctorPatientCard(item = patient)
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onLoadMoreClick,
                    modifier = Modifier.width(170.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SahtekBlue,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "Load More",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun DoctorPatientSearchRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f))
        ) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = {
                    Text(
                        text = "Search patient by name",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search patients"
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = SahtekTextPrimary,
                    unfocusedTextColor = SahtekTextPrimary,
                    cursorColor = SahtekBlueDark,
                    focusedLeadingIconColor = SahtekTextSecondary,
                    unfocusedLeadingIconColor = SahtekTextSecondary,
                    focusedPlaceholderColor = SahtekTextSecondary,
                    unfocusedPlaceholderColor = SahtekTextSecondary
                )
            )
        }

        Row(
            modifier = Modifier.clickable(onClick = onFilterClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Filter",
                tint = SahtekTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Filter",
                style = MaterialTheme.typography.bodyMedium,
                color = SahtekTextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PatientEmptyState(searchQuery: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "No patients found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SahtekTextPrimary
            )
            Text(
                text = "No patient matches \"$searchQuery\".",
                style = MaterialTheme.typography.bodyMedium,
                color = SahtekTextSecondary
            )
        }
    }
}

@Composable
private fun DoctorPatientCard(item: DoctorPatientUi) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 14.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.94f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF8FBFF))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SahtekTextSecondary
                )
                PatientDecorDots(item.resultTone)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            border = BorderStroke(1.dp, SahtekBlueDark.copy(alpha = 0.35f))
                        ) {
                            Box(
                                modifier = Modifier.size(22.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.order.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SahtekBlueDark
                                )
                            }
                        }

                        Text(
                            text = item.fullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SahtekTextPrimary
                        )
                    }

                    PatientChip(
                        text = item.consultationType,
                        tone = DoctorPatientTone.Info
                    )
                }

                PatientChip(
                    text = item.statusLabel,
                    tone = item.statusTone,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PatientResultChip(
                        text = item.resultLabel,
                        tone = item.resultTone
                    )

                    item.note?.let { note ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = SahtekBlue.copy(alpha = 0.35f),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodySmall,
                                color = SahtekTextSecondary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(start = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PatientActionButton(
                        icon = Icons.Filled.MoreHoriz,
                        contentDescription = "More options for ${item.fullName}"
                    )
                }
            }
        }
    }
}

@Composable
private fun PatientDecorDots(tone: DoctorPatientTone) {
    val accentColor = when (tone) {
        DoctorPatientTone.Info -> SahtekBlue
        DoctorPatientTone.Safe -> Color(0xFF63D6A3)
        DoctorPatientTone.Warning -> Color(0xFFF2C764)
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            accentColor,
            Color(0xFF8AE3BF),
            Color(0xFFC5E3FF),
            Color(0xFFE5EDF5)
        ).forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(if (index < 2) 8.dp else 6.dp)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

@Composable
private fun PatientChip(
    text: String,
    tone: DoctorPatientTone,
    modifier: Modifier = Modifier
) {
    val colors = patientChipColors(tone)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = colors.container
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.content,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PatientResultChip(
    text: String,
    tone: DoctorPatientTone
) {
    val colors = patientChipColors(tone)
    val icon = when (tone) {
        DoctorPatientTone.Info -> Icons.Filled.CheckCircle
        DoctorPatientTone.Safe -> Icons.Filled.CheckCircle
        DoctorPatientTone.Warning -> Icons.Filled.WarningAmber
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.container
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.content,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = colors.content,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PatientActionButton(
    icon: ImageVector,
    contentDescription: String
) {
    Surface(
        modifier = Modifier.clickable(onClick = {}),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, SahtekBorder.copy(alpha = 0.65f))
    ) {
        Box(
            modifier = Modifier
                .size(38.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = SahtekBlueDark,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun patientChipColors(tone: DoctorPatientTone): PatientChipColors =
    when (tone) {
        DoctorPatientTone.Info -> PatientChipColors(
            container = SahtekBlueLight,
            content = SahtekBlueDark
        )

        DoctorPatientTone.Safe -> PatientChipColors(
            container = Color(0xFFE3F7ED),
            content = SahtekSuccess
        )

        DoctorPatientTone.Warning -> PatientChipColors(
            container = Color(0xFFFFF2C9),
            content = SahtekWarning
        )
    }
