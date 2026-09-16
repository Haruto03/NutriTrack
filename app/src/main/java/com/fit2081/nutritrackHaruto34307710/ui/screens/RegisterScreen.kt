package com.fit2081.nutritrackHaruto34307710.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nutritrackHaruto34307710.NutriTrackApplication
import com.fit2081.nutritrackHaruto34307710.viewModel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModel.RegisterViewModelFactory(
            (LocalContext.current.applicationContext as NutriTrackApplication).repository
        )
    )
) {
    val userId by registerViewModel.userId
    val phoneNumber by registerViewModel.phoneNumber
    val name by registerViewModel.name
    val password by registerViewModel.password
    val confirmPassword by registerViewModel.confirmPassword
    val errorMessage by registerViewModel.errorMessage
    val registrationResult by registerViewModel.registrationResult.collectAsState()

    LaunchedEffect(registrationResult) {
        when (val result = registrationResult) {
            is RegisterViewModel.RegistrationResult.Success -> {
                registerViewModel.clearErrorMessage()
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
                registerViewModel.resetRegistrationResult()
            }
            is RegisterViewModel.RegistrationResult.Error -> {
                registerViewModel.setErrorMessage(result.message)
            }
            RegisterViewModel.RegistrationResult.Idle -> {
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
        Text("Register", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userId,
            onValueChange = { registerViewModel.onUserIdChange(it) },
            label = { Text("My ID (Provided by your Clinician)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { registerViewModel.onPhoneNumberChange(it) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { registerViewModel.onNameChange(it) },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { registerViewModel.onPasswordChange(it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { registerViewModel.onConfirmPasswordChange(it) },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please enter your ID, phone number, name, and set a password to claim your account.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                registerViewModel.clearErrorMessage()
                registerViewModel.registerUser()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text("Login")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}