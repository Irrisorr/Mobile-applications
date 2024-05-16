package com.example.listviewapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AttractionsFragment : Fragment() {

    private lateinit var attractionsAdapter: AttractionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
                R.drawable.orlich_gniazd,
                listOf(
                    "Kraków – Zamek Pilcza 92 km.",
                    "Zamek Pilcza – Ogrodzieniec 22 km.",
                    "Ogrodzieniec – Zrębice 56 km.",
                    "Zrębice – Częstochowa 26 km."
                )
            ),
            Attraction(
                "Tatrzański Park Narodowy",
                "Największy park narodowy w Polsce, słynący z pięknych gór i krajobrazu",
                R.drawable.park_narodowy,
                listOf(
                    "Niewielkie rozproszone osady",
                    "Wysokie szczyty górskie",
                    "Bogata flora i fauna"
                )
            ),
            Attraction(
                "Szlak Pięciu Stawów Polskich",
                "Popularny szlak w Tatrach Wysokich, prowadzący do malowniczych stawów",
                R.drawable.piec_stawow,
                listOf(
                    "Zakopane – Morskie Oko 9 km.",
                    "Morskie Oko – Dolina Pięciu Stawów Polskich 2 km.",
                    "Dolina Pięciu Stawów Polskich – Zakopane 9 km."
                )
            ),
            Attraction(
                "Szlak Bieszczadzki",
                "Dziki i malowniczy szlak w Bieszczadach, przez lasy i góry",
                R.drawable.bieszczady,
                listOf(
                    "Ustrzyki Dolne – Tarnica 9 km.",
                    "Tarnica – Wetlina 12 km.",
                    "Wetlina – Ustrzyki Dolne 13 km."
                )
            ),
            Attraction(
                "Szlak Kaszubski",
                "Szlak turystyczny w regionie Kaszub, przez lasy i jeziorа",
                R.drawable.kaszuby,
                listOf(
                    "Gdańsk – Wzgórza Szymbarskie 36 km.",
                    "Wzgórza Szymbarskie – Szymbark 18 km.",
                    "Szymbark – Gdańsk 36 km."
                )
            ),
            Attraction(
                "Szlak Małopolska Droga św. Jakuba",
                "Część europejskiego szlaku pielgrzymkowego św. Jakuba",
                R.drawable.droga_sw_jakuba,
                listOf(
                    "Kraków – Wadowice 50 km.",
                    "Wadowice – Maków Podhalański 23 km.",
                    "Maków Podhalański – Kalwaria Zebrzydowska 10 km.",
                    "Kalwaria Zebrzydowska – Kraków 29 km."
                )
            )
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.attractions_list)
        attractionsAdapter = AttractionsAdapter(attractions)
        recyclerView.adapter = attractionsAdapter

        recyclerView.layoutManager = GridLayoutManager(view.context, 2)
    }
}