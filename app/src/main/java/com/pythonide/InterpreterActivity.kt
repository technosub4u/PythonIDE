package com.pythonide

import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.pythonide.utils.CodeExecutor

class InterpreterActivity : AppCompatActivity() {
    private lateinit var interpreterInput: EditText
    private lateinit var toolbar: MaterialToolbar
    private lateinit var codeExecutor: CodeExecutor
    private var currentPrompt = ">>> "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interpreter)

        interpreterInput = findViewById(R.id.interpreterInput)
        toolbar = findViewById(R.id.toolbar)
        codeExecutor = CodeExecutor(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        setupInterpreter()
    }

    private fun setupInterpreter() {
        interpreterInput.append(currentPrompt)

        interpreterInput.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val text = interpreterInput.text.toString()
                val lastLine = text.substringAfterLast('\n')
                if (lastLine.startsWith(currentPrompt)) {
                    val code = lastLine.substring(currentPrompt.length)
                    if (code.isNotBlank()) {
                        val result = codeExecutor.executeCode(code)
                        interpreterInput.append("\n$result")
                    }
                    interpreterInput.append("\n$currentPrompt")
                    return@setOnKeyListener true
                }
            }
            false
        }
    }
}