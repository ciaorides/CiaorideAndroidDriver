package com.ciaorides.ciaorides.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R

class VehicleImagesAdapter(private val data: ArrayList<Uri>) :
    RecyclerView.Adapter<VehicleImagesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleImagesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_item_design, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImgVehicle)
//        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onBindViewHolder(holder: VehicleImagesAdapter.ViewHolder, position: Int) {
        holder.imageView.setImageURI(data.get(position))
    }

    override fun getItemCount(): Int {
        return data.size
    }

}