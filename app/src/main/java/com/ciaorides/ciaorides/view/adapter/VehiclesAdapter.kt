package com.ciaorides.ciaorides.view.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ItemVehiclesBinding
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.InfoPopUpDialog
import javax.inject.Inject

class VehiclesAdapter @Inject constructor() :
    RecyclerView.Adapter<VehiclesAdapter.ViewHolder>() {
    var selectedPosition = -1;

    private val diffUtil =
        object : DiffUtil.ItemCallback<VehicleInfoResponse.Response.Car>() {
            override fun areItemsTheSame(
                oldItem: VehicleInfoResponse.Response.Car,
                newItem: VehicleInfoResponse.Response.Car
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: VehicleInfoResponse.Response.Car,
                newItem: VehicleInfoResponse.Response.Car
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemVehiclesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemVehiclesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = differ.currentList[position]
        holder.binding.apply {
            tvVehicleSub.text = vehicle.sub_vehicle_type
            tvMaxSize.text = "Max No. (" + vehicle.max_seat_capacity + ")"
        }
        if (selectedPosition == position) {
            holder.binding.cardSelected.strokeColor = ContextCompat.getColor(
                holder.binding.cardSelected.context,
                R.color.appBlue
            )
        } else {
            holder.binding.cardSelected.strokeColor =
                ContextCompat.getColor(
                    holder.binding.cardSelected.context,
                    R.color.white
                )
        }

        holder.binding.ivCar.setOnClickListener {
            selectedPosition = position
            Log.d("INFO", "" + position)
            notifyDataSetChanged()
            setVehicleClickListener?.let {
                it(vehicle)
            }
        }
        holder.binding.ivInfo.setOnClickListener {
            setInfoClickCallBack?.let {
                it(vehicle)
            }
        }

        holder.binding.ivInfo.setOnClickListener {
            setInfoClickCallBack?.let {
                it(vehicle)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setVehicleClickListener: ((user: VehicleInfoResponse.Response.Car) -> Unit)? =
        null

    fun selectedVehicle(listener: (VehicleInfoResponse.Response.Car) -> Unit) {
        setVehicleClickListener = listener
    }

    private var setInfoClickCallBack: ((user: VehicleInfoResponse.Response.Car) -> Unit)? =
        null

    fun infoClickCallBack(listener: (VehicleInfoResponse.Response.Car) -> Unit) {
        setInfoClickCallBack = listener
    }
}