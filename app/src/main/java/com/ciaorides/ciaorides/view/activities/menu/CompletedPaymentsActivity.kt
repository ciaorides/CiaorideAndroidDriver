package com.ciaorides.ciaorides.view.activities.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityCompletedPaymentsBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.PaymentsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.PaymentsAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletedPaymentsActivity : BaseActivity<ActivityCompletedPaymentsBinding>() {
    override fun getViewBinding(): ActivityCompletedPaymentsBinding =
        ActivityCompletedPaymentsBinding.inflate(layoutInflater)

    lateinit var myRidesAdapter: PaymentsAdapter

    private val viewModel: MenuViewModel by viewModels()

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.completed_payments)
        binding.toolbar.profileView.visibility = View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        myRidesAdapter = PaymentsAdapter(object : PaymentsAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                ridesTaken: PaymentsResponse.BookingData
            ) {
                val intent = Intent(this@CompletedPaymentsActivity, RideDetailsActivity::class.java)
                intent.putExtra(Constants.KEY_BOOKING_DATA, ridesTaken)
                startActivity(intent)
            }

        })
        getMyPayments()
        handleMyRides()
    }

    private fun getMyPayments() {
        viewModel.getPayments(
            GlobalUserIdRequest(
                user_id = Constants.getValue(this@CompletedPaymentsActivity, Constants.USER_ID)
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun handleMyRides() {
        viewModel.paymentResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            if (data.response.data!!.isEmpty()) {
                                binding.rvRides.visibility = View.GONE
                            } else {
                                binding.rvRides.visibility = View.VISIBLE
                                myRidesAdapter.differ.submitList(data.response.data)
                                binding.rvRides.apply {
                                    this.adapter = myRidesAdapter
                                    this.layoutManager = LinearLayoutManager(this@CompletedPaymentsActivity, LinearLayoutManager.VERTICAL, false)
                                    this.hasFixedSize()
                                    this.visibility = View.VISIBLE
                                    this.adapter!!.notifyDataSetChanged()
                                }

                                binding.completedPaymentOlnineTime.text = data.response.final_data.online
                                binding.completedPaymentTotalTrips.text = data.response.final_data.total_trips
                                binding.completedPaymentScheduledTime.text = "Payment Completed on : ${data.response.payment_final.completed_date}"
                                binding.completedPaymentPayID.text = "Payment ID : ${data.response.payment_final.payment_id}"
                                binding.completedPaymentmethod.text = data.response.payment_final.upi_id
                                binding.completedPaymentAmount.text = data.response.payment_final.amount
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