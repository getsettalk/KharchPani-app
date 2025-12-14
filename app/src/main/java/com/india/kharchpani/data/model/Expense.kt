package com.india.kharchpani.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val date: String, // ISO-8601 yyyy-MM-dd
    val description: String,
    val amount: Double,
    val createdAt: Long,
    val updatedAt: Long? = null,
    val isPaid: Boolean = false
)
