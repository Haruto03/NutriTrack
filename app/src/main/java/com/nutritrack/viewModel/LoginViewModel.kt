package com.nutritrack.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nutritrack.NutriTrackRepository
import com.nutritrack.data.model.Patient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: NutriTrackRepository) : ViewModel() {


    private val _userId = mutableStateOf("")
    val userId: State<String> = _userId


    private val _password = mutableStateOf("")
    val password: State<String> = _password


    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    sealed class LoginResult {
        object Idle : LoginResult()
        data class Success(val patient: Patient) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

    fun onUserIdChange(newUserId: String) {
        _userId.value = newUserId
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }


    fun loginUser() {
        viewModelScope.launch {
            val currentUserId = _userId.value
            val currentPassword = _password.value

            if (currentUserId.isBlank() || currentPassword.isBlank()) {
                _loginResult.value = LoginResult.Error("User ID and Password cannot be empty.")

                return@launch
            }

            val patient = repository.getPatientByUserId(currentUserId)
            if (patient != null) {
                if (patient.password == null) {
                    _loginResult.value = LoginResult.Error("Account not claimed. Please register.")
                } else if (patient.password == currentPassword) {
                    _loginResult.value = LoginResult.Success(patient)
                } else {
                    _loginResult.value = LoginResult.Error("Invalid User ID or Password.")
                }
            } else {
                _loginResult.value = LoginResult.Error("Invalid User ID or Password.")
            }
        }
    }

    fun resetLoginResult() {
        _loginResult.value = LoginResult.Idle

    }


    @Suppress("UNCHECKED_CAST")
    class LoginViewModelFactory(private val repository: NutriTrackRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}