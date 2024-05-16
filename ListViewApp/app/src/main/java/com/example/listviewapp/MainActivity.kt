// MainActivity.kt
package com.example.listviewapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
}
