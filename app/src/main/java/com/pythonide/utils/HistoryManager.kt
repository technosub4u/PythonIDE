package com.pythonide.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("PythonIDEHistory", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val historyKey = "code_history"
    private val maxHistory = 10

    fun addToHistory(code: String) {
        val history = getHistory().toMutableList()
        history.add(0, code)
        if (history.size > maxHistory) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    fun getHistory(): List<String> {
        val json = prefs.getString(historyKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun saveHistory(history: List<String>) {
        val json = gson.toJson(history)
        prefs.edit().putString(historyKey, json).apply()
    }
}
