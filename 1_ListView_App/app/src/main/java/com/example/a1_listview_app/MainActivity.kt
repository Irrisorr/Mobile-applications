package com.example.a1_listview_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a1_listview_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            Toast.makeText(applicationContext, "Pressed button", Toast.LENGTH_SHORT).show()
        }

        binding.button.setOnLongClickListener{
            Toast.makeText(applicationContext, "Pressed long", Toast.LENGTH_SHORT).show()
            true
        }
    }
}