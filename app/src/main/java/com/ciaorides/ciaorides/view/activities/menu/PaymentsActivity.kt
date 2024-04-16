package com.ciaorides.ciaorides.view.activities.menu


import android.content.Intent
import android.opengl.Visibility
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityPaymentsBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.PaymentsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.MyRidesAdapter
import com.ciaorides.ciaorides.view.adapter.PaymentsAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PaymentsActivity : BaseActivity<ActivityPaymentsBinding>() {
    override fun getViewBinding(): ActivityPaymentsBinding =
        ActivityPaymentsBinding.inflate(layoutInflater)

    lateinit var myRidesAdapter: PaymentsAdapter

    private val viewModel: MenuViewModel by viewModels()

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.payments)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        myRidesAdapter = PaymentsAdapter(object : PaymentsAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                ridesTaken: PaymentsResponse.BookingData
            ) {
                val intent = Intent(this@PaymentsActivity, RideDetailsActivity::class.java)
                intent.putExtra(Constants.KEY_BOOKING_DATA, ridesTaken)
                startActivity(intent)
            }

        })
        getRidesTakenDetails()
        handleMyRides()
    }

    private fun getRidesTakenDetails() {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getPayments(
            GlobalUserIdRequest(
                user_id = Constants.getValue(this@PaymentsActivity, Constants.USER_ID)
            )
        )
    }

    private fun handleMyRides() {
        viewModel.paymentResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            if (data.response.data!!.isEmpty()) {
                                binding.rvRides.visibility = View.GONE
                                binding.noResultsFound.visibility = View.VISIBLE
                            } else {
                                binding.rvRides.visibility = View.VISIBLE
                                binding.noResultsFound.visibility = View.GONE
                                myRidesAdapter.differ.submitList(data.response.data)
                                binding.rvRides.apply {
                                    this.adapter = myRidesAdapter
                                    this.layoutManager = LinearLayoutManager(this@PaymentsActivity, LinearLayoutManager.VERTICAL, false)
                                    this.hasFixedSize()
                                    this.visibility = View.VISIBLE
                                    this.adapter!!.notifyDataSetChanged()
                                }
                            }
                        }
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