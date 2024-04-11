package com.example.a1_listview_app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.a1_listview_app.databinding.ActivityMainBinding
import kotlin.random.Random

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val arrayAdapter: ArrayAdapter<*>
        val languages = arrayOf(
            "Python", "Java", "C#","C++"
        )
        //deklaruje zmienną niemutowalną o nazwie arrayAdapter o typie
        var mListView = binding.listview

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        mListView.adapter = arrayAdapter
    }
}