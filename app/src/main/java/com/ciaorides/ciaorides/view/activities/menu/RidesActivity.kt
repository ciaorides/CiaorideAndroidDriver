package com.ciaorides.ciaorides.view.activities.menu

import android.content.Intent
import android.text.TextUtils
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RidesActivity : BaseActivity<ActivityRidesBinding>() {
    override fun getViewBinding(): ActivityRidesBinding =
        ActivityRidesBinding.inflate(layoutInflater)

    lateinit var myRidesAdapter: MyRidesAdapter

    private val viewModel: MenuViewModel by viewModels()

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge)
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
        binding.rvRides.apply {
            adapter = myRidesAdapter
            layoutManager = LinearLayoutManager(this@RidesActivity)
            visibility = View.VISIBLE
        }

        getRidesTakenDetails()
        handleMyRides()


    }

    private fun getRidesTakenDetails() {
//        if (!TextUtils.isEmpty(Constants.getValue(this@RidesActivity, Constants.USER_ID))) {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getMyRides(
            GlobalUserIdRequest(
                user_id = Constants.getValue(this@RidesActivity, Constants.USER_ID)
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
                        if (data.status) {
                            if (data.response.rides_taken.isEmpty()) {
                                binding.rvRides.visibility = View.GONE
                                binding.noResultsFound.visibility = View.VISIBLE
                            } else {
                                binding.rvRides.visibility = View.VISIBLE
                                binding.noResultsFound.visibility = View.GONE
                                myRidesAdapter.differ.submitList(data.response.rides_taken)
                            }
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }


}