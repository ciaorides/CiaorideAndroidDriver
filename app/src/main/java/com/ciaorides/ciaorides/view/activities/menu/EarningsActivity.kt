package com.ciaorides.ciaorides.view.activities.menu


import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEarningsBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.utils.Constants.TEMP_USER_ID
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EarningsActivity : BaseActivity<ActivityEarningsBinding>() {

    private val viewModel: MenuViewModel by viewModels()

    override fun getViewBinding(): ActivityEarningsBinding =
        ActivityEarningsBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.payments)
        binding.toolbar.profileView.visibility = View.GONE

        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        getMyEarnings()
        handleMyEarnings()
    }

    private fun getMyEarnings() {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getEmergencyContactList(
            GlobalUserIdRequest(
                user_id = TEMP_USER_ID
            )
        )
    }

    private fun handleMyEarnings() {
        viewModel.earningsResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.response.isEmpty()) {
                           /* binding.rvRides.visibility = View.GONE
                            binding.noResultsFound.visibility = View.VISIBLE*/
                        } else {
                            binding.textViewOnlineTime.setText(data.response.get(0).Ride_Time)
                            binding.textViewOnlineTime.setText(data.response.get(0).Ride_Time)
                           /* adapter.differ.submitList(data.response)
                            binding.rvRides.apply {
                                adapter = adapter
                                layoutManager = LinearLayoutManager(this@EmergencyContact)
                                visibility = View.VISIBLE
                            }
                            binding.noResultsFound.visibility = View.GONE*/
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