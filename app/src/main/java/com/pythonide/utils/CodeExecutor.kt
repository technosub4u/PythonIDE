
package com.pythonide.utils

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class CodeExecutor(private val context: Context) {
    private lateinit var pythonConsole: PythonConsole

    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
        }
        setupPythonConsole()
    }

    private fun setupPythonConsole() {
        val py = Python.getInstance()
        val consoleModule = py.getModule("chaquopy.utils.console")
        pythonConsole = object : PythonConsole {
            private val pyConsole = consoleModule.callAttr("PythonConsole")

            override fun execute(code: String, isScriptMode: Boolean): Any? {
                return pyConsole.callAttr("execute", code, isScriptMode)?.toString()
            }

            override fun addToHistory(code: String) {
                pyConsole.callAttr("add_to_history", code)
            }

            override fun getHistory(): List<String> {
                return pyConsole.callAttr("get_history")?.asList()?.map { it.toString() } ?: emptyList()
            }
        }
    }

    data class ExecutionResult(
        val output: String,
        val errorLine: Int = -1
    )

    fun executeCode(code: String, isScriptMode: Boolean = false): ExecutionResult {
        return try {
            val result = pythonConsole.execute(code, isScriptMode)
            if (!isScriptMode) {
                pythonConsole.addToHistory(code)
            }
            ExecutionResult(result?.toString() ?: "Code executed successfully")
        } catch (e: Exception) {
            val errorMessage = e.message ?: ""
            val lineNumber = errorMessage.substringAfter("line ")
                .substringBefore(":")
                .toIntOrNull() ?: -1
            val cleanError = errorMessage.substringAfter("Error: ")
                .substringBefore("\n")
            ExecutionResult("Error at line $lineNumber: $cleanError", lineNumber)
        }
    }

    fun getHistory(): List<String> {
        return pythonConsole.getHistory()
    }
}
