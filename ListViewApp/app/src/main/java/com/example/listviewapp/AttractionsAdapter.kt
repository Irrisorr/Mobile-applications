// AttractionsAdapter.kt
package com.example.listviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AttractionsAdapter(private val attractions: List<Attraction>) :
    RecyclerView.Adapter<AttractionsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.name_text_view)
        val imageView: ImageView = view.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attraction_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.nameTextView.text = attraction.name
        holder.imageView.setImageResource(attraction.imageResId)

        holder.view.setOnClickListener {
            val context = holder.view.context
            val fragment = AttractionDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("attraction", attraction)
                }
            }

            if (context is MainActivity && context.findViewById<View>(R.id.details_fragment_container) != null) {
                context.supportFragmentManager.beginTransaction()
                    .replace(R.id.details_fragment_container, fragment)
                    .commit()
            } else {
                (context as? MainActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragment_container, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    override fun getItemCount() = attractions.size
}
