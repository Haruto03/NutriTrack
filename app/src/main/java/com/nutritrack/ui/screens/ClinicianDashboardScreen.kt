package com.nutritrack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nutritrack.NutriTrackApplication
import com.nutritrack.ui.BottomNavigationBar
import com.nutritrack.viewModel.ClinicianViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianDashboardScreen(
    navController: NavHostController,
    viewModel: ClinicianViewModel = viewModel(
        factory = ClinicianViewModel.ClinicianViewModelFactory(
            application = LocalContext.current.applicationContext as NutriTrackApplication,
            repository = (LocalContext.current.applicationContext as NutriTrackApplication).repository
        )
    )
) {
    val avgHeifaMale by viewModel.averageHeifaMale.collectAsState()
    val avgHeifaFemale by viewModel.averageHeifaFemale.collectAsState()
    val dataPatterns by viewModel.dataPatterns.collectAsState()
    val isLoadingPatterns by viewModel.isLoadingPatterns.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinician Dashboard") },
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
            BottomNavigationBar(
                navController = navController
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("HEIFA Score Averages", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Average HEIFA (Male): ${avgHeifaMale?.let { "%.1f".format(it) } ?: "Calculating..."}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Average HEIFA (Female): ${avgHeifaFemale?.let { "%.1f".format(it) } ?: "Calculating..."}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { viewModel.findDataPatterns() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoadingPatterns
                ) {
                    if (isLoadingPatterns) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Find Data Patterns")
                    }
                }
            }

            dataPatterns?.let { patternsText ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("AI-Powered Data Analysis:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val cleanedPatterns = patternsText.lines()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    if (cleanedPatterns.isNotEmpty()) {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            cleanedPatterns.forEach { pattern ->
                                val displayPattern = if (pattern.matches(Regex("^\\d+\\.\\s.*"))) {
                                    pattern
                                } else if (pattern.startsWith("- ")) {
                                    pattern
                                } else {
                                    "• $pattern"
                                }
                                Text(
                                    text = displayPattern,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else if (!isLoadingPatterns && patternsText.isNotBlank()) {
                        Text(patternsText, style = MaterialTheme.typography.bodyMedium, color = Color.Red)
                    } else if (!isLoadingPatterns) {
                        Text("No specific patterns found or the analysis result was empty. Please try again.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done")
                }
            }
        }
    }
}