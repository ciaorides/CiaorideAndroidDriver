package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.databinding.ItemPaymentsBinding
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.PaymentsResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import javax.inject.Inject

class PaymentsAdapter @Inject constructor(private val listener : OnItemClickListener) :
    RecyclerView.Adapter<PaymentsAdapter.ViewHolder>() {
    private val diffUtil =
        object : DiffUtil.ItemCallback<PaymentsResponse.BookingData>() {
            override fun areItemsTheSame(
                oldItem: PaymentsResponse.BookingData,
                newItem: PaymentsResponse.BookingData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PaymentsResponse.BookingData,
                newItem: PaymentsResponse.BookingData
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    interface OnItemClickListener {
        fun onItemClick(position: Int, ridesTaken: PaymentsResponse.BookingData)
    }

    val onItemClickListener : OnItemClickListener = listener

    inner class ViewHolder(val binding: ItemPaymentsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemPaymentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ride = differ.currentList[position]
        with(holder.binding) {
            ietmPaymentBookingId.text = "Booking ID : ${ride.booking_id}"
            ietmPaymentAmount.text = "₹ " + ride.Final_Amount
            ietmPaymentTime.text = "${ride.Booking_Date} ${ride.Booking_Time}"
            holder.itemView.setOnClickListener {
                onItemClickListener.onItemClick(position,ride)
            }
        }
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