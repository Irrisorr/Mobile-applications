package com.example.touristroute

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.touristroute.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val country = intent.getStringExtra("country") ?: ""
        val imageId = intent.getIntExtra("imageId", R.drawable.icon1)

        binding.titleProfile.text = title
        binding.phone.text = phone
        binding.country.text = country
        binding.routeImg.setImageResource(imageId)

    }

}