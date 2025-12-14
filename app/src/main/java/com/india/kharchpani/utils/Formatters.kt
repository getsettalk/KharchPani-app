package com.india.kharchpani.utils

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    if (amount < 100000) {
        return format.format(amount)
    }
    val exp = (ln(amount) / ln(1000.0)).toInt()
    val suffix = charArrayOf('k', 'M', 'B', 'T', 'P', 'E')
    val value = String.format("%.1f%c", amount / 1000.0.pow(exp.toDouble()), suffix[exp - 1])
    return "₹$value"
}
