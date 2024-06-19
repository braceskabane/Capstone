package com.dicoding.picodiploma.loginwithanimation.view.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R

class LabelAdapter(private val locations: List<String>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<LabelAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.locationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_label, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        holder.button.text = location
        holder.button.setOnClickListener { onClick(location) }
    }

    override fun getItemCount(): Int = locations.size
}
