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
        val view: View = convertView ?: inflater.inflate(R.layout.list_item, parent, false)

        val image : ImageView = view.findViewById(R.id.route_pic)
        val title : TextView = view.findViewById(R.id.route_title)
        val description : TextView = view.findViewById(R.id.route_description)
        val time : TextView = view.findViewById(R.id.route_time)

        if (!arrayList.isNullOrEmpty() && position < arrayList.size) {
            val user = arrayList[position]
            image.setImageResource(user.imageId)
            title.text = user.title
            description.text = user.description
            time.text = user.time
        }


        return view
    }

}