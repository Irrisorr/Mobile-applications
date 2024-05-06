package com.example.listviewapp

import androidx.lifecycle.ViewModel

class StopwatchViewModel : ViewModel() {
    var startTime: Long = 0
    var elapsedTime: Long = 0
    var isRunning: Boolean = false
}
