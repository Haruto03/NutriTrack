package com.fit2081.nutritrackHaruto34307710.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.nutritrackHaruto34307710.NutriTrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: NutriTrackRepository) : ViewModel() {

    private val _userId = mutableStateOf("")
    val userId: State<String> = _userId

    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> = _phoneNumber

    private val _name = mutableStateOf("")
    val name: State<String> = _name

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _confirmPassword = mutableStateOf("")
    val confirmPassword: State<String> = _confirmPassword

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    sealed class RegistrationResult {
        object Idle : RegistrationResult()
        object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }

    private val _registrationResult = MutableStateFlow<RegistrationResult>(RegistrationResult.Idle)
    val registrationResult: StateFlow<RegistrationResult> = _registrationResult.asStateFlow()

    fun onUserIdChange(newUserId: String) {
        _userId.value = newUserId
    }

    fun onPhoneNumberChange(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }

    fun registerUser() {
        val currentUserId = _userId.value
        val currentPhoneNumber = _phoneNumber.value
        val currentName = _name.value
        val currentPassword = _password.value
        val currentConfirmPassword = _confirmPassword.value

        if (currentUserId.isBlank() || currentPhoneNumber.isBlank() || currentName.isBlank() || currentPassword.isBlank()) {
            _registrationResult.value = RegistrationResult.Error("All fields are required.")
            return
        }
        if (currentPassword != currentConfirmPassword) {
            _registrationResult.value = RegistrationResult.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            val patient = repository.getPatientByIdAndPhone(currentUserId, currentPhoneNumber)
            if (patient != null) {
                if (patient.password == null) {
                    val success = repository.claimAccountForPatient(currentUserId, currentPhoneNumber, currentName, currentPassword)
                    if (success) {
                        _registrationResult.value = RegistrationResult.Success
                    } else {
                        _registrationResult.value = RegistrationResult.Error("Failed to claim account. Please try again.")
                    }
                } else {
                    _registrationResult.value = RegistrationResult.Error("This account has already been claimed.")
                }
            } else {
                _registrationResult.value = RegistrationResult.Error("User ID or Phone Number not found in our records.")
            }
        }
    }

    fun resetRegistrationResult() {
        _registrationResult.value = RegistrationResult.Idle
    }

    @Suppress("UNCHECKED_CAST")
    class RegisterViewModelFactory(private val repository: NutriTrackRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                return RegisterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}