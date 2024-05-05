package com.example.listviewapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class AttractionDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_attraction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем переданные данные о достопримечательности
        val attraction = arguments?.getParcelable<Attraction>("attraction")

        if (attraction != null) {
            // Находим элементы в разметке
            val titleTextView: TextView = view.findViewById(R.id.title_text_view)
            val descriptionTextView: TextView = view.findViewById(R.id.description_text_view)
            val imageView: ImageView = view.findViewById(R.id.image_view)

            // Устанавливаем данные о достопримечательности
            titleTextView.text = attraction.name
            descriptionTextView.text = attraction.description
            imageView.setImageResource(attraction.imageResId)
        }
    }
}