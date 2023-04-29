package com.ciaorides.ciaorides.view.fragments

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.BottomSheetAvailableRidersBinding
import com.ciaorides.ciaorides.model.response.SharingAvailabilityResponse
import com.ciaorides.ciaorides.view.adapter.RidesAvailabilityAdapter

class AvailabilityDrivers {

    fun updateData(
        binding: BottomSheetAvailableRidersBinding,
        dataList: List<SharingAvailabilityResponse.Response>,
        count: Int
    ) {
        val ridersAdapter = RidesAvailabilityAdapter()
        binding.rvRiders.apply {
            adapter = ridersAdapter
        }
        if (dataList.isNotEmpty()) {
            ridersAdapter.differ.submitList(dataList)
        } else {
            //TODO error alert
        }

        binding.llOutStationRide.visibility = View.VISIBLE
        binding.tvBoth.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
        }
        binding.tvMale.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
        }
        binding.tvFemale.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
        }

        binding.tvAddSeats.setOnClickListener {
            var counter = binding.tvSeatCount.text.toString().toInt()
            counter += 1
            if (count >= counter) {
                binding.tvSeatCount.text = counter.toString()
            }
        }
        binding.tvRemoveSeats.setOnClickListener {
            var counter = binding.tvSeatCount.text.toString().toInt()
            counter -= 1
            if (counter >= 0) {
                binding.tvSeatCount.text = counter.toString()
            }
        }

        binding.btnBookNow.setOnClickListener {
            //validate
            //checkClick?.invoke(true)
        }
    }

}