package com.example.listviewapp

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class StopwatchFragment : Fragment() {

    private lateinit var stopwatchTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button

    private var isRunning = false
    private var elapsedTime: Long = 0
    private var startTime: Long = 0

    private val handler = Handler()
    private val updateTimeTask = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            elapsedTime = currentTime - startTime
            updateTimerText(elapsedTime)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stopwatch, container, false)

        stopwatchTextView = view.findViewById(R.id.stopwatch_text_view)
        startButton = view.findViewById(R.id.start_button)
        stopButton = view.findViewById(R.id.stop_button)
        resetButton = view.findViewById(R.id.reset_button)

        startButton.setOnClickListener { startStopwatch() }
        stopButton.setOnClickListener { stopStopwatch() }
        resetButton.setOnClickListener { resetStopwatch() }

        return view
    }

    private fun startStopwatch() {
        if (!isRunning) {
            startTime = SystemClock.elapsedRealtime() - elapsedTime
            handler.post(updateTimeTask)
            isRunning = true
        }
    }

    private fun stopStopwatch() {
        if (isRunning) {
            handler.removeCallbacks(updateTimeTask)
            isRunning = false
        }
    }

    private fun resetStopwatch() {
        if (!isRunning) {
            elapsedTime = 0
            updateTimerText(elapsedTime)
        }
    }

    private fun updateTimerText(elapsedTime: Long) {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = elapsedTime / (1000 * 60 * 60)
        stopwatchTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
