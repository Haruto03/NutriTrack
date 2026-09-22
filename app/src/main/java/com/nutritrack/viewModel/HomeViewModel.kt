package com.nutritrack.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrack.NutriTrackRepository
import com.nutritrack.data.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeScreenUiState(
    val foodQualityScore: Float = 0f,
    val patientName: String = "User",
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val repository: NutriTrackRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        loadHomeScreenData()
    }

    fun loadHomeScreenData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val patient = repository.getPatientByUserId(userId)
                if (patient != null) {
                    val score = calculateFoodQualityScore(patient)
                    val name = patient.name ?: "User"

                    _uiState.value = _uiState.value.copy(
                        foodQualityScore = score,
                        patientName = name,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Patient data not found for home screen."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error loading home screen data: ${e.message}"
                )
            }
        }
    }

    private fun calculateFoodQualityScore(patient: Patient): Float {
        return when (patient.sex?.lowercase()) {
            "male" -> patient.heifaTotalScoreMale ?: 0f
            "female" -> patient.heifaTotalScoreFemale ?: 0f
            else -> {
                ((patient.heifaTotalScoreMale ?: 0f) + (patient.heifaTotalScoreFemale ?: 0f)) / 2
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class HomeViewModelFactory(
        private val repository: NutriTrackRepository,
        private val userId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for HomeViewModel")
        }
    }
}