package com.ciaorides.ciaorides.view.activities

import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRideRequestsBinding
import com.ciaorides.ciaorides.model.request.RideRequestResponse
import com.ciaorides.ciaorides.view.adapter.RideRequestAdapter
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RideRequestsActivity : BaseActivity<ActivityRideRequestsBinding>() {

    @Inject
    lateinit var adapter: RideRequestAdapter

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.ride_requests)
        binding.recyclerView.adapter = adapter
        adapter.differ.submitList(getDataList())
    }

    private fun getDataList(): ArrayList<RideRequestResponse> {

        val list = ArrayList<RideRequestResponse>()
        var data = RideRequestResponse()
        data.distance = "10Km"
        data.message = "hitech city near cyber towers"
        data.name = "Srinivas"
        data.price = "200"
        list.add(data)

        data = RideRequestResponse()
        data.distance = "10Km"
        data.message = "hitech city near cyber towers"
        data.name = "Srikanth"
        data.price = "100"
        list.add(data)
        return list
    }

    override fun getViewBinding(): ActivityRideRequestsBinding =
        ActivityRideRequestsBinding.inflate(layoutInflater)
}