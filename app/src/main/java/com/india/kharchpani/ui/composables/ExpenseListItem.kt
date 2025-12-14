package com.india.kharchpani.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.india.kharchpani.data.model.Expense
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpenseListItem(
    expense: Expense, 
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val formattedAmount = format.format(expense.amount)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = expense.description, modifier = Modifier.weight(1f))
            Text(text = formattedAmount)
        }
    }
}
