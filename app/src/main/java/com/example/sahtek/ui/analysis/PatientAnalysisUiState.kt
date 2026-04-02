package com.example.sahtek.ui.analysis

enum class AnalysisMode {
    AUTOMATIC,
    MANUAL
}

enum class AnalysisStatus {
    SAFE,
    MODERATE,
    DANGER
}

data class AnalysisResultUi(
    val id: String,
    val fileName: String,
    val imagePreview: String? = null,
    val detectedCondition: String,
    val explanation: String,
    val confidence: String,
    val status: AnalysisStatus,
    val recommendation: String,
    val modelName: String,
    val createdAt: String,
    val summary: String // Keeping this for backward compatibility with existing code
)

data class AiModelUi(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

data class PatientAnalysisUiState(
    val isLoading: Boolean = false,
    val uploadedFileName: String? = null,
    val selectedMode: AnalysisMode = AnalysisMode.AUTOMATIC,
    val availableModels: List<AiModelUi> = emptyList(),
    val analysisResults: List<AnalysisResultUi> = emptyList(),
    val errorMessage: String? = null,
    val isRunSuccess: Boolean = false
)
