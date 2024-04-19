package com.example.listviewapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.listviewapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var firstFragment = FirstFragment()
        var secondFragment = SecondFragment()


        supportFragmentManager.commit {
            add(R.id.Fragment1, firstFragment)
            add(R.id.Fragment2, secondFragment)
        }

    }
}