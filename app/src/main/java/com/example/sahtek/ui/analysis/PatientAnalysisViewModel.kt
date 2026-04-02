package com.example.sahtek.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class PatientAnalysisViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PatientAnalysisUiState())
    val uiState: StateFlow<PatientAnalysisUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val mockModels = listOf(
            AiModelUi("1", "X-Ray Scanner"),
            AiModelUi("2", "Blood Cells Analysis"),
            AiModelUi("3", "MRI Interpretation"),
            AiModelUi("4", "CT Scan Vision"),
            AiModelUi("5", "Dermatology AI"),
            AiModelUi("6", "Cardio Graph AI")
        )

        val mockResults = listOf(
            AnalysisResultUi(
                id = "1",
                fileName = "Chest_XRay_001.jpg",
                confidence = "92%",
                detectedCondition = "Normal Chest X-Ray",
                explanation = "The AI analysis indicates that the lung fields are clear. No signs of pneumonia, effusion, or masses are detected. The heart size is within normal limits.",
                status = AnalysisStatus.SAFE,
                recommendation = "No immediate action required. Continue with regular health check-ups.",
                modelName = "Chest X-Ray AI Model",
                createdAt = "24 Oct 2023",
                summary = "Normal findings. No significant abnormalities detected in the lung fields."
            ),
            AnalysisResultUi(
                id = "2",
                fileName = "Blood_Report_Oct.pdf",
                confidence = "88%",
                detectedCondition = "Slight Vitamin D Deficiency",
                explanation = "Hemoglobin and other CBC parameters are normal. However, the analysis detected a serum Vitamin D level below the optimal range.",
                status = AnalysisStatus.MODERATE,
                recommendation = "Consider Vitamin D supplementation after consulting with your doctor. Increase exposure to sunlight.",
                modelName = "Lab Report Analyzer",
                createdAt = "15 Oct 2023",
                summary = "Hemoglobin levels within normal range. Slight vitamin D deficiency noted."
            ),
            AnalysisResultUi(
                id = "3",
                fileName = "MRI_Brain_Scan.png",
                confidence = "95%",
                detectedCondition = "Suspected Inflammation",
                explanation = "The AI detected areas of high signal intensity in the frontal lobe which may suggest localized inflammation or other neurological concerns.",
                status = AnalysisStatus.DANGER,
                recommendation = "Urgent: Please schedule a consultation with a Neurologist as soon as possible for a clinical evaluation.",
                modelName = "MRI Brain AI",
                createdAt = "Today",
                summary = "Detected potential abnormalities that require urgent specialist review."
            )
        )

        _uiState.update { 
            it.copy(
                availableModels = mockModels,
                analysisResults = mockResults
            )
        }
    }

    fun onAddFile(fileName: String) {
        _uiState.update { it.copy(uploadedFileName = fileName, errorMessage = null) }
    }

    fun onModeChange(mode: AnalysisMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun onModelToggle(modelId: String) {
        _uiState.update { state ->
            val updatedModels = state.availableModels.map { model ->
                if (model.id == modelId) model.copy(isSelected = !model.isSelected) else model
            }
            state.copy(availableModels = updatedModels)
        }
    }

    fun runAnalysis() {
        val currentState = _uiState.value
        
        if (currentState.uploadedFileName == null) {
            _uiState.update { it.copy(errorMessage = "Please upload a file first") }
            return
        }

        if (currentState.selectedMode == AnalysisMode.MANUAL && currentState.availableModels.none { it.isSelected }) {
            _uiState.update { it.copy(errorMessage = "Please select at least one AI model") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Simulate AI processing
            delay(2500)

            val newResult = AnalysisResultUi(
                id = UUID.randomUUID().toString(),
                fileName = currentState.uploadedFileName,
                confidence = "${(85..99).random()}%",
                detectedCondition = "Analysis Completed",
                explanation = "The AI has successfully processed the file: ${currentState.uploadedFileName}. Please review the details below.",
                status = listOf(AnalysisStatus.SAFE, AnalysisStatus.MODERATE).random(),
                recommendation = "Review these results with your primary care physician during your next visit.",
                modelName = if (currentState.selectedMode == AnalysisMode.AUTOMATIC) "Auto-Select AI" else "Manual Selection",
                createdAt = "Today",
                summary = "The AI analysis has been completed successfully for ${currentState.uploadedFileName}."
            )

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    isRunSuccess = true,
                    analysisResults = listOf(newResult) + state.analysisResults,
                    uploadedFileName = null,
                    availableModels = state.availableModels.map { it.copy(isSelected = false) }
                )
            }
        }
    }

    fun clearSuccessState() {
        _uiState.update { it.copy(isRunSuccess = false) }
    }
}
