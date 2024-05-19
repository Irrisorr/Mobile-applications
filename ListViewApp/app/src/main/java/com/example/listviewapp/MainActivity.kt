package com.example.listviewapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            if (findViewById<View>(R.id.details_fragment_container) != null) {
                supportFragmentManager.beginTransaction()
                    .add(R.id.list_fragment_container, AttractionsFragment())
                    .add(R.id.details_fragment_container, PlaceholderFragment()) // Пустой фрагмент для деталей
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, AttractionsFragment())
                    .commit()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_theme -> {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val isDarkTheme = sharedPreferences.getBoolean("dark_theme", false)
                val editor = sharedPreferences.edit()
                if (isDarkTheme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editor.putBoolean("dark_theme", false)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editor.putBoolean("dark_theme", true)
                }
                editor.apply()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
