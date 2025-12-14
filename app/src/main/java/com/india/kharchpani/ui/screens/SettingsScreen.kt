package com.india.kharchpani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.india.kharchpani.ui.viewmodel.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, themeViewModel: ThemeViewModel = viewModel()) {
    val currentTheme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current
    val versionName = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: Exception) {
        "N/A"
    }
    var showHindi by remember { mutableStateOf(false) }

    val tutorialEnglish = """
    1. Add expenses from the Home screen using the '+' button.
    2. Long-press an expense to enter selection mode.
    3. Mark selected expenses as paid using the checkmark icon in the top bar.
    4. Double-tap an expense to edit or delete it.
    5. Use the bottom navigation to view analytics, export/import data, and change settings.
    """

    val tutorialHindi = """
    1. होम स्क्रीन पर '+' बटन का उपयोग करके खर्च जोड़ें।
    2. चयन मोड में प्रवेश करने के लिए किसी खर्च पर लंबे समय तक दबाएं।
    3. शीर्ष बार में चेकमार्क आइकन का उपयोग करके चयनित खर्चों को भुगतान के रूप में चिह्नित करें।
    4. किसी खर्च को संपादित करने या हटाने के लिए उस पर डबल-टैप करें।
    5. एनालिटिक्स देखने, डेटा निर्यात/आयात करने और सेटिंग्स बदलने के लिए नीचे नेविगेशन का उपयोग करें।
    """

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = currentTheme == "Light",
                onClick = { themeViewModel.saveTheme("Light") },
                label = { Text("Light") }
            )
            FilterChip(
                selected = currentTheme == "Dark",
                onClick = { themeViewModel.saveTheme("Dark") },
                label = { Text("Dark") }
            )
            FilterChip(
                selected = currentTheme == "System",
                onClick = { themeViewModel.saveTheme("System") },
                label = { Text("System") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "App Version", style = MaterialTheme.typography.titleMedium)
        Text(text = versionName ?: "N/A")

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "How to Use", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Text("English")
            Switch(checked = showHindi, onCheckedChange = { showHindi = it })
            Text("हिंदी")
        }
        Text(text = if (showHindi) tutorialHindi else tutorialEnglish, style = MaterialTheme.typography.bodyLarge)
    }
}
