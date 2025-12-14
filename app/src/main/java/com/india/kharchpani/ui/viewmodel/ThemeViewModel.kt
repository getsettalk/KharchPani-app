package com.india.kharchpani.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.india.kharchpani.data.datastore.ThemeDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themeDataStore = ThemeDataStore(application)

    val theme: StateFlow<String> = themeDataStore.getTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "System"
    )

    fun saveTheme(theme: String) {
        viewModelScope.launch {
            themeDataStore.saveTheme(theme)
        }
    }
}
