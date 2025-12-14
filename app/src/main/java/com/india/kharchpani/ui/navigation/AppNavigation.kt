package com.india.kharchpani.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.india.kharchpani.ui.screens.AddEditExpenseScreen
import com.india.kharchpani.ui.screens.AnalyticsScreen
import com.india.kharchpani.ui.screens.ExportImportScreen
import com.india.kharchpani.ui.screens.HomeScreen
import com.india.kharchpani.ui.screens.SettingsScreen
import com.india.kharchpani.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
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
