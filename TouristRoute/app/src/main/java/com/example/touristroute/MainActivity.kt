package com.example.touristroute

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.touristroute.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userArrayList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageId = intArrayOf(
            R.drawable.icon1,R.drawable.icon2,R.drawable.icon3,R.drawable.icon4
        )

        val title = arrayOf("1 test", "2 test", "3 test", "4 test")

        val description = arrayOf("1 descr", "2 descr", "3 descr", "4 descr")

        val time = arrayOf("1 time", "2 time", "3 time", "4 time")

        val phone = arrayOf("1 phone", "2 phone", "3 phone", "4 phone")

        val country = arrayOf("1 country", "2 country", "3 country", "4 country")

        userArrayList = arrayListOf<User>()

        for (i in title.indices){
            val user = User(title[i], description[i], time[i], phone[i], country[i], imageId[i])
            userArrayList.add(user)
        }

        binding.listview.isClickable = true
        binding.listview.adapter = MyAdapter(this, userArrayList)
        binding.listview.setOnItemClickListener { parent, view, position, id ->
            if (position < title.size && position < phone.size && position < country.size && position < imageId.size) {
                val titleValue = title[position]
                val phoneValue = phone[position]
                val countryValue = country[position]
                val imageIdValue = imageId[position]

                val intent = Intent(this, UserActivity::class.java)
                intent.putExtra("title", titleValue)
                intent.putExtra("phone", phoneValue)
                intent.putExtra("country", countryValue)
                intent.putExtra("imageId", imageIdValue)
                startActivity(intent)
            }
        }

    }
}