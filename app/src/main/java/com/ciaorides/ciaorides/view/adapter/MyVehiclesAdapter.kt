package com.ciaorides.ciaorides.view.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemMyVehicleBinding
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.utils.visible
import javax.inject.Inject

class MyVehiclesAdapter @Inject constructor() :
    RecyclerView.Adapter<MyVehiclesAdapter.ViewHolder>() {
    private val diffUtil =
        object : DiffUtil.ItemCallback<MyVehicleResponse.Response>() {
            override fun areItemsTheSame(
                oldItem: MyVehicleResponse.Response,
                newItem: MyVehicleResponse.Response
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MyVehicleResponse.Response,
                newItem: MyVehicleResponse.Response
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemMyVehicleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMyVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = differ.currentList[position]
        with(holder.binding) {

            tvVehicleName.text = vehicle.vehicle_type
            tvVehicleNumber.text = vehicle.number_plate
            tvVehicleType.text = vehicle.color
            if (vehicle.vehicle_step1 == "yes" && vehicle.vehicle_step2 == "yes" && vehicle.vehicle_step3 == "yes") {
                btnEdit.visibility = View.GONE
            } else btnEdit.visibility = View.VISIBLE
            btnDelete.setOnClickListener {
                setDeleteClickListener?.invoke(vehicle)
            }
            btnEdit.setOnClickListener {
                setClickListener?.invoke(vehicle)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: MyVehicleResponse.Response) -> Unit)? =
        null
    private var setDeleteClickListener: ((user: MyVehicleResponse.Response) -> Unit)? =
        null

    fun onDeleteClicked(listener: (MyVehicleResponse.Response) -> Unit) {
        setDeleteClickListener = listener
    }

    fun onUpdateClick(listener: (MyVehicleResponse.Response) -> Unit) {
        setClickListener = listener
    }
}