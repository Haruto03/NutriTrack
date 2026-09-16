// Path: NutriTrack/app/src/main/java/com/fit2081/nutritrack/ui/screens/WelcomeScreen.kt
package com.fit2081.nutritrackHaruto34307710.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.nutritrackHaruto34307710.R
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLandscape && windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "NutriTrack",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.nutri_track_logo),
                        contentDescription = "NutriTrack Logo",
                        modifier = Modifier.size(120.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(start = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.welcome_exp),
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onNavigateToLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Get Started", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Designed by Haruto Iriyama (34307710)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "NutriTrack",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.nutri_track_logo),
                    contentDescription = "NutriTrack Logo",
                    modifier = Modifier.size(150.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(id = R.string.welcome_exp),
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Get Started", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Designed by Haruto Iriyama (34307710)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}