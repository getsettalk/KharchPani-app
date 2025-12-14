package com.india.kharchpani.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.india.kharchpani.data.model.Expense
import com.india.kharchpani.data.repository.JsonDataHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAdjusters

enum class Filter { TODAY, WEEK, MONTH, ALL }

data class ChartData(val label: String, val amount: Double)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val jsonDataHelper = JsonDataHelper(application)
    private var directoryUri: Uri? = null
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    private var allExpenses = listOf<Expense>()
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(Filter.ALL)
    val filter: StateFlow<Filter> = _filter.asStateFlow()

    fun initialize(uri: Uri) {
        directoryUri = uri
        loadExpenses()
    }

    fun setFilter(filter: Filter) {
        _filter.value = filter
        applyFilter()
    }

    fun getExpensesFileUri(): Uri? {
        return directoryUri?.let { jsonDataHelper.getExpensesFileUri(it) }
    }

    private fun safeParseDate(date: String): LocalDate? {
        return try {
            LocalDate.parse(date, formatter)
        } catch (e: DateTimeParseException) {
            Log.e("MainViewModel", "Failed to parse date: $date", e)
            null
        }
    }

    private fun applyFilter() {
        val filteredExpenses = when (filter.value) {
            Filter.TODAY -> {
                val today = LocalDate.now()
                allExpenses.filter { safeParseDate(it.date)?.isEqual(today) == true }
            }
            Filter.WEEK -> {
                val today = LocalDate.now()
                val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                val endOfWeek = startOfWeek.plusDays(6)
                allExpenses.filter {
                    val expenseDate = safeParseDate(it.date)
                    expenseDate != null && !expenseDate.isBefore(startOfWeek) && !expenseDate.isAfter(endOfWeek)
                }
            }
            Filter.MONTH -> {
                val today = LocalDate.now()
                allExpenses.filter { 
                    val expenseDate = safeParseDate(it.date)
                    expenseDate != null && expenseDate.month == today.month && expenseDate.year == today.year
                }
            }
            Filter.ALL -> allExpenses
        }

        _uiState.update {
            if (it is HomeUiState.Success) {
                it.copy(expenses = filteredExpenses)
            } else {
                it
            }
        }
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                directoryUri?.let { uri ->
                    allExpenses = jsonDataHelper.readExpenses(uri).sortedByDescending { expense ->
                        safeParseDate(expense.date)
                    }
                    
                    val today = LocalDate.now()

                    val todayTotal = calculateTodayTotal(allExpenses)
                    val weeklyTotal = calculateWeeklyTotal(allExpenses)
                    val monthlyTotal = calculateMonthlyTotal(allExpenses)

                    _uiState.value = HomeUiState.Success(
                        expenses = allExpenses,
                        todayTotal = todayTotal,
                        weeklyTotal = weeklyTotal,
                        monthlyTotal = monthlyTotal,
                        weeklyChartData = getWeeklyChartData(),
                        monthlyChartData = getMonthlyChartData()
                    )
                    applyFilter() // Apply initial filter
                } ?: run {
                    _uiState.value = HomeUiState.Error("Storage location not set.")
                }
            } catch(e: Exception) {
                Log.e("MainViewModel", "Error loading expenses", e)
                _uiState.value = HomeUiState.Error("Failed to load or parse expenses.json. Check the file for errors.")
            }
        }
    }
    
    private fun getWeeklyChartData(): List<ChartData> {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endOfWeek = startOfWeek.plusDays(6)

        return allExpenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> !expenseDate.isBefore(startOfWeek) && !expenseDate.isAfter(endOfWeek) }
            .groupBy { it.first.dayOfWeek.name.take(3) }
            .map { (day, expenses) -> ChartData(day, expenses.sumOf { it.second.amount }) }
    }

    private fun getMonthlyChartData(): List<ChartData> {
        val today = LocalDate.now()
        return allExpenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> expenseDate.month == today.month && expenseDate.year == today.year }
            .groupBy { it.first.dayOfMonth.toString() }
            .map { (day, expenses) -> ChartData(day, expenses.sumOf { it.second.amount }) }
    }

    fun getExpenseById(id: String): Expense? {
        return allExpenses.find { it.id == id }
    }

    private fun calculateTodayTotal(expenses: List<Expense>): Double {
        val today = LocalDate.now()
        return expenses.filter { safeParseDate(it.date)?.isEqual(today) == true }.sumOf { it.amount }
    }

    private fun calculateWeeklyTotal(expenses: List<Expense>): Double {
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endOfWeek = startOfWeek.plusDays(6)
        return expenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> !expenseDate.isBefore(startOfWeek) && !expenseDate.isAfter(endOfWeek) }
            .sumOf { it.second.amount }
    }

    private fun calculateMonthlyTotal(expenses: List<Expense>): Double {
        val today = LocalDate.now()
        return expenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> expenseDate.month == today.month && expenseDate.year == today.year }
            .sumOf { it.second.amount }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            directoryUri?.let { uri ->
                val currentExpenses = allExpenses.toMutableList()
                currentExpenses.add(expense)
                jsonDataHelper.writeExpenses(uri, currentExpenses)
                loadExpenses()
            }
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            directoryUri?.let { uri ->
                val currentExpenses = allExpenses.toMutableList()
                val index = currentExpenses.indexOfFirst { it.id == expense.id }
                if (index != -1) {
                    currentExpenses[index] = expense.copy(updatedAt = System.currentTimeMillis())
                    jsonDataHelper.writeExpenses(uri, currentExpenses)
                    loadExpenses()
                }
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            directoryUri?.let { uri ->
                val currentExpenses = allExpenses.toMutableList()
                currentExpenses.removeAll { it.id == expense.id }
                jsonDataHelper.writeExpenses(uri, currentExpenses)
                loadExpenses()
            }
        }
    }

    fun importExpenses(importUri: Uri, merge: Boolean) {
        viewModelScope.launch {
            directoryUri?.let { dirUri ->
                val importedExpenses = jsonDataHelper.readExpensesFromFile(importUri)
                val currentExpenses = if (merge) allExpenses.toMutableList() else mutableListOf()
                currentExpenses.addAll(importedExpenses)
                val distinctExpenses = currentExpenses.distinctBy { it.id }
                jsonDataHelper.writeExpenses(dirUri, distinctExpenses)
                loadExpenses()
            }
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val expenses: List<Expense>,
        val todayTotal: Double = 0.0,
        val weeklyTotal: Double = 0.0,
        val monthlyTotal: Double = 0.0,
        val weeklyChartData: List<ChartData> = emptyList(),
        val monthlyChartData: List<ChartData> = emptyList()
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
