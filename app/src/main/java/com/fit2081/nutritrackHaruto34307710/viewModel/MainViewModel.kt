// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/viewModel/MainViewModel.kt
package com.fit2081.nutritrackHaruto34307710.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrackHaruto34307710.NutriTrackRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val repository: NutriTrackRepository) : AndroidViewModel(application) {

    fun performInitialDatabaseSeed() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("AppPrefs_NutriTrack", Context.MODE_PRIVATE)
            val isSeededFlag = sharedPrefs.getBoolean("database_patient_data_seeded_v1", false)

            if (!isSeededFlag) {
                if (!repository.isDatabaseSeeded()) {
                    println("Database is not seeded. Seeding patient data from CSV...")
                    repository.seedPatientDataFromCsv()
                    with(sharedPrefs.edit()) {
                        putBoolean("database_patient_data_seeded_v1", true)
                        apply()
                    }
                    println("Patient data seeded successfully and flag set.")
                } else {
                    with(sharedPrefs.edit()) {
                        putBoolean("database_patient_data_seeded_v1", true)
                        apply()
                    }
                    println("Patient database was already seeded (found data), flag has been set now.")
                }
            } else {
                println("Patient database seed flag is already set. No seeding action needed.")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(
        private val repository: NutriTrackRepository,
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}