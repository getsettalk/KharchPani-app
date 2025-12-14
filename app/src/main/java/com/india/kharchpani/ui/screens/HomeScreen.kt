package com.india.kharchpani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.india.kharchpani.ui.composables.ExpenseListItem
import com.india.kharchpani.ui.composables.KharchPaniTopAppBar
import com.india.kharchpani.ui.composables.SummaryCard
import com.india.kharchpani.ui.viewmodel.Filter
import com.india.kharchpani.ui.viewmodel.HomeUiState
import com.india.kharchpani.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, 
    navController: NavController, 
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.filter.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { KharchPaniTopAppBar(title = "Home") },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_edit_expense") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { /* Already on home screen */ }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { navController.navigate("analytics") }) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analytics")
                    }
                    IconButton(onClick = { navController.navigate("export_import") }) {
                        Icon(Icons.Default.ImportExport, contentDescription = "Export/Import")
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SummaryCard(title = "Today", amount = state.todayTotal, modifier = Modifier.weight(1f))
                            SummaryCard(title = "This Week", amount = state.weeklyTotal, modifier = Modifier.weight(1f))
                            SummaryCard(title = "This Month", amount = state.monthlyTotal, modifier = Modifier.weight(1f))
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Filter.values().forEach { filter ->
                                FilterChip(
                                    modifier = Modifier.weight(1f),
                                    selected = selectedFilter == filter,
                                    onClick = { viewModel.setFilter(filter) },
                                    label = { Text(filter.name.lowercase().replaceFirstChar { it.titlecase() }) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.expenses.isEmpty()) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(text = "No expenses in this period. Try a different filter!")
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(state.expenses) { expense ->
                                    ExpenseListItem(expense = expense) {
                                        navController.navigate("add_edit_expense?expenseId=${expense.id}")
                                    }
                                }
                            }
                        }
                    }
                }
                is HomeUiState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
