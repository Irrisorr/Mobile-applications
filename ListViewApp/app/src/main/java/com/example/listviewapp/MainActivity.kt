package com.example.listviewapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Тема
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("dark_theme", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Используем главный layout

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            handleBottomNavigationSelection(item.itemId)
        }

        // Проверяем наличие контейнера для detail фрагмента
        if (findViewById<View>(R.id.details_fragment_container) != null) {
            // Если контейнер есть (планшет), показываем PlaceholderFragment в нем
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment_container, PlaceholderFragment())
                .commit()
        }

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }


    private fun handleBottomNavigationSelection(itemId: Int): Boolean {
        return when (itemId) {
            R.id.navigation_home -> {
                loadFragment(HomeFragment())
                true
            }
            R.id.navigation_easy -> {
                loadFragment(AttractionsFragment.newInstance("easy"))
                true
            }
            R.id.navigation_hard -> {
                loadFragment(AttractionsFragment.newInstance("hard"))
                true
            }
            else -> false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val containerId = if (findViewById<View>(R.id.details_fragment_container) != null) {
            R.id.list_fragment_container
        } else {
            R.id.fragment_container
        }
        fragmentTransaction.replace(containerId, fragment)

        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_switch_theme -> {
                switchTheme()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchTheme() {
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
    }
}