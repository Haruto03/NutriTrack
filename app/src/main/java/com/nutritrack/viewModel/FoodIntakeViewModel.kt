package com.nutritrack.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrack.NutriTrackRepository
import com.nutritrack.Persona
import com.nutritrack.data.model.FoodIntake
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoodIntakeViewModel(private val repository: NutriTrackRepository) : ViewModel() {

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories.asStateFlow()

    private val _selectedPersonaInDropdown = MutableStateFlow("")
    val selectedPersonaInDropdown: StateFlow<String> = _selectedPersonaInDropdown.asStateFlow()

    private val _biggestMealTime = MutableStateFlow("")
    val biggestMealTime: StateFlow<String> = _biggestMealTime.asStateFlow()

    private val _sleepTime = MutableStateFlow("")
    val sleepTime: StateFlow<String> = _sleepTime.asStateFlow()

    private val _wakeUpTime = MutableStateFlow("")
    val wakeUpTime: StateFlow<String> = _wakeUpTime.asStateFlow()

    // For modal state, usually kept in ViewModel if its content or trigger is complex
    // or needs to survive simple recompositions beyond a single screen.
    // For this case, we can keep them as mutableStateOf for simplicity if their direct impact is UI visibility.
    private val _showPersonaModal = MutableStateFlow(false)
    val showPersonaModal: StateFlow<Boolean> = _showPersonaModal.asStateFlow()

    private val _modalPersona = MutableStateFlow<Persona?>(null)
    val modalPersona: StateFlow<Persona?> = _modalPersona.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    fun loadLatestFoodIntake(patientId: String) {
        viewModelScope.launch {
            val intake = repository.getLatestFoodIntakeByPatientId(patientId)
            intake?.let {
                _selectedCategories.value = it.selectedCategories.split(",").filter { cat -> cat.isNotBlank() }
                _selectedPersonaInDropdown.value = it.persona
                _biggestMealTime.value = it.biggestMealTime
                _sleepTime.value = it.sleepTime
                _wakeUpTime.value = it.wakeUpTime
            }
            _isDataLoaded.value = true // Mark data as loaded even if intake is null
        }
    }

    fun toggleCategory(category: String) {
        val currentCategories = _selectedCategories.value.toMutableList()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        _selectedCategories.value = currentCategories.toList()
    }

    fun setSelectedPersona(personaName: String) {
        _selectedPersonaInDropdown.value = personaName
    }

    fun setBiggestMealTime(time: String) {
        _biggestMealTime.value = time
    }

    fun setSleepTime(time: String) {
        _sleepTime.value = time
    }

    fun setWakeUpTime(time: String) {
        _wakeUpTime.value = time
    }

    fun setShowPersonaModal(show: Boolean, persona: Persona? = null) {
        _modalPersona.value = persona
        _showPersonaModal.value = show
    }


    fun saveFoodIntake(patientId: String, onSaveCompleteAction: () -> Unit) {
        viewModelScope.launch {
            val foodIntake = FoodIntake(
                patientId = patientId,
                selectedCategories = _selectedCategories.value.joinToString(","),
                persona = _selectedPersonaInDropdown.value,
                biggestMealTime = _biggestMealTime.value,
                sleepTime = _sleepTime.value,
                wakeUpTime = _wakeUpTime.value,
                submissionDate = System.currentTimeMillis()
            )
            repository.insertFoodIntake(foodIntake)
            onSaveCompleteAction() // Call the completion action, which might include SharedPreferences saving
        }
    }

    @Suppress("UNCHECKED_CAST")
    class FoodIntakeViewModelFactory(private val repository: NutriTrackRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FoodIntakeViewModel::class.java)) {
                return FoodIntakeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class for FoodIntake")
        }
    }
}