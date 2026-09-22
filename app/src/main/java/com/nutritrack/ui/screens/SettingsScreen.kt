package com.nutritrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nutritrack.AuthManager
import com.nutritrack.ui.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val userName = AuthManager.currentUserName
    val userId = AuthManager.currentUserId
    val userPhone = AuthManager.currentUserPhone

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text("ACCOUNT", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(userName ?: "N/A", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(userPhone ?: "N/A", style = MaterialTheme.typography.bodyLarge)
                    Text(userId ?: "N/A", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("OTHER SETTINGS", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Logout") },
                leadingContent = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout") },
                trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = "Go to logout")},
                modifier = Modifier.clickable {
                    AuthManager.logout()
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            HorizontalDivider()
            ListItem(
                headlineContent = { Text("Clinician Login") },
                leadingContent = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Clinician Login") },
                trailingContent = { Icon(Icons.Default.ArrowForwardIos, contentDescription = "Go to clinician login")},
                modifier = Modifier.clickable {
                    navController.navigate("clinician_login")
                }
            )
            HorizontalDivider()
        }
    }
}