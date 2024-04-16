package com.ciaorides.ciaorides.view.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.BuildConfig
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ItemRideRequestsBinding
import com.ciaorides.ciaorides.databinding.ItemRidersBinding
import com.ciaorides.ciaorides.model.request.RideRequestResponse
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.model.response.SharingAvailabilityResponse
import com.ciaorides.ciaorides.utils.Constants
import javax.inject.Inject

class RideRequestAdapter @Inject constructor() :
    RecyclerView.Adapter<RideRequestAdapter.ViewHolder>() {

    private val diffUtil =
        object : DiffUtil.ItemCallback<RideRequestResponse>() {
            override fun areItemsTheSame(
                oldItem: RideRequestResponse,
                newItem: RideRequestResponse
            ): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: RideRequestResponse,
                newItem: RideRequestResponse
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemRideRequestsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRideRequestsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = differ.currentList[position]
        holder.binding.apply {
            tvName.text = driver.name
            tvMessage.text = driver.message
            tvDistance.text = driver.distance
            tvPrice.text = driver.price

        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}