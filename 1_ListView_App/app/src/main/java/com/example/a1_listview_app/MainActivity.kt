package com.example.a1_listview_app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.a1_listview_app.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var random_num = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            binding.textView.text = random_num.toString()
            val random_num = savedInstanceState?.getInt("RAND")
            if (random_num != null) {
                this.random_num = random_num
            }
        }

        binding.button.setOnClickListener {
            random_num = Random.nextInt(0, 10)
            binding.textView.text = random_num.toString()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("RAND", random_num)
    }
}