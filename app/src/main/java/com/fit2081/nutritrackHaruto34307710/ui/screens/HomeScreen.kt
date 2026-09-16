// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/ui/screens/HomeScreen.kt
package com.fit2081.nutritrackHaruto34307710.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.fit2081.nutritrackHaruto34307710.NutriTrackApplication
import com.fit2081.nutritrackHaruto34307710.ui.BottomNavigationBar
import com.fit2081.nutritrackHaruto34307710.viewModel.HomeViewModel
import com.fit2081.nutritrackHaruto34307710.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass,
    userName: String,
    userId: String,
    phoneNumber: String
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.HomeViewModelFactory(
            (LocalContext.current.applicationContext as NutriTrackApplication).repository,
            userId
        )
    )
    val uiState by homeViewModel.uiState.collectAsState()

    val foodQualityScore = uiState.foodQualityScore
    val currentUserName = uiState.patientName

    val scoreColor = when {
        foodQualityScore >= 80f -> Color(0xFF4CAF50)
        foodQualityScore >= 50f -> Color(0xFFFFC107)
        else -> MaterialTheme.colorScheme.error
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        topBar = {
            TopAppBar(
                title = { Text("NutriTrack Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    Text(
                        text = "Hello,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = currentUserName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.edit_encourage),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { navController.navigate("questionnaire") }) {
                        Text("Edit")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.salad),
                            contentDescription = "Healthy salad bowl",
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.5f)
                                .heightIn(max = 220.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            ScoreDisplaySection(foodQualityScore, scoreColor, onInsightsClick = { navController.navigate("insights") })
                        }
                    }
                } else {
                    Image(
                        painter = painterResource(R.drawable.salad),
                        contentDescription = "Healthy salad bowl",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ScoreDisplaySection(foodQualityScore, scoreColor, onInsightsClick = { navController.navigate("insights") })
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "What is the Food Quality Score?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.foodScore_exp),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ScoreDisplaySection(
    foodQualityScore: Float,
    scoreColor: Color,
    onInsightsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "My Score",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "See all scores >",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onInsightsClick() }
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = buildAnnotatedString {
            append("Your Food Quality score: ")
            withStyle(style = SpanStyle(color = scoreColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)) {
                append("${"%.1f".format(foodQualityScore)}/100")
            }
        },
        style = MaterialTheme.typography.bodyLarge
    )
}