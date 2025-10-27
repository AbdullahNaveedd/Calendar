package com.kashifbhai.customcalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kashifbhai.customcalendar.ImageItem
import com.kashifbhai.customcalendar.R

class ImageAdapter(private val items: List<ImageItem>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    var isLockedVisible = false

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.itemImage)
        val title: TextView = view.findViewById(R.id.itemTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.image.setImageResource(item.imageRes)
        holder.title.visibility = if (isLockedVisible) View.VISIBLE else View.GONE

    }

    override fun getItemCount() = items.size

    fun updateLockedVisibility(visible: Boolean) {
        isLockedVisible = visible
        notifyDataSetChanged()
    }
}
