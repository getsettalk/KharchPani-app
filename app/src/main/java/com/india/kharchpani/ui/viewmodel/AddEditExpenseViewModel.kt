package com.india.kharchpani.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.india.kharchpani.data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddEditExpenseViewModel(private val mainViewModel: MainViewModel) : ViewModel() {

    private val _uiState = MutableStateFlow<AddEditExpenseUiState>(AddEditExpenseUiState.Idle)
    val uiState: StateFlow<AddEditExpenseUiState> = _uiState.asStateFlow()

    var description by mutableStateOf("")
    var amount by mutableStateOf("")
    var date by mutableStateOf(LocalDate.now())

    private var expenseId: String? = null

    fun initialize(expenseId: String?) {
        if (expenseId == null) return
        this.expenseId = expenseId
        val expense = mainViewModel.getExpenseById(expenseId) ?: return

        description = expense.description
        amount = expense.amount.toString()
        date = LocalDate.parse(expense.date)
    }

    fun onDescriptionChange(newDescription: String) {
        description = newDescription
    }

    fun onAmountChange(newAmount: String) {
        amount = newAmount
    }

    fun onDateChange(newDate: LocalDate) {
        date = newDate
    }

    fun saveExpense() {
        val amountAsDouble = amount.toDoubleOrNull()
        if (description.isBlank() || amountAsDouble == null) {
            _uiState.value = AddEditExpenseUiState.Error("Please fill all fields.")
            return
        }

        viewModelScope.launch {
            val expenseToSave = Expense(
                id = expenseId ?: java.util.UUID.randomUUID().toString(),
                description = description,
                amount = amountAsDouble,
                date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                createdAt = mainViewModel.getExpenseById(expenseId ?: "")?.createdAt ?: System.currentTimeMillis(),
                updatedAt = if (expenseId != null) System.currentTimeMillis() else null
            )

            if (expenseId == null) {
                mainViewModel.addExpense(expenseToSave)
            } else {
                mainViewModel.updateExpense(expenseToSave)
            }
            _uiState.value = AddEditExpenseUiState.Success
        }
    }

    fun deleteExpense() {
        viewModelScope.launch {
            expenseId?.let { mainViewModel.getExpenseById(it) }?.let {
                mainViewModel.deleteExpense(it)
                _uiState.value = AddEditExpenseUiState.Success
            }
        }
    }
}

sealed interface AddEditExpenseUiState {
    data object Idle : AddEditExpenseUiState
    data object Success : AddEditExpenseUiState
    data class Error(val message: String) : AddEditExpenseUiState
}
