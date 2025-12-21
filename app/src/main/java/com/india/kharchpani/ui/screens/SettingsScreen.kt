package com.india.kharchpani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
    val scrollState = rememberScrollState()

    val tutorialEnglish = """
    🌟 Getting Started
    • Add Expenses: Tap '+' on Home.
    • Edit/Delete: Double-tap any item.
    • Selection Mode: Long-press to select items.
    • Mark Paid: Use the 'Done' icon in selection mode.

    📊 Calculation Logic
    • Week: Starts on Sunday and ends on Saturday.
    • Last Week: Previous full Sunday-to-Saturday cycle.
    • Month: From the 1st to the last day of current month.
    • Year: From Jan 1st to Dec 31st of current year.
    • Yesterday: Precisely the previous calendar day.

    🛡️ Data & Storage
    • Offline Only: Data stays only on your device.
    • Uninstall Protection: Data is stored in your chosen folder via SAF, so it survives app deletion.
    """.trimIndent()

    val tutorialHindi = """
    🌟 शुरुआत कैसे करें
    • खर्च जोड़ें: होम पर '+' दबाएं।
    • बदलें/हटाएं: किसी भी आइटम पर डबल-टैप करें।
    • सिलेक्शन मोड: चुनने के लिए लंबे समय तक दबाएं।
    • भुगतान चिह्नित करें: सिलेक्शन मोड में 'Done' आइकन का उपयोग करें।

    📊 गणना पद्धति
    • सप्ताह: रविवार से शुरू और शनिवार को समाप्त होता है।
    • पिछला सप्ताह: पिछला पूरा रविवार-से-शनिवार चक्र।
    • महीना: वर्तमान महीने की 1 तारीख से आखिरी दिन तक।
    • वर्ष: वर्तमान वर्ष की 1 जनवरी से 31 दिसंबर तक।
    • कल (Yesterday): ठीक पिछला कैलेंडर दिन।

    🛡️ डेटा और स्टोरेज
    • केवल ऑफलाइन: डेटा केवल आपके डिवाइस पर रहता है।
    • अनइंस्टॉल सुरक्षा: डेटा आपके चुने हुए फोल्डर (SAF) में रहता है, इसलिए ऐप हटाने पर भी सुरक्षित रहता है।
    """.trimIndent()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = "Theme Preference", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Light", "Dark", "System").forEach { theme ->
                FilterChip(
                    selected = currentTheme == theme,
                    onClick = { themeViewModel.saveTheme(theme) },
                    label = { Text(theme) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "How it Works & Usage", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = "English", style = MaterialTheme.typography.bodySmall)
            Switch(
                checked = showHindi,
                onCheckedChange = { showHindi = it },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(text = "हिंदी", style = MaterialTheme.typography.bodySmall)
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = if (showHindi) tutorialHindi else tutorialEnglish,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "App Details", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Version", fontWeight = FontWeight.Bold)
                Text(text = versionName ?: "N/A")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
