package com.india.kharchpani.data.repository

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.india.kharchpani.data.model.Expense
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader

class JsonDataHelper(private val context: Context) {

    companion object {
        private const val FILE_NAME = "expenses.json"
        private const val MIME_TYPE = "application/json"
    }

    private fun getExpensesFile(directoryUri: Uri): DocumentFile? {
        val directory = DocumentFile.fromTreeUri(context, directoryUri) ?: return null
        var file = directory.findFile(FILE_NAME)
        if (file == null || !file.exists()) {
            file = directory.createFile(MIME_TYPE, FILE_NAME)
        }
        return file
    }

    fun getExpensesFileUri(directoryUri: Uri): Uri? {
        return getExpensesFile(directoryUri)?.uri
    }

    fun readExpenses(directoryUri: Uri): List<Expense> {
        val file = getExpensesFile(directoryUri) ?: return emptyList()
        return readExpensesFromFile(file.uri)
    }

    fun readExpensesFromFile(fileUri: Uri): List<Expense> {
        val contentResolver = context.contentResolver
        val stringBuilder = StringBuilder()
        try {
            contentResolver.openInputStream(fileUri)?.use {
                BufferedReader(InputStreamReader(it)).use {
                    var line: String?
                    do {
                        line = it.readLine()
                        if (line != null) stringBuilder.append(line)
                    } while (line != null)
                }
            }
            if (stringBuilder.isNotBlank()) {
                return Json.decodeFromString<List<Expense>>(stringBuilder.toString())
            }
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    fun writeExpenses(directoryUri: Uri, expenses: List<Expense>) {
        val file = getExpensesFile(directoryUri) ?: return
        val jsonString = Json.encodeToString(expenses)
        val contentResolver = context.contentResolver
        try {
            // Use "wt" to TRUNCATE the file before writing.
            // This is the critical fix to prevent data corruption.
            contentResolver.openFileDescriptor(file.uri, "wt")?.use {
                FileOutputStream(it.fileDescriptor).use {
                    it.write(jsonString.toByteArray())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
