package com.nutritrack.ui.screens

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nutritrack.AuthManager
import com.nutritrack.NutriTrackApplication
import com.nutritrack.Persona
import com.nutritrack.personas // Assuming this is a static list
import com.nutritrack.viewModel.FoodIntakeViewModel
import com.nutritrack.R

private val foodCategories = listOf("Fruits", "Vegetables", "Grains", "Red Meat", "Seafood", "Poultry","Fish","Egg","Nuts/Seeds")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FoodIntakeQuestionnaireScreen(
    onBack: () -> Unit,
    onSaveComplete: () -> Unit,
    foodIntakeViewModel: FoodIntakeViewModel = viewModel(
        factory = FoodIntakeViewModel.FoodIntakeViewModelFactory(
            (LocalContext.current.applicationContext as NutriTrackApplication).repository
        )
    )
) {
    val context = LocalContext.current

    val selectedCategories by foodIntakeViewModel.selectedCategories.collectAsState()
    val selectedPersonaInDropdown by foodIntakeViewModel.selectedPersonaInDropdown.collectAsState()
    val biggestMealTime by foodIntakeViewModel.biggestMealTime.collectAsState()
    val sleepTime by foodIntakeViewModel.sleepTime.collectAsState()
    val wakeUpTime by foodIntakeViewModel.wakeUpTime.collectAsState()
    val showPersonaModal by foodIntakeViewModel.showPersonaModal.collectAsState()
    val modalPersona by foodIntakeViewModel.modalPersona.collectAsState()
    val isDataLoaded by foodIntakeViewModel.isDataLoaded.collectAsState()


    LaunchedEffect(key1 = AuthManager.currentUserId, key2 = isDataLoaded) {
        val userId = AuthManager.currentUserId
        if (userId != null && !isDataLoaded) {
            foodIntakeViewModel.loadLatestFoodIntake(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Intake Questionnaire") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            if (!isDataLoaded && AuthManager.currentUserId != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tick all the food categories you can eat", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            foodCategories.forEach { category ->
                                val isChecked = selectedCategories.contains(category)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .wrapContentWidth()
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { _ -> // checked state is implicitly managed by contains
                                            foodIntakeViewModel.toggleCategory(category)
                                        }
                                    )
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(start = 4.dp),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Your Persona", style = MaterialTheme.typography.titleLarge)
                        Text(text = stringResource(R.string.wholepersona_dsc))
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            maxItemsInEachRow = 3
                        ) {
                            personas.forEach { persona ->
                                Button(
                                    onClick = {
                                        foodIntakeViewModel.setShowPersonaModal(true, persona)
                                    },
                                    modifier = Modifier
                                        .defaultMinSize(minWidth = 100.dp)
                                        .height(IntrinsicSize.Min),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = persona.name,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                    }

                    item {
                        Text("Which persona best fits you?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedPersonaInDropdown,
                                onValueChange = {},
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                label = { Text("Select Persona") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                personas.forEach { persona ->
                                    DropdownMenuItem(
                                        text = { Text(persona.name) },
                                        onClick = {
                                            foodIntakeViewModel.setSelectedPersona(persona.name)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Timings", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        TimePickerItem("What time of day approx. do you normally eat your biggest meal?", biggestMealTime) {
                            foodIntakeViewModel.setBiggestMealTime(it)
                        }
                        TimePickerItem("What time of day approx. do you go to sleep at night?", sleepTime) {
                            foodIntakeViewModel.setSleepTime(it)
                        }
                        TimePickerItem("What time of day approx. do you wake up in the morning?", wakeUpTime) {
                            foodIntakeViewModel.setWakeUpTime(it)
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val currentUserId = AuthManager.currentUserId
                                if (currentUserId != null) {
                                    foodIntakeViewModel.saveFoodIntake(currentUserId) {
                                        // This lambda is executed after repository.insertFoodIntake
                                        val sharedPref = context.getSharedPreferences("NutriTrackUserPrefs_FoodIntake", Context.MODE_PRIVATE)
                                        val keyPrefix = "${currentUserId}_"
                                        with(sharedPref.edit()) {
                                            putString(keyPrefix + "SelectedCategories", selectedCategories.joinToString(","))
                                            putString(keyPrefix + "PersonaDropdown", selectedPersonaInDropdown)
                                            putString(keyPrefix + "BiggestMealTime", biggestMealTime)
                                            putString(keyPrefix + "SleepTime", sleepTime)
                                            putString(keyPrefix + "WakeUpTime", wakeUpTime)
                                            apply()
                                        }
                                        onSaveComplete() // Navigate or show completion message
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedCategories.isNotEmpty() && selectedPersonaInDropdown.isNotBlank() && biggestMealTime.isNotBlank() && sleepTime.isNotBlank() && wakeUpTime.isNotBlank()
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }

    if (showPersonaModal && modalPersona != null) {
        val personaSelected = modalPersona!! // Safe due to the condition
        AlertDialog(
            onDismissRequest = { foodIntakeViewModel.setShowPersonaModal(false) },
            icon = {
                Image(
                    painter = painterResource(id = personaSelected.imageRes),
                    contentDescription = personaSelected.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Fit
                )
            },
            title = { Text(text = personaSelected.name, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    item {
                        Text(text = stringResource(id = personaSelected.descriptionId))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { foodIntakeViewModel.setShowPersonaModal(false) }) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@Composable
fun TimePickerItem(label: String, currentTimeText: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = label, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    onClickLabel = "Select Time for $label"
                ) {
                    val currentHour = if (currentTimeText.isNotBlank() && currentTimeText.contains(":")) {
                        currentTimeText.split(":")[0].toIntOrNull() ?: 12
                    } else 12
                    val currentMinute = if (currentTimeText.isNotBlank() && currentTimeText.contains(":")) {
                        currentTimeText.split(":")[1].toIntOrNull() ?: 0
                    } else 0
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
                            onTimeSelected(formattedTime)
                        },
                        currentHour,
                        currentMinute,
                        true
                    ).show()
                }
        ) {
            OutlinedTextField(
                value = currentTimeText,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Select") },
                readOnly = true,
                enabled = false, // Visually disabled, click handled by Box
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                trailingIcon = {
                    Icon(painterResource(id = R.drawable.ic_clock), contentDescription = "Select Time")
                },
                singleLine = true
            )
        }
    }
}