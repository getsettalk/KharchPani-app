package com.india.kharchpani.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddEditExpenseViewModelFactory(private val mainViewModel: MainViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEditExpenseViewModel(mainViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
