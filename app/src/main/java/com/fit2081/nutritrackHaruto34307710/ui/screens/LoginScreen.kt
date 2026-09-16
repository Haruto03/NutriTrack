package com.fit2081.nutritrackHaruto34307710.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.nutritrackHaruto34307710.AuthManager
import com.fit2081.nutritrackHaruto34307710.NutriTrackApplication
import com.fit2081.nutritrackHaruto34307710.viewModel.LoginViewModel
import com.fit2081.nutritrackHaruto34307710.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.LoginViewModelFactory(
            (LocalContext.current.applicationContext as NutriTrackApplication).repository
        )
    )
) {
    val userId by loginViewModel.userId
    val password by loginViewModel.password
    val errorMessage by loginViewModel.errorMessage
    val loginResult by loginViewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult) {
        when (val result = loginResult) {
            is LoginViewModel.LoginResult.Success -> {
                AuthManager.login(result.patient.userId, result.patient.name, result.patient.phoneNumber)
                loginViewModel.clearErrorMessage()
                onLoginSuccess()
                loginViewModel.resetLoginResult()
            }
            is LoginViewModel.LoginResult.Error -> {
                loginViewModel.setErrorMessage(result.message)
            }
            LoginViewModel.LoginResult.Idle -> {
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Log In", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userId,
            onValueChange = { loginViewModel.onUserIdChange(it) },
            label = { Text("My ID (Provided by your Clinician)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { loginViewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.login_dsc_a3),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.loginUser()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToRegister) {
            Text("Register")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
    }
}