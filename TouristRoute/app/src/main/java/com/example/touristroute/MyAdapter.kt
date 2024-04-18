package com.example.touristroute

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MyAdapter(private val context: Activity, private val arrayList: ArrayList<User>) : ArrayAdapter<User>(
    context,
    R.layout.list_item,
    arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.list_item, null)

        val image : ImageView = view.findViewById(R.id.route_pic)
        val title : TextView = view.findViewById(R.id.route_title)
        val description : TextView = view.findViewById(R.id.route_description)
        val time : TextView = view.findViewById(R.id.route_time)

        image.setImageResource(arrayList[position].imageId)
        title.text = arrayList[position].title
        description.text = arrayList[position].description
        time.text = arrayList[position].time


        return view
    }

}