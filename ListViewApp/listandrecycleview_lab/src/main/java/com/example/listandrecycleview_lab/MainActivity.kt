package com.example.listandrecycleview_lab

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listandrecycleview_lab.databinding.ActivityMainBinding

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