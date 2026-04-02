package com.example.sahtek.ui.analysis

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sahtek.ui.theme.*

@Composable
fun AddPatientAnalysisScreen(
    viewModel: PatientAnalysisViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var visible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                val fileName = getFileName(context, it) ?: "Uploaded_Medical_File"
                viewModel.onAddFile(fileName)
            }
        }
    )

    LaunchedEffect(Unit) { visible = true }

    // Handle navigation back on success
    LaunchedEffect(uiState.isRunSuccess) {
        if (uiState.isRunSuccess) {
            onBack()
            viewModel.clearSuccessState()
        }
    }

    Scaffold(
        topBar = { AnalysisTopBar() },
        containerColor = SahtekBackground
    ) { paddingValues ->
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { 30 })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Analysis",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = SahtekTextPrimary
                )
                Text(
                    text = "Upload or capture a medical document to begin analysis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SahtekTextSecondary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Improved Upload Area
                PremiumUploadArea(
                    fileName = uiState.uploadedFileName,
                    onUpload = { 
                        filePickerLauncher.launch(arrayOf("image/*", "application/pdf"))
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Premium Mode Selector
                PremiumModeSelector(
                    selectedMode = uiState.selectedMode,
                    onModeChange = { viewModel.onModeChange(it) }
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Scans Analysis Selection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SahtekTextPrimary
                )
                Text(
                    text = "Select one or more AI models for your specific case",
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Improved AI Models Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(uiState.availableModels) { model ->
                        PremiumAiModelCard(
                            model = model,
                            isEnabled = uiState.selectedMode == AnalysisMode.MANUAL,
                            onClick = { viewModel.onModelToggle(model.id) }
                        )
                    }
                }

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = SahtekError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Premium Save and Run Button
                Button(
                    onClick = { viewModel.runAnalysis() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 12.dp)
                        .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = SahtekBlue, spotColor = SahtekBlue),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SahtekBlue),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save and Run",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PremiumUploadArea(fileName: String?, onUpload: () -> Unit) {
    val borderColor = if (fileName != null) SahtekBlue else SahtekBlue.copy(alpha = 0.3f)
    val backgroundBrush = if (fileName != null) {
        Brush.verticalGradient(listOf(SahtekBlueLight, Color.White))
    } else {
        Brush.verticalGradient(listOf(SahtekBlueLight.copy(alpha = 0.5f), Color.White))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(if (fileName != null) 8.dp else 0.dp, RoundedCornerShape(28.dp))
            .background(backgroundBrush, RoundedCornerShape(28.dp))
            .border(2.dp, borderColor, RoundedCornerShape(28.dp))
            .clickable { onUpload() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = if (fileName != null) SahtekBlue else Color.White
            ) {
                Icon(
                    imageVector = if (fileName != null) Icons.Default.Check else Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = if (fileName != null) Color.White else SahtekBlue,
                    modifier = Modifier.padding(16.dp).size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = fileName ?: "Drop files here or click to browse",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (fileName != null) SahtekBlue else SahtekTextPrimary
            )
            if (fileName == null) {
                Text(
                    text = "Support: JPG, PNG, PDF (Max 10MB)",
                    style = MaterialTheme.typography.bodySmall,
                    color = SahtekTextSecondary
                )
            }
        }
    }
}

@Composable
fun PremiumModeSelector(selectedMode: AnalysisMode, onModeChange: (AnalysisMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PremiumModeButton(
            text = "Automatic",
            icon = Icons.Default.AutoAwesome,
            isSelected = selectedMode == AnalysisMode.AUTOMATIC,
            onClick = { onModeChange(AnalysisMode.AUTOMATIC) },
            modifier = Modifier.weight(1f)
        )
        PremiumModeButton(
            text = "Manual",
            icon = Icons.Default.Tune,
            isSelected = selectedMode == AnalysisMode.MANUAL,
            onClick = { onModeChange(AnalysisMode.MANUAL) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PremiumModeButton(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val backgroundColor by animateColorAsState(if (isSelected) SahtekBlue else Color.Transparent)
    val textColor by animateColorAsState(if (isSelected) Color.White else SahtekTextSecondary)
    val iconTint by animateColorAsState(if (isSelected) Color.White else SahtekBlue)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

@Composable
fun PremiumAiModelCard(model: AiModelUi, isEnabled: Boolean, onClick: () -> Unit) {
    val borderColor by animateColorAsState(if (model.isSelected) SahtekBlue else Color.Transparent)
    val alpha by animateFloatAsState(if (isEnabled) 1f else 0.4f)
    val scale by animateFloatAsState(if (model.isSelected) 1.02f else 1f)

    Card(
        modifier = Modifier
            .height(120.dp)
            .scale(scale)
            .shadow(
                elevation = if (model.isSelected) 12.dp else 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = SahtekShadow,
                spotColor = SahtekShadow
            )
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(enabled = isEnabled) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = alpha))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).size(28.dp),
                shape = CircleShape,
                color = if (model.isSelected) SahtekBlue else SahtekBlueLight.copy(alpha = 0.5f)
            ) {
                Icon(
                    imageVector = if (model.isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (model.isSelected) Color.White else SahtekBlue.copy(alpha = 0.3f),
                    modifier = Modifier.padding(4.dp)
                )
            }
            
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = SahtekBlueLight
                ) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = SahtekBlue,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = SahtekTextPrimary,
                    maxLines = 1
                )
            }
        }
    }
}

private fun getFileName(context: android.content.Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result.substring(cut + 1)
        }
    }
    return result
}
