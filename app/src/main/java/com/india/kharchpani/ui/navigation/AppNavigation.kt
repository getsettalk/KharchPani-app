package com.india.kharchpani.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.india.kharchpani.ui.composables.KharchPaniBottomAppBar
import com.india.kharchpani.ui.composables.KharchPaniTopAppBar
import com.india.kharchpani.ui.screens.AddEditExpenseScreen
import com.india.kharchpani.ui.screens.AnalyticsScreen
import com.india.kharchpani.ui.screens.ExportImportScreen
import com.india.kharchpani.ui.screens.HomeScreen
import com.india.kharchpani.ui.screens.SettingsScreen
import com.india.kharchpani.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitles = mapOf(
        "home" to "KharchPani",
        "analytics" to "Analytics",
        "export_import" to "Export & Import",
        "settings" to "Settings",
        "add_edit_expense?expenseId={expenseId}" to if(navBackStackEntry?.arguments?.getString("expenseId") == null) "Add Expense" else "Edit Expense"
    )

    Scaffold(
        topBar = {
            if (viewModel.isInSelectionMode) {
                KharchPaniTopAppBar(
                    title = "${viewModel.selectedExpenses.size} selected",
                    canNavigateBack = true,
                    onNavigateUp = { viewModel.clearSelection() },
                    actions = {
                        IconButton(onClick = { viewModel.toggleSelectedExpensesPaidStatus() }) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Toggle Paid Status")
                        }
                    }
                )
            } else {
                KharchPaniTopAppBar(
                    title = topBarTitles[currentRoute] ?: "KharchPani",
                    canNavigateBack = navController.previousBackStackEntry != null && currentRoute != "home",
                    onNavigateUp = { navController.navigateUp() }
                )
            }
        },
        floatingActionButton = {
            if (currentRoute == "home" && !viewModel.isInSelectionMode) {
                FloatingActionButton(onClick = { navController.navigate("add_edit_expense") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        },
        bottomBar = {
            if (currentRoute in listOf("home", "analytics", "export_import", "settings") && !viewModel.isInSelectionMode) {
                KharchPaniBottomAppBar(
                    currentRoute = currentRoute,
                    navController = navController
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = navController, viewModel = viewModel)
            }
            composable(
                route = "add_edit_expense?expenseId={expenseId}",
                arguments = listOf(navArgument("expenseId") {
                    type = NavType.StringType
                    nullable = true
                })
            ) {
                val expenseId = it.arguments?.getString("expenseId")
                AddEditExpenseScreen(
                    navController = navController,
                    mainViewModel = viewModel,
                    expenseId = expenseId
                )
            }
            composable("analytics") {
                AnalyticsScreen(navController = navController, viewModel = viewModel)
            }
            composable("export_import") {
                ExportImportScreen(navController = navController, viewModel = viewModel)
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
        }
    }
}
