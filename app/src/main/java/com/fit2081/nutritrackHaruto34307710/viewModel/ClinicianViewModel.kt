// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/viewModel/ClinicianViewModel.kt
package com.fit2081.nutritrackHaruto34307710.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrackHaruto34307710.NutriTrackRepository
import com.fit2081.nutritrackHaruto34307710.data.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ClinicianViewModel(
    application: Application,
    private val repository: NutriTrackRepository
) : AndroidViewModel(application) {

    private val _averageHeifaMale = MutableStateFlow<Float?>(null)
    val averageHeifaMale: StateFlow<Float?> = _averageHeifaMale.asStateFlow()

    private val _averageHeifaFemale = MutableStateFlow<Float?>(null)
    val averageHeifaFemale: StateFlow<Float?> = _averageHeifaFemale.asStateFlow()

    private val _dataPatterns = MutableStateFlow<String?>(null)
    val dataPatterns: StateFlow<String?> = _dataPatterns.asStateFlow()

    private val _isLoadingPatterns = MutableStateFlow(false)
    val isLoadingPatterns: StateFlow<Boolean> = _isLoadingPatterns.asStateFlow()

    init {
        loadAverageScores()
    }

    private fun loadAverageScores() {
        viewModelScope.launch {
            _averageHeifaMale.value = repository.getAverageHeifaScoreMale()
            _averageHeifaFemale.value = repository.getAverageHeifaScoreFemale()
        }
    }

    fun findDataPatterns() {
        viewModelScope.launch {
            _isLoadingPatterns.value = true
            _dataPatterns.value = null
            try {
                val allPatients: List<Patient> = repository.getAllPatientDataForAnalysis()
                if (allPatients.isNotEmpty()) {
                    val patientDataSummary = buildString {
                        append("Dataset contains ${allPatients.size} patient records.\n")
                        val malePatients = allPatients.filter { it.sex.equals("Male", ignoreCase = true) }
                        val femalePatients = allPatients.filter { it.sex.equals("Female", ignoreCase = true) }
                        append("Gender distribution: ${malePatients.size} males, ${femalePatients.size} females.\n\n")

                        append("Overall HEIFA Score Averages:\n")
                        _averageHeifaMale.value?.let { append("- Male: ${"%.1f".format(it)}\n") }
                        _averageHeifaFemale.value?.let { append("- Female: ${"%.1f".format(it)}\n\n") }

                        append("Average Vegetable HEIFA Scores:\n")
                        malePatients.mapNotNull { it.vegetablesHEIFAScoreMale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Male (Vegetables): ${"%.1f".format(it)}\n")
                        }
                        femalePatients.mapNotNull { it.vegetablesHEIFAScoreFemale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Female (Vegetables): ${"%.1f".format(it)}\n\n")
                        }

                        append("Average Fruit HEIFA Scores:\n")
                        malePatients.mapNotNull { it.fruitHEIFAScoreMale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Male (Fruit): ${"%.1f".format(it)}\n")
                        }
                        femalePatients.mapNotNull { it.fruitHEIFAScoreFemale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Female (Fruit): ${"%.1f".format(it)}\n\n")
                        }

                        append("Average Wholegrains HEIFA Scores:\n")
                        malePatients.mapNotNull { it.wholegrainsHEIFAScoreMale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Male (Wholegrains): ${"%.1f".format(it)}\n")
                        }
                        femalePatients.mapNotNull { it.wholegrainsHEIFAScoreFemale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Female (Wholegrains): ${"%.1f".format(it)}\n\n")
                        }

                        append("Average Discretionary Foods HEIFA Scores (lower is better for intake, higher for HEIFA score):\n")
                        malePatients.mapNotNull { it.discretionaryHEIFAScoreMale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Male (Discretionary): ${"%.1f".format(it)}\n")
                        }
                        femalePatients.mapNotNull { it.discretionaryHEIFAScoreFemale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Female (Discretionary): ${"%.1f".format(it)}\n\n")
                        }
                        append("Average Water HEIFA Scores:\n")
                        malePatients.mapNotNull { it.waterHEIFAScoreMale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Male (Water): ${"%.1f".format(it)}\n")
                        }
                        femalePatients.mapNotNull { it.waterHEIFAScoreFemale }.average().takeIf { !it.isNaN() }?.let {
                            append("- Female (Water): ${"%.1f".format(it)}\n")
                        }
                    }
                    println("Generated Patient Data Summary for AI:\n$patientDataSummary")

                    _dataPatterns.value = repository.getGeminiDataPatternAnalysis(patientDataSummary)
                } else {
                    _dataPatterns.value = "No patient data available to analyze."
                }
            } catch (e: Exception) {
                _dataPatterns.value = "Error analyzing data patterns: ${e.message}"
                println("Error finding data patterns: ${e.message}")
            } finally {
                _isLoadingPatterns.value = false
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class ClinicianViewModelFactory(
        private val application: Application,
        private val repository: NutriTrackRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClinicianViewModel::class.java)) {
                return ClinicianViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for ClinicianView")
        }
    }
}