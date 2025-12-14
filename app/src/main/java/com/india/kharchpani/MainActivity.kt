package com.india.kharchpani

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import com.india.kharchpani.ui.navigation.AppNavigation
import com.india.kharchpani.ui.theme.KharchPaniTheme
import com.india.kharchpani.ui.viewmodel.MainViewModel
import com.india.kharchpani.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    private val openDocumentTreeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.data?.also { uri ->
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            sharedPreferences.edit().putString("expenses_dir_uri", uri.toString()).apply()
            viewModel.initialize(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val expensesDirUri = sharedPreferences.getString("expenses_dir_uri", null)?.toUri()

        if (expensesDirUri == null) {
            openDocumentTreeLauncher.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
        } else {
            viewModel.initialize(expensesDirUri)
        }

        setContent {
            val theme by themeViewModel.theme.collectAsState()
            KharchPaniTheme(theme = theme) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
