package com.india.kharchpani.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeDataStore(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme")

    val getTheme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[themeKey] ?: "System"
        }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { settings ->
            settings[themeKey] = theme
        }
    }
}
