package com.fit2081.nutritrackHaruto34307710.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClinicianLoginViewModel : ViewModel() {

    private val _clinicianKey = mutableStateOf("")
    val clinicianKey: State<String> = _clinicianKey

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    sealed class LoginResult {
        object Idle : LoginResult()
        object Success : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Idle)
    val loginResult: StateFlow<LoginResult> = _loginResult.asStateFlow()

    private val correctKey = "dollar-entry-apples"

    fun onClinicianKeyChange(newKey: String) {
        _clinicianKey.value = newKey
        if (_errorMessage.value.isNotEmpty()) {
            _errorMessage.value = ""
        }
        if (_loginResult.value !is LoginResult.Idle) {
            _loginResult.value = LoginResult.Idle
        }
    }

    fun login() {
        if (_clinicianKey.value == correctKey) {
            _errorMessage.value = ""
            _loginResult.value = LoginResult.Success
        } else {
            val errorMsg = "Invalid Clinician Key. Please try again."
            _errorMessage.value = errorMsg
            _loginResult.value = LoginResult.Error(errorMsg)
        }
    }

    fun resetLoginResult() {
        _loginResult.value = LoginResult.Idle
    }

    fun clearErrorMessage() {
        _errorMessage.value = ""
    }
}