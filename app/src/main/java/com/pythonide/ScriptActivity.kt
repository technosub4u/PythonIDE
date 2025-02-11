package com.pythonide

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.pythonide.utils.CodeExecutor
import com.pythonide.utils.HistoryManager
import org.w3c.dom.Element
import android.webkit.WebView
import android.webkit.WebViewClient

class ScriptActivity : AppCompatActivity() {
    private lateinit var codeInput: com.pythonide.editor.CodeEditorView
    private lateinit var outputText: TextView
    private lateinit var runButton: MaterialButton
    private lateinit var codeExecutor: CodeExecutor
    private lateinit var historyManager: HistoryManager
    private var autoIndent = true
    private var autoSave = true
    private var isDarkMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_script)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        codeInput = findViewById(R.id.codeInput)
        outputText = findViewById(R.id.outputText)
        runButton = findViewById(R.id.runButton)

        codeExecutor = CodeExecutor(this)
        historyManager = HistoryManager(this)

        setupCodeEditor()
        setupListeners()
    }

    private fun setupCodeEditor() {
        codeInput.setLanguage("python")
        codeInput.setTextColor(getColor(R.color.cyberpunk_text))
        codeInput.setTabLength(4)
        codeInput.setHighlightCurrentLine(true)
    }

    private fun setupListeners() {
        runButton.setOnClickListener {
            val code = codeInput.text.toString()
            val result = codeExecutor.executeCode(code, isScriptMode = true)
            outputText.text = result.output
            historyManager.addToHistory(code)

            if (result.errorLine > 0) {
                codeInput.setLineBackground(result.errorLine - 1, 0x1AFF0000)
            } else {
                // Clear all line backgrounds
                for (i in 0 until codeInput.lineCount) {
                    codeInput.setLineBackground(i, 0x00000000)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.script_menu, menu)
        menu.findItem(R.id.action_auto_indent).isChecked = autoIndent
        menu.findItem(R.id.action_auto_save).isChecked = autoSave
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_new -> {
                codeInput.setText("")
                outputText.text = ""
                true
            }
            R.id.action_history -> {
                showHistory()
                true
            }
            R.id.action_theme -> {
                toggleTheme()
                true
            }
            R.id.action_auto_indent -> {
                autoIndent = !autoIndent
                item.isChecked = autoIndent
                codeInput.autoIndent = autoIndent
                true
            }
            R.id.action_auto_save -> {
                autoSave = !autoSave
                item.isChecked = autoSave
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        isDarkMode = !isDarkMode
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun showHistory() {
        val dialog = BottomSheetDialog(this)
        val history = historyManager.getHistory()

        val view = layoutInflater.inflate(R.layout.history_bottom_sheet, null)
        val historyList = view.findViewById<TextView>(R.id.historyList)

        historyList.text = history.mapIndexed { index, code ->
            "${index + 1}. $code\n"
        }.joinToString("\n")

        dialog.setContentView(view)
        dialog.show()
    }
}