package com.india.kharchpani.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SummaryCard(title: String, amount: Double, modifier: Modifier = Modifier) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val formattedAmount = format.format(amount)

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)
            Text(text = formattedAmount, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
