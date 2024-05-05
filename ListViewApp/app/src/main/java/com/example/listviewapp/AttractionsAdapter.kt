package com.example.listviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AttractionsAdapter(private val attractions: List<Attraction>) :
    RecyclerView.Adapter<AttractionsAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.name_text_view)
        val descriptionTextView: TextView = view.findViewById(R.id.description_text_view)
        val imageView: ImageView = view.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.attraction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.nameTextView.text = attraction.name
        holder.descriptionTextView.text = attraction.description
        holder.imageView.setImageResource(attraction.imageResId)

        // Обработчик нажатия на элемент списка
        holder.view.setOnClickListener {
            val fragment = AttractionDetailsFragment()
            val bundle = Bundle().apply {
                putParcelable("attraction", attraction)
            }
            fragment.arguments = bundle

            // Получаем ссылку на фрагментный менеджер и открываем новый фрагмент
            (holder.view.context as? MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun getItemCount() = attractions.size
}