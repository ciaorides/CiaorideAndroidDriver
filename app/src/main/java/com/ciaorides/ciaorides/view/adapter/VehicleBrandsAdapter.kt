package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.model.response.VehicleBrandsResponse

class VehicleBrandsAdapter(private val data: VehicleBrandsResponse) :
    RecyclerView.Adapter<VehicleBrandsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VehicleBrandsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vehicle_item_brand, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemVehicle: TextView = itemView.findViewById(R.id.itemVehicle)
//        val textView: TextView = itemView.findViewById(R.id.textView)
    }

    override fun onBindViewHolder(holder: VehicleBrandsAdapter.ViewHolder, position: Int) {
        holder.itemVehicle.text = (data.response.get(position).title)

        holder.itemVehicle.setOnClickListener {
            setClickListener?.let {
                it(data.response.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.response.size
    }

    private var setClickListener: ((user: VehicleBrandsResponse.Response) -> Unit)? =
        null

    fun onItemClicked(listener: (VehicleBrandsResponse.Response) -> Unit) {
        setClickListener = listener
    }
}