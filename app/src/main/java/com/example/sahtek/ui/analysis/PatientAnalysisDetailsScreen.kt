package com.example.sahtek.ui.analysis

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sahtek.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAnalysisDetailsScreen(
    resultId: String,
    viewModel: PatientAnalysisViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val result = uiState.analysisResults.find { it.id == resultId }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Analysis not found", color = SahtekTextSecondary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Analysis Details", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SahtekTextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SahtekBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = SahtekTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SahtekBackground)
            )
        },
        containerColor = SahtekBackground
    ) { paddingValues ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Scan Preview Section
                ScanPreviewCard(fileName = result.fileName)

                // 2. Status Card
                StatusBadgeCard(status = result.status)

                // 3. Analysis Summary Card
                AnalysisSummaryCard(result = result)

                // 4. Detailed Explanation
                ExplanationSection(explanation = result.explanation)

                // 5. Recommendations
                RecommendationsSection(recommendation = result.recommendation)

                // 6. Medical Disclaimer
                MedicalDisclaimer()

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ScanPreviewCard(fileName: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Medical Scan Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SahtekTextPrimary
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shadow(15.dp, RoundedCornerShape(28.dp), ambientColor = SahtekShadow, spotColor = SahtekShadow),
            shape = RoundedCornerShape(28.dp),
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Placeholder for actual image loading (e.g., Coil)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = SahtekBlueLight,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = fileName, style = MaterialTheme.typography.bodySmall, color = SahtekTextSecondary)
                }
                
                // Zoom icon
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Expand", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun StatusBadgeCard(status: AnalysisStatus) {
    val (color, icon, text, description) = when (status) {
        AnalysisStatus.SAFE -> Quadruple(
            SahtekSuccess, 
            Icons.Default.CheckCircle, 
            "Safe", 
            "No immediate medical risks detected."
        )
        AnalysisStatus.MODERATE -> Quadruple(
            SahtekWarning, 
            Icons.Default.Info, 
            "Moderate", 
            "Some abnormalities detected. Requires attention."
        )
        AnalysisStatus.DANGER -> Quadruple(
            SahtekError, 
            Icons.Default.Warning, 
            "Danger", 
            "Urgent medical attention is highly recommended."
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp), ambientColor = SahtekShadow, spotColor = SahtekShadow),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SahtekTextSecondary
                )
            }
        }
    }
}

@Composable
fun AnalysisSummaryCard(result: AnalysisResultUi) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp), ambientColor = SahtekShadow, spotColor = SahtekShadow),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Analysis Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SahtekTextPrimary
            )
            
            SummaryItem(label = "Condition", value = result.detectedCondition, icon = Icons.Default.MedicalInformation)
            SummaryItem(label = "Confidence", value = result.confidence, icon = Icons.Default.Verified)
            SummaryItem(label = "AI Model", value = result.modelName, icon = Icons.Default.Memory)
            SummaryItem(label = "Date", value = result.createdAt, icon = Icons.Default.Event)
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = SahtekBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "$label:", style = MaterialTheme.typography.bodyMedium, color = SahtekTextSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SahtekTextPrimary)
    }
}

@Composable
fun ExplanationSection(explanation: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "AI Detailed Detection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SahtekTextPrimary
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = SahtekBlueLight.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.dp, SahtekBlueLight)
        ) {
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = SahtekTextPrimary,
                lineHeight = 22.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun RecommendationsSection(recommendation: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Recommendations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SahtekTextPrimary
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, SahtekBlueLight)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SahtekWarning, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = SahtekTextPrimary
                )
            }
        }
    }
}

@Composable
fun MedicalDisclaimer() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SahtekBorder.copy(alpha = 0.2f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = SahtekTextSecondary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Disclaimer: This AI result is supportive and not a final medical diagnosis. Please consult a healthcare professional for confirmation.",
                style = MaterialTheme.typography.bodySmall,
                color = SahtekTextSecondary,
                textAlign = TextAlign.Start
            )
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
