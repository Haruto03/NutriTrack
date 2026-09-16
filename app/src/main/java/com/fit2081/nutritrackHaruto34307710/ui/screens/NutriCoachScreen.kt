package com.fit2081.nutritrackHaruto34307710.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
// Removed remember and mutableStateOf for fruitNameInput and showAllTipsDialog if moved to VM
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.fit2081.nutritrackHaruto34307710.ui.BottomNavigationBar
import com.fit2081.nutritrackHaruto34307710.NutriTrackApplication
import com.fit2081.nutritrackHaruto34307710.viewModel.NutriCoachViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fit2081.nutritrackHaruto34307710.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriCoachScreen(
    navController: NavHostController,
    userId: String,
    viewModel: NutriCoachViewModel = viewModel(
        factory = NutriCoachViewModel.NutriCoachViewModelFactory(
            application = LocalContext.current.applicationContext as NutriTrackApplication,
            repository = (LocalContext.current.applicationContext as NutriTrackApplication).repository,
            userId = userId
        )
    )
) {
    val fruitDetails by viewModel.fruitDetails.collectAsState()
    val motivationalMessage by viewModel.motivationalMessage.collectAsState()
    val savedTips by viewModel.savedTips.collectAsState()
    val currentPatient by viewModel.currentPatient.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val isFruitLoading by viewModel.isFruitLoading.collectAsState()
    val fruitFetchError by viewModel.fruitFetchError.collectAsState()

    // Get states from ViewModel
    val fruitNameInput by viewModel.fruitNameInput.collectAsState()
    val showAllTipsDialog by viewModel.showAllTipsDialog.collectAsState()


    val fruitScore = currentPatient?.let {
        if (it.sex.equals("Male", ignoreCase = true)) it.fruitHEIFAScoreMale else it.fruitHEIFAScoreFemale
    } ?: 0f
    val isFruitScoreOptimal = fruitScore >= 10f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriCoach") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Fruit Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                if (!isFruitScoreOptimal) {
                    OutlinedTextField(
                        value = fruitNameInput,
                        onValueChange = { viewModel.onFruitNameInputChange(it) }, // Use ViewModel's updater
                        label = { Text("Enter Fruit Name (e.g., Banana)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = fruitFetchError != null
                    )
                    fruitFetchError?.let { errorMsg ->
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(if (fruitFetchError != null) 4.dp else 8.dp))

                    Button(
                        onClick = {
                            viewModel.fetchFruitDetails() // Call ViewModel function
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isFruitLoading && fruitNameInput.isNotBlank()
                    ) {
                        if (isFruitLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Text("Get Details")
                        }
                    }

                    fruitDetails?.let { details ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Name: ${details.name ?: "N/A"}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("Family: ${details.family ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                                details.nutritions?.let { nutritions ->
                                    Text("Calories: ${nutritions.calories ?: "N/A"} kcal", style = MaterialTheme.typography.bodyMedium)
                                    Text("Fat: ${nutritions.fat ?: "N/A"} g", style = MaterialTheme.typography.bodyMedium)
                                    Text("Sugar: ${nutritions.sugar ?: "N/A"} g", style = MaterialTheme.typography.bodyMedium)
                                    Text("Carbohydrates: ${nutritions.carbohydrates ?: "N/A"} g", style = MaterialTheme.typography.bodyMedium)
                                    Text("Protein: ${nutritions.protein ?: "N/A"} g", style = MaterialTheme.typography.bodyMedium)
                                } ?: Text("Nutritional information not available.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                } else {
                    AsyncImage(
                        model = "https://picsum.photos/seed/${userId}/600/400",
                        contentDescription = "Random image representing optimal fruit intake",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.nutri_track_logo),
                        error = painterResource(id = R.drawable.nutri_track_logo)
                    )
                    Text(
                        "Your fruit intake score is optimal! Keep up the good work with your fruit choices.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Motivational Message (AI)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.generateMotivationalMessage() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isAiLoading
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Generate New Tip")
                    }
                }

                motivationalMessage?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Text(message, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.setShowAllTipsDialog(true) }, // Use ViewModel's updater
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show All Tips")
                }
            }
        }
    }

    if (showAllTipsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowAllTipsDialog(false) }, // Use ViewModel's updater
            title = { Text("All Saved Tips", style = MaterialTheme.typography.titleLarge) },
            text = {
                if (savedTips.isEmpty()) {
                    Text("No motivational tips have been saved yet.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                        items(savedTips) { tip ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(tip.tipText, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(tip.generatedDate)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowAllTipsDialog(false) }) { // Use ViewModel's updater
                    Text("Done")
                }
            }
        )
    }
}