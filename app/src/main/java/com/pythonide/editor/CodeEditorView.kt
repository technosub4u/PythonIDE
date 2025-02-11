
package com.pythonide.editor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import android.view.KeyEvent
import android.text.SpannableStringBuilder

class CodeEditorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    var autoIndent: Boolean = true
    private var isUpdating = false
    private var currentLanguage: String = "text"
    private var tabLength: Int = 4
    private var highlightCurrentLine: Boolean = false

    init {
        setHorizontallyScrolling(true)
        setTextSize(14f)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!isUpdating && s is Editable) {
                    isUpdating = true
                    SyntaxHighlighter.highlight(s)
                    isUpdating = false
                }
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER && autoIndent) {
            val currentLine = getCurrentLine()
            val indentation = getIndentation(currentLine)

            if (currentLine.trimEnd().endsWith(":")) {
                // Add extra indent for new block
                post {
                    append("\n" + indentation + "    ")
                }
                return true
            }

            post {
                append("\n" + indentation)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getCurrentLine(): String {
        val text = text.toString()
        val start = text.lastIndexOf('\n', selectionStart - 1) + 1
        val end = text.indexOf('\n', selectionStart).let { if (it == -1) text.length else it }
        return text.substring(start, end)
    }

    private fun getIndentation(line: String): String {
        val matcher = Regex("^[ \\t]+").find(line)
        return matcher?.value ?: ""
    }

    fun setLineBackground(lineNumber: Int, color: Int) {
        val text = text.toString()
        val lines = text.split("\n")
        if (lineNumber >= 0 && lineNumber < lines.size) {
            val startPos = text.take(lineNumber).count { it == '\n' }
            val endPos = text.indexOf('\n', startPos + 1).let { if (it == -1) text.length else it }
            setBackgroundColor(color)
        }
    }

    fun setLanguage(lang: String) {
        currentLanguage = lang.toLowerCase()
        // Trigger syntax highlighting refresh
        text = text
    }

    fun setTabLength(length: Int) {
        tabLength = length
    }

    fun setHighlightCurrentLine(highlight: Boolean) {
        highlightCurrentLine = highlight
    }
}
