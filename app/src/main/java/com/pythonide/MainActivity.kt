package com.pythonide

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.card.MaterialCardView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private var isDarkMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<MaterialCardView>(R.id.interpreterCard).setOnClickListener {
            startActivity(Intent(this, InterpreterActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.scriptCard).setOnClickListener {
            startActivity(Intent(this, ScriptActivity::class.java))
        }

        // Initialize theme based on saved preference
        isDarkMode = getPreferences(MODE_PRIVATE).getBoolean("dark_mode", true)
        updateTheme()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                toggleTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleTheme() {
        isDarkMode = !isDarkMode
        updateTheme()
        // Save preference
        getPreferences(MODE_PRIVATE).edit().putBoolean("dark_mode", isDarkMode).apply()
    }

    private fun updateTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}