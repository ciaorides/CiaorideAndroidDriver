package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ItemMyRideBinding
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import javax.inject.Inject

class MyRidesAdapter @Inject constructor() :
    RecyclerView.Adapter<MyRidesAdapter.ViewHolder>() {
    var typeRide = 0
    private val diffUtil =
        object : DiffUtil.ItemCallback<MyRidesResponse.Response.RidesTaken>() {
            override fun areItemsTheSame(
                oldItem: MyRidesResponse.Response.RidesTaken,
                newItem: MyRidesResponse.Response.RidesTaken
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: MyRidesResponse.Response.RidesTaken,
                newItem: MyRidesResponse.Response.RidesTaken
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemMyRideBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMyRideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ride = differ.currentList[position]
        when (typeRide) {
            0 -> {
                handleRidsTaken(holder.binding, ride)
            }
            1 -> {
                handleRideOffered(holder.binding, ride)
            }
            else -> {
                handleRideScheduled(holder.binding, ride)
            }
        }
    }

    private fun handleRidsTaken(
        binding: ItemMyRideBinding,
        ride: MyRidesResponse.Response.RidesTaken
    ) {
        with(binding) {
//            tvName.text = ride.first_name
//               tvPrice.text = "Rs " + ride.total_amount
//            tvFromAddress.text = ride.from_address
//            tvToAddress.text = ride.to_address
         //   tvDate.text = ride.ride_time
        }
    }

    private fun handleRideOffered(
        binding: ItemMyRideBinding,
        ride: MyRidesResponse.Response.RidesTaken
    ) {
        with(binding) {
            // tvName.text = ride.first_name // add number of passengers
//             tvPrice.text = "Rs " + ride.total_amount
//            tvFromAddress.text = ride.from_address
//            tvToAddress.text = ride.to_address
//            tvDate.text = ride.ride_time
        }
    }

    private fun handleRideScheduled(
        binding: ItemMyRideBinding,
        ride: MyRidesResponse.Response.RidesTaken
    ) {
//        with(binding) {
//            tvName.text = ride.ride_time
//            tvRating.visibility = View.GONE
//            tvVehicleNumber.visibility = View.GONE
//            tvVehicleType.visibility = View.GONE
//            tvPrice.text = ride.ride_type
//            tvPaymentHeading.visibility=View.GONE
//            tvRating.visibility = View.GONE
//            ivRating.visibility = View.GONE
//            profileImage.visibility = View.GONE
//            if (ride.ride_type == "Taking") {
//                cardTime.setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        cardTime.context,
//                        R.color.colorBlue
//                    )
//                )
//                divider.visibility = View.GONE
//                tvDate.visibility = View.GONE
//            } else {
//                cardTime.setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        cardTime.context,
//                        R.color.colorPink
//                    )
//                )
//                divider.visibility = View.VISIBLE
//                tvDate.text = ride.ride_time
//                tvDate.visibility = View.VISIBLE
//            }
//            tvFromAddress.text = ride.from_address
//            tvToAddress.text = ride.to_address
//
//
//        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: RecentSearchesResponse.Response.UserLastData) -> Unit)? =
        null

    fun onClicked(listener: (RecentSearchesResponse.Response.UserLastData) -> Unit) {
        setClickListener = listener
    }
}