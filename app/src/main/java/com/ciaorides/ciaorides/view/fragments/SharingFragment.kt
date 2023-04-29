package com.ciaorides.ciaorides.view.fragments

import android.view.View
import androidx.core.content.ContextCompat
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.BottomSheetSharingBinding

class SharingFragment {
    fun handleViews(binding: BottomSheetSharingBinding, checkClick: ((request: Boolean) -> Unit)? = null) {
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
            if (4 >= counter) {
                binding.tvSeatCount.text = counter.toString()
                if (counter > 1) {
                    binding.tvBikeHeading.setTextColor(
                        ContextCompat.getColor(
                            binding.tvAddSeats.context,
                            R.color.appGray
                        )
                    )
                    binding.tvBikeSubHeading.setTextColor(
                        ContextCompat.getColor(
                            binding.tvAddSeats.context,
                            R.color.appGray
                        )
                    )
                } else {
                    binding.tvBikeHeading.setTextColor(
                        ContextCompat.getColor(
                            binding.tvAddSeats.context,
                            R.color.appTextGray
                        )
                    )
                    binding.tvBikeSubHeading.setTextColor(
                        ContextCompat.getColor(
                            binding.tvAddSeats.context,
                            R.color.appTextGray
                        )
                    )
                }
            }
        }
        binding.tvRemoveSeats.setOnClickListener {
            var counter = binding.tvSeatCount.text.toString().toInt()
            counter -= 1
            if (counter < 2) {
                binding.tvBikeHeading.setTextColor(
                    ContextCompat.getColor(
                        binding.tvAddSeats.context,
                        R.color.appTextGray
                    )
                )
                binding.tvBikeSubHeading.setTextColor(
                    ContextCompat.getColor(
                        binding.tvAddSeats.context,
                        R.color.appTextGray
                    )
                )
            }
            if (counter >= 0) {
                binding.tvSeatCount.text = counter.toString()
            }
        }

        binding.btnBookNow.setOnClickListener {
            //validate
            checkClick?.invoke(true)
        }
    }
}