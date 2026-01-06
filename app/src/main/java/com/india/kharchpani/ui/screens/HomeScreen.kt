package com.india.kharchpani.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.india.kharchpani.ui.composables.ExpenseListItem
import com.india.kharchpani.ui.composables.SummaryCard
import com.india.kharchpani.ui.viewmodel.Filter
import com.india.kharchpani.ui.viewmodel.HomeUiState
import com.india.kharchpani.ui.viewmodel.MainViewModel
import com.india.kharchpani.utils.formatCurrency
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.filter.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeUiState.Success -> {
            val groupedExpenses = remember(state.expenses) {
                state.expenses.groupBy { it.date }
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Show normal summary cards only when NOT in selection mode
                if (!viewModel.isInSelectionMode) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard(title = "Today", amount = state.todayTotal, modifier = Modifier.weight(1f))
                            SummaryCard(title = "Yesterday", amount = state.yesterdayTotal, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard(title = "This Week", amount = state.weeklyTotal, modifier = Modifier.weight(1f))
                            SummaryCard(title = "This Month", amount = state.monthlyTotal, modifier = Modifier.weight(1f))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Filter.values().forEach { filter ->
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = selectedFilter == filter,
                                onClick = { viewModel.setFilter(filter) },
                                label = { 
                                    Text(
                                        text = filter.name.lowercase().replaceFirstChar { it.titlecase() },
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                } else {
                    // NEW: Show Selected Total Info when in selection mode
                    val selectedTotal = viewModel.selectedExpenses.sumOf { it.amount }
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.medium,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${viewModel.selectedExpenses.size} items selected",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = "Total: ${formatCurrency(selectedTotal)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.expenses.isEmpty()) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(text = "No expenses in this period. Try a different filter!")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        groupedExpenses.forEach { (dateString, expensesForDate) ->
                            stickyHeader {
                                val date = LocalDate.parse(dateString)
                                val formattedDate = date.format(DateTimeFormatter.ofPattern("EEEE, dd-MM-yyyy"))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                                        .padding(horizontal = 16.dp, vertical = 4.dp)

                                ) {
                                    Text(
                                        text = formattedDate,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            items(expensesForDate, key = { it.id }) { expense ->
                                ExpenseListItem(
                                    expense = expense,
                                    isSelected = viewModel.selectedExpenses.any { it.id == expense.id },
                                    isInSelectionMode = viewModel.isInSelectionMode,
                                    onClick = {
                                        if (viewModel.isInSelectionMode) {
                                            viewModel.toggleExpenseSelection(expense)
                                        }
                                    },
                                    onLongClick = {
                                        viewModel.toggleExpenseSelection(expense)
                                    },
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
            }
        }
        is HomeUiState.Error -> {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = state.message, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
