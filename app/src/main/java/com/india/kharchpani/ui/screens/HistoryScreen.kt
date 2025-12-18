package com.india.kharchpani.ui.screens

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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.india.kharchpani.ui.composables.ExpenseListItem
import com.india.kharchpani.ui.viewmodel.MainViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController, viewModel: MainViewModel) {
    val expenses by viewModel.historyExpenses.collectAsState()
    var showRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    
    var filterText by remember { mutableStateOf("All Records") }

    if (showRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis
                    
                    if (startMillis != null) {
                        val start = Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                        val end = endMillis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
                        
                        viewModel.filterHistory(start, end)
                        filterText = if (end == null) "Date: ${start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}" 
                                     else "${start.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))} to ${end.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))}"
                    }
                    showRangePicker = false
                }) { Text("Apply") }
            },
            dismissButton = {
                TextButton(onClick = { showRangePicker = false }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Filtering", style = MaterialTheme.typography.labelMedium)
                Text(text = filterText, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { 
                viewModel.filterHistory(null, null)
                filterText = "All Records"
            }) {
                Icon(Icons.Default.FilterListOff, contentDescription = "Clear Filter")
            }
            Button(onClick = { showRangePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.padding(horizontal = 4.dp))
                Text("Pick Date")
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(expenses, key = { it.id }) { expense ->
                ExpenseListItem(
                    expense = expense,
                    isSelected = viewModel.selectedExpenses.any { it.id == expense.id },
                    isInSelectionMode = viewModel.isInSelectionMode,
                    onClick = {
                        if (viewModel.isInSelectionMode) {
                            viewModel.toggleExpenseSelection(expense)
                        }
                    },
                    onLongClick = { viewModel.toggleExpenseSelection(expense) },
                    onDoubleClick = {
                        if (!viewModel.isInSelectionMode) {
                            navController.navigate("add_edit_expense?expenseId=${expense.id}")
                        }
                    }
                )
            }
        }
    }
}
