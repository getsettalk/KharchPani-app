package com.india.kharchpani.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.india.kharchpani.utils.formatCurrency

@Composable
fun AdvancedSummaryCard(
    title: String,
    amount: Double,
    subtitle: String,
    percent: Double,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = formatCurrency(amount), style = MaterialTheme.typography.headlineSmall)
            val percentColor = if (percent >= 0) Color(0xFF006A60) else Color.Red
            val sign = if (percent >= 0) "+" else ""
            Text(
                text = "$subtitle $sign${String.format("%.1f", percent)}%",
                style = MaterialTheme.typography.bodySmall,
                color = percentColor
            )
        }
    }
}
