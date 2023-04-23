package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRideDetailsBinding
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.utils.Constants.KEY_RIDES_TAKEN
import com.ciaorides.ciaorides.view.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideDetailsActivity : BaseActivity<ActivityRideDetailsBinding>() {

    override fun getViewBinding(): ActivityRideDetailsBinding =
        ActivityRideDetailsBinding.inflate(layoutInflater)

    lateinit var ridesModel : MyRidesResponse.Response.RidesTaken

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.ride_details)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        ridesModel = intent?.getParcelableExtra<MyRidesResponse.Response.RidesTaken>(KEY_RIDES_TAKEN)!!
        setUpUI()
    }

    private fun setUpUI() {
        ridesModel.let {
            binding.textViewRating.text = it.rating
            binding.textViewToAddress.text = it.to_address
            binding.textViewFromAddress.text = it.from_address
            binding.tvPrice.text = it.total_amount
            binding.tvVehicleNumber.text = it.number_plate
            binding.tvName.text = it.first_name + " " + it.last_name
        }
    }
}