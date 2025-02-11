
package com.pythonide.utils

interface PythonConsole {
    fun execute(code: String, isScriptMode: Boolean = false): Any?
    fun addToHistory(code: String)
    fun getHistory(): List<String>
}
