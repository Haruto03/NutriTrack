// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/viewModel/InsightsViewModel.kt
package com.fit2081.nutritrackHaruto34307710.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrackHaruto34307710.NutriTrackRepository
import com.fit2081.nutritrackHaruto34307710.data.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InsightsScreenUiState(
    val patientData: Patient? = null,
    val categoryScores: Map<String, Double> = emptyMap(),
    val totalScore: Double = 0.0,
    val totalMaxScore: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

class InsightsViewModel(
    private val repository: NutriTrackRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsScreenUiState())
    val uiState: StateFlow<InsightsScreenUiState> = _uiState.asStateFlow()

    val maxScoresByCategory: Map<String, Int> = mapOf(
        "Discretionary Foods" to 10,
        "Vegetables" to 10,
        "Fruit" to 10,
        "Grains and Cereals" to 5,
        "Wholegrains" to 5,
        "Meat and Alternatives" to 10,
        "Dairy and Alternatives" to 10,
        "Sodium" to 10,
        "Alcohol" to 5,
        "Water" to 5,
        "Added Sugar" to 10,
        "Saturated Fat" to 5,
        "Unsaturated Fat" to 5
    )

    init {
        loadInsightsData()
    }

    fun loadInsightsData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val patient = repository.getPatientByUserId(userId)
                if (patient != null) {
                    val currentCategoryScores = deriveCategoryScores(patient)
                    val currentTotalScore = currentCategoryScores.values.sum()
                    val currentTotalMaxScore = maxScoresByCategory
                        .filterKeys { currentCategoryScores.containsKey(it) }
                        .values.sum()

                    _uiState.value = _uiState.value.copy(
                        patientData = patient,
                        categoryScores = currentCategoryScores,
                        totalScore = currentTotalScore,
                        totalMaxScore = currentTotalMaxScore,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Patient data not found for insights."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading insights data: ${e.message}"
                )
            }
        }
    }

    private fun deriveCategoryScores(patient: Patient): Map<String, Double> {
        val scores = mutableMapOf<String, Double>()
        val isMale = patient.sex?.equals("Male", ignoreCase = true) == true

        scores["Discretionary Foods"] = (if (isMale) patient.discretionaryHEIFAScoreMale else patient.discretionaryHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Vegetables"] = (if (isMale) patient.vegetablesHEIFAScoreMale else patient.vegetablesHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Fruit"] = (if (isMale) patient.fruitHEIFAScoreMale else patient.fruitHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Grains and Cereals"] = (if (isMale) patient.grainsAndCerealsHEIFAScoreMale else patient.grainsAndCerealsHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Wholegrains"] = (if (isMale) patient.wholegrainsHEIFAScoreMale else patient.wholegrainsHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Meat and Alternatives"] = (if (isMale) patient.meatAndAlternativesHEIFAScoreMale else patient.meatAndAlternativesHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Dairy and Alternatives"] = (if (isMale) patient.dairyAndAlternativesHEIFAScoreMale else patient.dairyAndAlternativesHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Sodium"] = (if (isMale) patient.sodiumHEIFAScoreMale else patient.sodiumHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Alcohol"] = (if (isMale) patient.alcoholHEIFAScoreMale else patient.alcoholHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Water"] = (if (isMale) patient.waterHEIFAScoreMale else patient.waterHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Added Sugar"] = (if (isMale) patient.sugarHEIFAScoreMale else patient.sugarHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Saturated Fat"] = (if (isMale) patient.saturatedFatHEIFAScoreMale else patient.saturatedFatHEIFAScoreFemale)?.toDouble() ?: 0.0
        scores["Unsaturated Fat"] = (if (isMale) patient.unsaturatedFatHEIFAScoreMale else patient.unsaturatedFatHEIFAScoreFemale)?.toDouble() ?: 0.0

        return scores
    }

    @Suppress("UNCHECKED_CAST")
    class InsightsViewModelFactory(
        private val repository: NutriTrackRepository,
        private val userId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
                return InsightsViewModel(repository, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for InsightsViewModel")
        }
    }
}