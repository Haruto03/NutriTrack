package com.nutritrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nutritrack.ui.BottomNavigationBar
import com.nutritrack.viewModel.ClinicianLoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginScreen(
    navController: NavHostController,
    clinicianLoginViewModel: ClinicianLoginViewModel = viewModel()
) {
    val clinicianKey by clinicianLoginViewModel.clinicianKey
    val errorMessage by clinicianLoginViewModel.errorMessage
    val loginResult by clinicianLoginViewModel.loginResult.collectAsState()

    LaunchedEffect(loginResult) {
        when (loginResult) {
            is ClinicianLoginViewModel.LoginResult.Success -> {
                navController.navigate("clinician_dashboard") {
                    popUpTo("clinician_login") { inclusive = true }
                }
                clinicianLoginViewModel.resetLoginResult()
            }
            is ClinicianLoginViewModel.LoginResult.Error -> {
                // Error message is handled by observing clinicianLoginViewModel.errorMessage
            }
            ClinicianLoginViewModel.LoginResult.Idle -> {
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinician Login") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Clinician Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = clinicianKey,
                onValueChange = { clinicianLoginViewModel.onClinicianKeyChange(it) },
                label = { Text("Clinician Key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = errorMessage.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    clinicianLoginViewModel.login()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clinician Login")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}