package com.ciaorides.ciaorides.view.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.model.response.VehicleBrandsResponse
import com.ciaorides.ciaorides.model.response.VehicleModelsResponse

class VehicleModelsAdapter(private val data: VehicleModelsResponse) :
    RecyclerView.Adapter<VehicleModelsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleModelsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_item_brand, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemVehicle: TextView = itemView.findViewById(R.id.itemVehicle)
//        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onBindViewHolder(holder: VehicleModelsAdapter.ViewHolder, position: Int) {
        holder.itemVehicle.text = (data.response.get(position).title)

        holder.itemVehicle.setOnClickListener {
            setClickListener?.let {
                it(data.response.get(position))
            }
        }
    }
    private var setClickListener: ((user: VehicleModelsResponse.Response) -> Unit)? =
        null

    fun onItemClicked(listener: (VehicleModelsResponse.Response) -> Unit) {
        setClickListener = listener
    }
    override fun getItemCount(): Int {
        return data.response.size
    }

}