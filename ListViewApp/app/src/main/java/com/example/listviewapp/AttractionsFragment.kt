package com.example.listviewapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AttractionsFragment : Fragment() {

    private lateinit var attractionsAdapter: AttractionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_attractions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val attractions = listOf(
            Attraction(
                "Szlak Orlich Gniazd",
                "Znany szlak turystyczny w południowej Polsce, prowadzący wzdłuż zamków i ruin",
                R.drawable.orlich_gniazd
            ),
            Attraction(
                "Tatrzański Park Narodowy",
                "Największy park narodowy w Polsce, słynący z pięknych gór i krajobrazu",
                R.drawable.park_narodowy
            ),
            Attraction(
                "Szlak Pięciu Stawów Polskich",
                "Popularny szlak w Tatrach Wysokich, prowadzący do malowniczych stawów",
                R.drawable.piec_stawow
            ),
            Attraction(
                "Szlak Bieszczadzki",
                "Dziki i malowniczy szlak w Bieszczadach, przez lasy i góry",
                R.drawable.bieszczady
            ),
            Attraction(
                "Szlak Kaszubski",
                "Szlak turystyczny w regionie Kaszub, przez lasy i jeziorа",
                R.drawable.kaszuby
            ),
            Attraction(
                "Szlak Małopolska Droga św. Jakuba",
                "Część europejskiego szlaku pielgrzymkowego św. Jakuba",
                R.drawable.droga_sw_jakuba
            )
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.attractions_list)
        attractionsAdapter = AttractionsAdapter(attractions)
        recyclerView.adapter = attractionsAdapter

        recyclerView.layoutManager = LinearLayoutManager(view.context)
    }
}