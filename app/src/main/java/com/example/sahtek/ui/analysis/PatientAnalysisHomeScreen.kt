package com.example.sahtek.ui.analysis

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sahtek.ui.theme.*

@Composable
fun PatientAnalysisHomeScreen(
    viewModel: PatientAnalysisViewModel,
    onNavigateToAddAnalysis: () -> Unit,
    onNavigateToDetails: (String) -> Unit
) {
    Scaffold(
        topBar = { AnalysisTopBar() },
        containerColor = SahtekBackground
    ) { paddingValues ->
        PatientAnalysisHomeScreenContent(
            viewModel = viewModel,
            onNavigateToAddAnalysis = onNavigateToAddAnalysis,
            onNavigateToDetails = onNavigateToDetails,
            innerPadding = paddingValues
        )
    }
}

@Composable
fun PatientAnalysisHomeScreenContent(
    viewModel: PatientAnalysisViewModel,
    onNavigateToAddAnalysis: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Animation for the entire content appearing
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) + slideInVertically(initialOffsetY = { 40 })
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            item {
                Text(
                    text = "Analysis",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = SahtekTextPrimary,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PremiumActionCard(
                        title = "Add Scan",
                        subtitle = "Upload X-ray or MRI",
                        icon = Icons.Default.Image,
                        onClick = onNavigateToAddAnalysis,
                        modifier = Modifier.weight(1f),
                        containerColor = Color.White
                    )
                    PremiumActionCard(
                        title = "Add Analysis",
                        subtitle = "Submit Lab Report",
                        icon = Icons.Default.Description,
                        onClick = onNavigateToAddAnalysis,
                        modifier = Modifier.weight(1f),
                        containerColor = Color.White
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 25.dp,
                            shape = RoundedCornerShape(32.dp),
                            ambientColor = SahtekShadow,
                            spotColor = SahtekShadow
                        )
                        .clip(RoundedCornerShape(32.dp))
                        .background(SahtekSurface)
                        .padding(28.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI Results",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SahtekTextPrimary,
                            fontSize = 22.sp
                        )
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = SahtekTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        uiState.analysisResults.forEachIndexed { index, result ->
                            ResultListItem(
                                result = result, 
                                index = index,
                                onDetailsClick = { onNavigateToDetails(result.id) }
                            )
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun PremiumActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

    Surface(
        modifier = modifier
            .height(150.dp)
            .scale(scale)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = SahtekShadow,
                spotColor = SahtekShadow
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(28.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(16.dp),
                color = SahtekBlueLight
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SahtekBlue,
                    modifier = Modifier.padding(12.dp).size(30.dp)
                )
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SahtekTextPrimary,
                    fontSize = 18.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun ResultListItem(
    result: AnalysisResultUi, 
    index: Int,
    onDetailsClick: () -> Unit
) {
    var itemVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        delay(index * 100L) // Staggered entrance
        itemVisible = true 
    }

    AnimatedVisibility(
        visible = itemVisible,
        enter = fadeIn() + slideInHorizontally()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(22.dp),
                    ambientColor = SahtekShadow,
                    spotColor = SahtekShadow
                )
                .clip(RoundedCornerShape(22.dp))
                .border(1.5.dp, SahtekBlueLight.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
                .background(Color.White)
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = SahtekBlueLight.copy(alpha = 0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = SahtekBlue,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = result.fileName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = SahtekTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = SahtekSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Confidence ${result.confidence}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = SahtekSuccess
                            )
                        }
                    }
                }
                
                Button(
                    onClick = onDetailsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SahtekBlue),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun AnalysisTopBar(
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        color = SahtekBackground,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            color = SahtekBlueLight,
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = "Sahtek Online",
                        tint = SahtekBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "Sahtek Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = SahtekTextPrimary
                    )
                    Text(
                        text = "Patient dashboard",
                        style = MaterialTheme.typography.bodySmall,
                        color = SahtekTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.background(Color.White, CircleShape).size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications",
                        tint = SahtekTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = SahtekBlue,
                    modifier = Modifier
                        .size(38.dp)
                        .clickable(onClick = onProfileClick)
                        .shadow(4.dp, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "S",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private suspend fun delay(timeMillis: Long) {
    kotlinx.coroutines.delay(timeMillis)
}
