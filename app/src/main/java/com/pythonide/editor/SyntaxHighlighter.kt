
package com.pythonide.editor

import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.graphics.Color

object SyntaxHighlighter {
    private val KEYWORDS = setOf(
        "and", "as", "assert", "async", "await", "break", "class", "continue", 
        "def", "del", "elif", "else", "except", "finally", "for", "from", 
        "global", "if", "import", "in", "is", "lambda", "None", "nonlocal", 
        "not", "or", "pass", "raise", "return", "try", "while", "with", "yield"
    )

    fun highlight(text: Spannable) {
        // Remove existing spans
        val spans = text.getSpans(0, text.length, ForegroundColorSpan::class.java)
        spans.forEach { text.removeSpan(it) }

        // Highlight keywords
        val content = text.toString()
        val words = content.split(Regex("\\b"))
        
        var position = 0
        words.forEach { word ->
            if (KEYWORDS.contains(word)) {
                text.setSpan(
                    ForegroundColorSpan(Color.parseColor("#FF9D00")),
                    position,
                    position + word.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else if (word.matches(Regex("\".*\""))) {
                // String literals
                text.setSpan(
                    ForegroundColorSpan(Color.parseColor("#98C379")),
                    position,
                    position + word.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            position += word.length
        }
    }
}
