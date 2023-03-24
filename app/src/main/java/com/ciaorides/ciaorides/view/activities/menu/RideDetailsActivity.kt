package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRideDetailsBinding
import com.ciaorides.ciaorides.view.activities.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideDetailsActivity : BaseActivity<ActivityRideDetailsBinding>() {
    override fun getViewBinding(): ActivityRideDetailsBinding =
        ActivityRideDetailsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.ride_details)
        binding.toolbar.profileView.visibility = View.GONE
    }
}