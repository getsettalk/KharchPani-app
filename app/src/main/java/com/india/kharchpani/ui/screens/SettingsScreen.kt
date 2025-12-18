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
    🌟 Mastering KharchPani

    ✅  Marking Expenses as Paid 
    1.   Long-Press : On the Home or History screen, long-press any expense to enter 'Selection Mode'.
    2.   Select Multiple : Once in selection mode, tap other expenses to add them to your selection.
    3.   Confirm Status : Tap the checkmark icon (Done) in the top bar. All selected items will now appear with a strikethrough and a subtle red tint, indicating they are paid.
    4.   Undo/Un-mark : To mark items as unpaid again, repeat the process. The status will toggle back.

    📝  Other Useful Tips 
    -  Edit/Delete : Quickly double-tap any item to open the Edit screen.
    -  Date Filters : Use the History tab to find expenses within a custom date range.
    -  Backup : Use the 'Export & Import' tab to save your data as a JSON file.
    """

    val tutorialHindi = """
    🌟  खर्चपानी (KharchPani) में महारत हासिल करें 

    ✅  खर्च को 'Paid' (भुगतान किया गया) के रूप में चिह्नित करें 
    1.   लंबे समय तक दबाएं (Long-Press) : होम या इतिहास स्क्रीन पर, 'सिलेक्शन मोड' में प्रवेश करने के लिए किसी भी खर्च पर लंबे समय तक दबाएं।
    2.   एकाधिक चुनें : सिलेक्शन मोड में आने के बाद, अन्य खर्चों को चुनने के लिए उन पर टैप करें।
    3.   स्थिति की पुष्टि करें : शीर्ष बार में चेकमार्क (Done) आइकन पर टैप करें। सभी चयनित आइटम अब स्ट्राइकथ्रू और हल्के लाल रंग के साथ दिखाई देंगे।
    4.   अन-मार्क करें : आइटम को फिर से अनपेड के रूप में चिह्नित करने के लिए, प्रक्रिया को दोहराएं। स्थिति बदल जाएगी।

    📝  अन्य उपयोगी सुझाव 
    -  संपादित करें/हटाएं : किसी भी आइटम को संपादित करने या हटाने के लिए उस पर जल्दी से डबल-टैप करें।
    -  तिथि फ़िल्टर : कस्टम तिथि सीमा के भीतर खर्च खोजने के लिए इतिहास (History) टैब का उपयोग करें।
    -  बैकअप : अपने डेटा को JSON फ़ाइल के रूप में सहेजने के लिए 'एक्सपोर्ट और इंपोर्ट' टैब का उपयोग करें।
    """

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

        Text(text = "How to Use (Tutorial)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                modifier = Modifier.padding(10.dp),
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
