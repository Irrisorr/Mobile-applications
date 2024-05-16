package com.example.listviewapp

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class StopwatchFragment : Fragment() {

    private lateinit var stopwatchTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button
    private lateinit var saveButton: Button
    private lateinit var scrollView: ScrollView
    private lateinit var savedTimesLayout: LinearLayout

    private lateinit var viewModel: StopwatchViewModel

    private val handler = Handler()
    private val updateTimeTask = object : Runnable {
        override fun run() {
            val currentTime = SystemClock.elapsedRealtime()
            viewModel.elapsedTime = currentTime - viewModel.startTime
            updateTimerText(viewModel.elapsedTime)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stopwatch, container, false)

        stopwatchTextView = view.findViewById(R.id.stopwatch_text_view)
        startButton = view.findViewById(R.id.start_button)
        stopButton = view.findViewById(R.id.stop_button)
        resetButton = view.findViewById(R.id.reset_button)
        saveButton = view.findViewById(R.id.save_button)
        scrollView = view.findViewById(R.id.saved_times_scroll_view)
        savedTimesLayout = view.findViewById(R.id.saved_times_layout)

        viewModel = ViewModelProvider(this).get(StopwatchViewModel::class.java)

        startButton.setOnClickListener { startStopwatch() }
        stopButton.setOnClickListener { stopStopwatch() }
        resetButton.setOnClickListener { resetStopwatch() }
        saveButton.setOnClickListener { saveTime() }

        if (viewModel.isRunning) {
            handler.post(updateTimeTask)
        }
        updateTimerText(viewModel.elapsedTime)

        return view
    }

    override fun onPause() {
        super.onPause()
        if (viewModel.isRunning) {
            handler.removeCallbacks(updateTimeTask)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isRunning) {
            handler.post(updateTimeTask)
        }
    }

    private fun startStopwatch() {
        if (!viewModel.isRunning) {
            viewModel.startTime = SystemClock.elapsedRealtime() - viewModel.elapsedTime
            handler.post(updateTimeTask)
            viewModel.isRunning = true
        }
    }

    private fun stopStopwatch() {
        if (viewModel.isRunning) {
            handler.removeCallbacks(updateTimeTask)
            viewModel.isRunning = false
        }
    }

    private fun resetStopwatch() {
        if (!viewModel.isRunning) {
            viewModel.elapsedTime = 0
            updateTimerText(viewModel.elapsedTime)
        }
    }

    private fun saveTime() {
        viewModel.savedTimes.add(0, viewModel.elapsedTime)
        updateSavedTimeText()
    }

    private fun updateTimerText(elapsedTime: Long) {
        val seconds = (elapsedTime / 1000) % 60
        val minutes = (elapsedTime / (1000 * 60)) % 60
        val hours = elapsedTime / (1000 * 60 * 60)
        stopwatchTextView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun updateSavedTimeText() {
        savedTimesLayout.removeAllViews()
        viewModel.savedTimes.forEachIndexed { index, time ->
            val seconds = (time / 1000) % 60
            val minutes = (time / (1000 * 60)) % 60
            val hours = time / (1000 * 60 * 60)
            val savedTimeTextView = TextView(requireContext()).apply {
                text = "Saved time ${viewModel.savedTimes.size - index}: ${String.format("%02d:%02d:%02d", hours, minutes, seconds)}"
                textSize = 18f
            }
            savedTimesLayout.addView(savedTimeTextView)
        }
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }
}
