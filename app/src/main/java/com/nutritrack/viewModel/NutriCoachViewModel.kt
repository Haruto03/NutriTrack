package com.nutritrack.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrack.NutriTrackRepository
import com.nutritrack.data.model.NutriCoachTip
import com.nutritrack.data.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class FruitNutritions(
    val calories: Double?,
    val fat: Double?,
    val sugar: Double?,
    val carbohydrates: Double?,
    val protein: Double?
)

data class FruitDetails(
    val name: String?,
    val family: String?,
    val order: String?,
    val genus: String?,
    val nutritions: FruitNutritions?
)

class NutriCoachViewModel(
    application: Application,
    private val repository: NutriTrackRepository,
    private val currentUserId: String
) : AndroidViewModel(application) {

    private val _fruitDetails = MutableStateFlow<FruitDetails?>(null)
    val fruitDetails: StateFlow<FruitDetails?> = _fruitDetails.asStateFlow()

    private val _fruitFetchError = MutableStateFlow<String?>(null)
    val fruitFetchError: StateFlow<String?> = _fruitFetchError.asStateFlow()

    private val _motivationalMessage = MutableStateFlow<String?>(null)
    val motivationalMessage: StateFlow<String?> = _motivationalMessage.asStateFlow()

    private val _savedTips = MutableStateFlow<List<NutriCoachTip>>(emptyList())
    val savedTips: StateFlow<List<NutriCoachTip>> = _savedTips.asStateFlow()

    private val _currentPatient = MutableStateFlow<Patient?>(null)
    val currentPatient: StateFlow<Patient?> = _currentPatient.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _isFruitLoading = MutableStateFlow(false)
    val isFruitLoading: StateFlow<Boolean> = _isFruitLoading.asStateFlow()

    // New state for fruitNameInput
    private val _fruitNameInput = MutableStateFlow("")
    val fruitNameInput: StateFlow<String> = _fruitNameInput.asStateFlow()

    // New state for showAllTipsDialog
    private val _showAllTipsDialog = MutableStateFlow(false)
    val showAllTipsDialog: StateFlow<Boolean> = _showAllTipsDialog.asStateFlow()

    init {
        loadCurrentPatientData()
        loadSavedTips()
    }

    fun onFruitNameInputChange(newName: String) {
        _fruitNameInput.value = newName
        if (_fruitFetchError.value != null) { // Clear error when user types
            clearFruitFetchError()
        }
    }

    fun setShowAllTipsDialog(show: Boolean) {
        _showAllTipsDialog.value = show
    }

    private fun loadCurrentPatientData() {
        viewModelScope.launch {
            _currentPatient.value = repository.getPatientByUserId(currentUserId)
        }
    }

    fun fetchFruitDetails() { // Takes fruitName from ViewModel state
        val fruitName = _fruitNameInput.value.trim().lowercase()
        if (fruitName.isBlank()) {
            _fruitFetchError.value = "Fruit name cannot be empty."
            return
        }
        viewModelScope.launch {
            _isFruitLoading.value = true
            _fruitDetails.value = null
            _fruitFetchError.value = null
            try {
                val details = repository.getFruityviceData(fruitName)
                if (details != null) {
                    _fruitDetails.value = details
                } else {
                    _fruitFetchError.value = "Could not fetch details for '$fruitName'. Please check the name or try again later."
                    if(!repository.isNetworkAvailable()){
                        _fruitFetchError.value = "No network connection. Please check your internet and try again."
                    }
                }
            } catch (e: Exception) {
                _fruitFetchError.value = "An unexpected error occurred: ${e.localizedMessage}"
                println("Error fetching fruit details in ViewModel: ${e.message}")
            } finally {
                _isFruitLoading.value = false
            }
        }
    }

    fun generateMotivationalMessage() {
        viewModelScope.launch {
            _isAiLoading.value = true
            _motivationalMessage.value = null
            val patientData = _currentPatient.value
            val foodIntakeData = repository.getLatestFoodIntakeByPatientId(currentUserId)

            val prompt = buildString {
                append("Generate a short encouraging message to help someone improve their fruit intake.")
                patientData?.let {
                    append(" The user is ${it.sex}.")
                    val fruitScore = if (it.sex.equals("Male", ignoreCase = true)) it.fruitHEIFAScoreMale else it.fruitHEIFAScoreFemale
                    fruitScore?.let { score -> append(" Their current fruit score is ${"%.1f".format(score)}/10.") }
                }
                foodIntakeData?.let {
                    append(" They recently reported eating these food categories: ${it.selectedCategories}.")
                    append(" Their selected persona is '${it.persona}'.")
                    append(" Their biggest meal is around ${it.biggestMealTime}, they sleep around ${it.sleepTime} and wake up around ${it.wakeUpTime}.")
                }
            }

            try {
                val generatedMessage = repository.getGeminiMotivationalTip(prompt)
                _motivationalMessage.value = generatedMessage

                generatedMessage?.let {
                    if (it.startsWith("Error generating tip:") || it.startsWith("Cannot generate tip:")) {
                        println("Not saving API error message to DB: $it")
                    } else {
                        val newTip = NutriCoachTip(
                            patientId = currentUserId,
                            tipText = it,
                            generatedDate = Date().time
                        )
                        repository.insertNutriCoachTip(newTip)
                        // No need to manually update _savedTips if it's a Flow from Room that collects emissions
                    }
                }

            } catch (e: Exception) {
                _motivationalMessage.value = "Sorry, I couldn't generate a tip right now. Please try again later."
                println("Error generating motivational message: ${e.message}")
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    private fun loadSavedTips() {
        viewModelScope.launch {
            repository.getNutriCoachTipsByPatientId(currentUserId).collect { tips ->
                _savedTips.value = tips
            }
        }
    }

    fun clearFruitFetchError() {
        _fruitFetchError.value = null
    }

    @Suppress("UNCHECKED_CAST")
    class NutriCoachViewModelFactory(
        private val application: Application,
        private val repository: NutriTrackRepository,
        private val userId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NutriCoachViewModel::class.java)) {
                return NutriCoachViewModel(application, repository, userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}