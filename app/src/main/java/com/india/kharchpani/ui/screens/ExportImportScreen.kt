package com.india.kharchpani.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.india.kharchpani.ui.viewmodel.MainViewModel

@Composable
fun ExportImportScreen(navController: NavController, viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf<Uri?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                showImportDialog = uri
            }
        }
    )

    if (showImportDialog != null) {
        AlertDialog(
            onDismissRequest = { showImportDialog = null },
            title = { Text("Import Expenses") },
            text = { Text("Do you want to merge the imported expenses with your existing expenses, or replace them?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.importExpenses(showImportDialog!!, merge = true)
                        showImportDialog = null
                    }
                ) {
                    Text("Merge")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.importExpenses(showImportDialog!!, merge = false)
                        showImportDialog = null
                    }
                ) {
                    Text("Replace")
                }
            }
        )
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
                    IconButton(onClick = { /* Already on Export/Import screen */ }) {
                        Icon(Icons.Default.ImportExport, contentDescription = "Export/Import")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                viewModel.getExpensesFileUri()?.let { uri ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Export Expenses"))
                }
            }) {
                Text("Export Expenses")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { importLauncher.launch("application/json") }) {
                Text("Import Expenses")
            }
        }
    }
}
