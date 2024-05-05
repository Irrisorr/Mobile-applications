package com.example.listviewapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            val currentFragment = supportFragmentManager.getFragment(savedInstanceState, "currentFragment")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, currentFragment!!)
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, AttractionsFragment())
                .commit()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        supportFragmentManager.putFragment(outState, "currentFragment", supportFragmentManager.findFragmentById(R.id.fragment_container)!!)
    }

}