package com.nutritrack.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Slider
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nutritrack.NutriTrackApplication
import com.nutritrack.ui.BottomNavigationBar
import com.nutritrack.viewModel.InsightsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    navController: NavHostController,
    userId: String,
    phoneNumber: String
) {
    val context = LocalContext.current

    val insightsViewModel: InsightsViewModel = viewModel(
        factory = InsightsViewModel.InsightsViewModelFactory(
            (context.applicationContext as NutriTrackApplication).repository,
            userId
        )
    )
    val uiState by insightsViewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${uiState.error}")
        }
        return
    }


    if (uiState.patientData == null || uiState.categoryScores.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No data found for the given user or no scores to display.")
        }
        return
    }

    val categoryScores = uiState.categoryScores
    val totalScore = uiState.totalScore
    val totalMaxScore = uiState.totalMaxScore
    val maxScoresMapFromViewModel = insightsViewModel.maxScoresByCategory

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Insights: Food Score",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val displayOrder = maxScoresMapFromViewModel.keys.toList()
            displayOrder.forEach { category ->
                categoryScores[category]?.let { score ->
                    item {
                        Text(category, style = MaterialTheme.typography.titleMedium)
                        val max = maxScoresMapFromViewModel[category] ?: 10

                        Slider(
                            value = score.toFloat(),
                            onValueChange = {},
                            valueRange = 0f..max.toFloat(),
                            steps = if (max > 0) max - 1 else 0,
                            enabled = false,
                            colors = SliderDefaults.colors(
                                disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                                disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledThumbColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                        )
                        Text(
                            "${"%.1f".format(score)} / $max",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Total Food Quality Score",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = totalScore.toFloat(),
                    onValueChange = {},
                    valueRange = 0f..totalMaxScore.toFloat(),
                    steps = if (totalMaxScore > 0) totalMaxScore - 1 else 0,
                    enabled = false,
                    colors = SliderDefaults.colors(
                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                        disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledThumbColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                )
                Text(
                    text = "${"%.1f".format(totalScore)} / $totalMaxScore",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                Button(
                    onClick = {
                        val shareText = "My Total Food Quality Score is ${"%.1f".format(totalScore)} / $totalMaxScore. Check out my Nutrition Scores!"
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val chooser = Intent.createChooser(shareIntent, "Share your score")
                        context.startActivity(chooser)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Share with someone")
                }
            }

            item {
                Button(

                    onClick = { navController.navigate("nutricoach") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Improve my diet!")
                }
            }
        }
    }
}