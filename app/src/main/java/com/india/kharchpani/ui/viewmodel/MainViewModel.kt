package com.india.kharchpani.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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

data class AdvancedSummaryData(
    val averageDailySpend: Double = 0.0,
    val monthOverMonthChange: Double = 0.0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val jsonDataHelper = JsonDataHelper(application)
    private var directoryUri: Uri? = null
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    private var allExpenses = listOf<Expense>()
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(Filter.ALL)
    val filter: StateFlow<Filter> = _filter.asStateFlow()

    val selectedExpenses = mutableStateListOf<Expense>()
    val isInSelectionMode: Boolean
        get() = selectedExpenses.isNotEmpty()

    fun toggleExpenseSelection(expense: Expense) {
        val index = selectedExpenses.indexOfFirst { it.id == expense.id }
        if (index >= 0) {
            selectedExpenses.removeAt(index)
        } else {
            selectedExpenses.add(expense)
        }
    }

    fun clearSelection() {
        selectedExpenses.clear()
    }

    fun toggleSelectedExpensesPaidStatus() {
        viewModelScope.launch {
            val selectedIds = selectedExpenses.map { it.id }.toSet()
            val updatedExpenses = allExpenses.map {
                if (it.id in selectedIds) {
                    it.copy(isPaid = !it.isPaid)
                } else {
                    it
                }
            }
            directoryUri?.let { jsonDataHelper.writeExpenses(it, updatedExpenses) }
            clearSelection()
            loadExpenses()
        }
    }

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

                    _uiState.value = HomeUiState.Success(
                        expenses = allExpenses,
                        todayTotal = calculateTodayTotal(allExpenses, today),
                        yesterdayTotal = calculateYesterdayTotal(allExpenses, today),
                        weeklyTotal = calculateWeeklyTotal(allExpenses, today),
                        monthlyTotal = calculateMonthlyTotal(allExpenses, today),
                        lastWeekTotal = calculateLastWeekTotal(allExpenses, today),
                        currentYearTotal = calculateCurrentYearTotal(allExpenses, today),
                        advancedSummaryData = calculateAdvancedSummaryData(allExpenses, today),
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

    private fun calculateTodayTotal(expenses: List<Expense>, today: LocalDate): Double {
        return expenses.filter { safeParseDate(it.date)?.isEqual(today) == true }.sumOf { it.amount }
    }

    private fun calculateYesterdayTotal(expenses: List<Expense>, today: LocalDate): Double {
        val yesterday = today.minusDays(1)
        return expenses.filter { safeParseDate(it.date)?.isEqual(yesterday) == true }.sumOf { it.amount }
    }

    private fun calculateWeeklyTotal(expenses: List<Expense>, today: LocalDate): Double {
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endOfWeek = startOfWeek.plusDays(6)
        return expenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> !expenseDate.isBefore(startOfWeek) && !expenseDate.isAfter(endOfWeek) }
            .sumOf { it.second.amount }
    }

    private fun calculateLastWeekTotal(expenses: List<Expense>, today: LocalDate): Double {
        val startOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endOfLastWeek = startOfLastWeek.plusDays(6)
        return expenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> !expenseDate.isBefore(startOfLastWeek) && !expenseDate.isAfter(endOfLastWeek) }
            .sumOf { it.second.amount }
    }

    private fun calculateMonthlyTotal(expenses: List<Expense>, today: LocalDate): Double {
        return expenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> expenseDate.month == today.month && expenseDate.year == today.year }
            .sumOf { it.second.amount }
    }

    private fun calculateCurrentYearTotal(expenses: List<Expense>, today: LocalDate): Double {
        return expenses
            .mapNotNull { expense -> safeParseDate(expense.date)?.let { it to expense } }
            .filter { (expenseDate, _) -> expenseDate.year == today.year }
            .sumOf { it.second.amount }
    }

    private fun calculateAdvancedSummaryData(expenses: List<Expense>, today: LocalDate): AdvancedSummaryData {
        val currentMonthExpenses = expenses.filter { 
            val date = safeParseDate(it.date)
            date != null && date.month == today.month && date.year == today.year 
        }
        val lastMonthExpenses = expenses.filter { 
            val date = safeParseDate(it.date)
            val lastMonth = today.minusMonths(1)
            date != null && date.month == lastMonth.month && date.year == lastMonth.year 
        }

        val averageDailySpend = if (currentMonthExpenses.isNotEmpty()) {
            currentMonthExpenses.sumOf { it.amount } / today.dayOfMonth
        } else {
            0.0
        }

        val currentMonthTotal = currentMonthExpenses.sumOf { it.amount }
        val lastMonthTotal = lastMonthExpenses.sumOf { it.amount }

        val monthOverMonthChange = if (lastMonthTotal > 0) {
            ((currentMonthTotal - lastMonthTotal) / lastMonthTotal) * 100
        } else if (currentMonthTotal > 0) {
            100.0
        } else {
            0.0
        }

        return AdvancedSummaryData(averageDailySpend, monthOverMonthChange)
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
        val yesterdayTotal: Double = 0.0,
        val weeklyTotal: Double = 0.0,
        val monthlyTotal: Double = 0.0,
        val lastWeekTotal: Double = 0.0,
        val currentYearTotal: Double = 0.0,
        val advancedSummaryData: AdvancedSummaryData = AdvancedSummaryData(),
        val weeklyChartData: List<ChartData> = emptyList(),
        val monthlyChartData: List<ChartData> = emptyList()
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
