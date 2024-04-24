package com.example.fragments_lab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.fragments_lab.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var firstFragment = FirstFragment()
        var secondFragment = SecondFragment()

        supportFragmentManager.commit {
            add(R.id.Fragment1, firstFragment)
            add(R.id.Fragment2, secondFragment)
        }

    }
}