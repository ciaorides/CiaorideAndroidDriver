package com.ciaorides.ciaorides.view.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.BuildConfig
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ItemRidersBinding
import com.ciaorides.ciaorides.model.response.SharingAvailabilityResponse
import com.ciaorides.ciaorides.utils.Constants
import javax.inject.Inject

class RidesAvailabilityAdapter @Inject constructor() :
    RecyclerView.Adapter<RidesAvailabilityAdapter.ViewHolder>() {
    var selectedPosition = -1;

    private val diffUtil =
        object : DiffUtil.ItemCallback<SharingAvailabilityResponse.Response>() {
            override fun areItemsTheSame(
                oldItem: SharingAvailabilityResponse.Response,
                newItem: SharingAvailabilityResponse.Response
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SharingAvailabilityResponse.Response,
                newItem: SharingAvailabilityResponse.Response
            ): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)

    inner class ViewHolder(val binding: ItemRidersBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRidersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = differ.currentList[position]
        holder.binding.apply {
            tvName.text = driver.first_name
            tvVehicleNumber.text = driver.number_plate
            tvVehicleType.text = driver.vehicle_type
            tvPrice.text = "Rs " + driver.amount_per_head

        }
        if (!TextUtils.isEmpty(driver.profile_pic)) {
            Constants.showGlide(
                holder.binding.profileImage.context,
                BuildConfig.IMAGE_BASE_URL + driver.profile_pic, holder.binding.profileImage
            )
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

        holder.binding.ivInfo.setOnClickListener {
            setInfoClickCallBack?.let {
                //it(vehicle)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var setVehicleClickListener: ((user: SharingAvailabilityResponse.Response) -> Unit)? =
        null

    fun selectedVehicle(listener: (SharingAvailabilityResponse.Response) -> Unit) {
        setVehicleClickListener = listener
    }


    private var setInfoClickCallBack: ((user: SharingAvailabilityResponse.Response) -> Unit)? =
        null

    fun infoClickCallBack(listener: (SharingAvailabilityResponse.Response) -> Unit) {
        setInfoClickCallBack = listener
    }
}