package com.india.kharchpani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.india.kharchpani.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, themeViewModel: ThemeViewModel = viewModel()) {
    val currentTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: Exception) {
        "N/A"
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { navController.navigate("analytics") }) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
                    }
                    IconButton(onClick = { navController.navigate("export_import") }) {
                        Icon(Icons.Default.ImportExport, contentDescription = "Export/Import")
                    }
                    IconButton(onClick = { /* Already on settings screen */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = currentTheme == "Light",
                    onClick = { themeViewModel.saveTheme("Light") },
                    label = { Text("Light") }
                )
                FilterChip(
                    selected = currentTheme == "Dark",
                    onClick = { themeViewModel.saveTheme("Dark") },
                    label = { Text("Dark") }
                )
                FilterChip(
                    selected = currentTheme == "System",
                    onClick = { themeViewModel.saveTheme("System") },
                    label = { Text("System") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "App Version", style = MaterialTheme.typography.titleMedium)
            Text(text = versionName ?: "N/A")
        }
    }
}
