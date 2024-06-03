package com.ciaorides.ciaorides.view.activities.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRidesBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.Constants.KEY_RIDES_TAKEN
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.MyRidesAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RidesActivity : BaseActivity<ActivityRidesBinding>() {
    override fun getViewBinding(): ActivityRidesBinding =
        ActivityRidesBinding.inflate(layoutInflater)

    lateinit var myRidesAdapter: MyRidesAdapter

    private val viewModel: MenuViewModel by viewModels()
    lateinit var responseData : MyRidesResponse

    @SuppressLint("NotifyDataSetChanged")
    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.my_rides)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        myRidesAdapter = MyRidesAdapter(object : MyRidesAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                ridesTaken: MyRidesResponse.Response.RidesTaken
            ) {
                val intent = Intent(this@RidesActivity, RideDetailsActivity::class.java)
                intent.putExtra(KEY_RIDES_TAKEN, ridesTaken)
                startActivity(intent)
            }

        })
        setUpTabs()
        getRidesTakenDetails()
        handleMyRides()
    }

    private fun setUpTabs() {
        binding.tabs.addTab(binding.tabs.newTab().setText("Rides Taken"),true);
        binding.tabs.addTab(binding.tabs.newTab().setText("Rides Scheduled"));
        binding.tabs.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setUpData(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setUpData(position: Int) {
        if (position == 0) {
            if (responseData.status) {
                if (responseData.response.rides_taken!!.isEmpty()) {
                    binding.rvRides.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    binding.rvRides.visibility = View.VISIBLE
                    binding.noResultsFound.visibility = View.GONE
                    myRidesAdapter.differ.submitList(responseData.response.rides_taken)
                    binding.rvRides.apply {
                        this.adapter = myRidesAdapter
                        this.layoutManager = LinearLayoutManager(this@RidesActivity, LinearLayoutManager.VERTICAL, false)
                        this.hasFixedSize()
                        this.visibility = View.VISIBLE
                        this.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        } else if (position == 1){
            if (responseData.status) {
                if (responseData.response.rides_scheduled!!.isEmpty()) {
                    binding.rvRides.visibility = View.GONE
                    binding.noResultsFound.visibility = View.VISIBLE
                } else {
                    binding.rvRides.visibility = View.VISIBLE
                    binding.noResultsFound.visibility = View.GONE
                    myRidesAdapter.differ.submitList(responseData.response.rides_scheduled)
                    binding.rvRides.apply {
                        this.adapter = myRidesAdapter
                        this.layoutManager = LinearLayoutManager(this@RidesActivity, LinearLayoutManager.VERTICAL, false)
                        this.hasFixedSize()
                        this.visibility = View.VISIBLE
                        this.adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun getRidesTakenDetails() {
//        if (!TextUtils.isEmpty(Constants.getValue(this@RidesActivity, Constants.USER_ID))) {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getMyRides(
            GlobalUserIdRequest(
                user_id = Constants.getValue(this@RidesActivity, Constants.USER_ID),
                user_type = "driver"
//                user_id = "2250"
            )
        )
//        }
    }


    private fun setUpTabLayout(
        response: MyRidesResponse,
    ) {
        response.let {

        }
    }

    private fun handleMyRides() {
        viewModel.myRidesResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        responseData = data
                        setUpData(0)
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }

        }
    }
}