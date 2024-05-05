package com.example.listviewapp

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AttractionDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val orientation = resources.configuration.orientation
        val layoutResId = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            R.layout.fragment_attraction_details_land
        } else {
            R.layout.fragment_attraction_details
        }
        return inflater.inflate(layoutResId, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attraction = arguments?.getParcelable<Attraction>("attraction")

        if (attraction != null) {
            val titleTextView: TextView = view.findViewById(R.id.title_text_view)
            val descriptionTextView: TextView = view.findViewById(R.id.description_text_view)
            val imageView: ImageView = view.findViewById(R.id.image_view)
            val stagesRecyclerView: RecyclerView = view.findViewById(R.id.stages_recycler_view)

            titleTextView.text = attraction.name
            descriptionTextView.text = attraction.description
            imageView.setImageResource(attraction.imageResId)

            stagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            stagesRecyclerView.adapter = StagesAdapter(attraction.stages)
        }
    }
}