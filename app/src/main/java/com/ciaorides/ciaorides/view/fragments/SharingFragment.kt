package com.ciaorides.ciaorides.view.fragments

import android.view.View
import androidx.core.content.ContextCompat
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.BottomSheetSharingBinding
import com.ciaorides.ciaorides.utils.Constants

class SharingFragment {
    fun handleViews(binding: BottomSheetSharingBinding, checkClick: ((request: Pair<Boolean,Int>) -> Unit)? = null) {
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
        binding.cardCar.setOnClickListener {
            binding.cardCar.strokeColor =
                ContextCompat.getColor(binding.cardCar.context, R.color.appBlue)

            binding.cardBike.strokeColor =
                ContextCompat.getColor(binding.cardCar.context, R.color.white)
        }
        binding.cardBike.setOnClickListener {
            if(binding.tvSeatCount.text.toString().toInt()<2){
                binding.cardBike.strokeColor =
                    ContextCompat.getColor(binding.cardCar.context, R.color.appBlue)
                binding.cardCar.strokeColor =
                    ContextCompat.getColor(binding.cardCar.context, R.color.white)
            }
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
                    binding.cardBike.strokeColor =
                        ContextCompat.getColor(binding.cardCar.context, R.color.white)
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
            Constants.showRideSharingAlert(binding.btnBookNow.context) {
                checkClick?.invoke(Pair(true,binding.tvSeatCount.text.toString().toInt()))
            }
        }
    }
}