package com.ciaorides.ciaorides.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ItemBankDetailsBinding
import com.ciaorides.ciaorides.databinding.ItemMyRideBinding
import com.ciaorides.ciaorides.databinding.ItemMyVehicleBinding
import com.ciaorides.ciaorides.databinding.ItemVehiclesBinding
import com.ciaorides.ciaorides.model.response.BankDetailsResponse
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import javax.inject.Inject

class BankDetailsAdapter @Inject constructor() :
    RecyclerView.Adapter<BankDetailsAdapter.ViewHolder>() {
    private val diffUtil =
        object : DiffUtil.ItemCallback<BankDetailsResponse.Response>() {
            override fun areItemsTheSame(
                oldItem: BankDetailsResponse.Response,
                newItem: BankDetailsResponse.Response
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: BankDetailsResponse.Response,
                newItem: BankDetailsResponse.Response
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemBankDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemBankDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vehicle = differ.currentList[position]
        with(holder.binding) {
            tvBankName.text = vehicle.bank_name
            tvAccountNumber.text = vehicle.account_number
            btnDelete.setOnClickListener {
                setClickListener?.invoke(vehicle)
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setClickListener: ((user: BankDetailsResponse.Response) -> Unit)? =
        null

    fun onDeleteClicked(listener: (BankDetailsResponse.Response) -> Unit) {
        setClickListener = listener
    }
}