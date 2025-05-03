package com.ciaorides.ciaorides.view.activities.menu

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityPendingPaymentsBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.PaymentsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.PaymentsAdapter
import com.ciaorides.ciaorides.view.adapter.PendingPaymentAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPaymentsActivity : BaseActivity<ActivityPendingPaymentsBinding>() {
    override fun getViewBinding(): ActivityPendingPaymentsBinding =
        ActivityPendingPaymentsBinding.inflate(layoutInflater)

    lateinit var myRidesAdapter: PendingPaymentAdapter
    private val viewModel: MenuViewModel by viewModels()

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.pending_payments)
        binding.toolbar.profileView.visibility = View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        myRidesAdapter = PendingPaymentAdapter(object : PendingPaymentAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                ridesTaken: PaymentsResponse.BookingData
            ) {
                val intent = Intent(this@PendingPaymentsActivity, RideDetailsActivity::class.java)
                intent.putExtra(Constants.KEY_BOOKING_DATA, ridesTaken)
                startActivity(intent)
            }

        })
        getRidesTakenDetails()
        handleMyRides()
    }

    private fun getRidesTakenDetails() {
        viewModel.getPendingPayments(
            GlobalUserIdRequest(
                user_id = Constants.getValue(this@PendingPaymentsActivity, Constants.USER_ID)
            )
        )
    }

    private fun handleMyRides() {
        viewModel.paymentResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            if (data.response.data == null) {
                                binding.rvRides.visibility = View.GONE
                            } else {
                                if (data.response.data!!.isEmpty()) {
                                    binding.rvRides.visibility = View.GONE
                                } else {
                                    binding.rvRides.visibility = View.VISIBLE
                                    myRidesAdapter.differ.submitList(data.response.data)
                                    binding.rvRides.apply {
                                        this.adapter = myRidesAdapter
                                        this.layoutManager = LinearLayoutManager(
                                            this@PendingPaymentsActivity,
                                            LinearLayoutManager.VERTICAL,
                                            false
                                        )
                                        this.hasFixedSize()
                                        this.visibility = View.VISIBLE
                                        this.adapter!!.notifyDataSetChanged()
                                    }
                                }
                            }

                            try {
                                binding.pendingPaymentOlnineTime.text =
                                    data.response.final_data.online
                                binding.pendingPaymentTotalTrips.text =
                                    data.response.final_data.total_trips
                                binding.pendingPaymentScheduledTime.text =
                                    "Payment Completed on : ${data.response.payment_final.completed_date}"
                                binding.pendingPaymentPayID.text =
                                    "Payment ID : ${data.response.payment_final.payment_id}"
                                binding.pendingPaymentmethod.text =
                                    data.response.payment_final.upi_id
                                binding.pendingPaymentAmount.text =
                                    data.response.payment_final.amount
                            } catch (e: Exception) {
                                e.printStackTrace()
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