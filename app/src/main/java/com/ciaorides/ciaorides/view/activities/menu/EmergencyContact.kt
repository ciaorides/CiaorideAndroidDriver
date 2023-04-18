package com.ciaorides.ciaorides.view.activities.menu

import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEmergencyContactBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.EmergencyContactAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import javax.inject.Inject

class EmergencyContact : BaseActivity<ActivityEmergencyContactBinding>() {
    override fun getViewBinding(): ActivityEmergencyContactBinding =
        ActivityEmergencyContactBinding.inflate(layoutInflater)

    @Inject
    lateinit var adapter: EmergencyContactAdapter

    private val viewModel: MenuViewModel by viewModels()

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.emergency_contact)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        binding.rvRides.apply {
            adapter = adapter
            layoutManager = LinearLayoutManager(this@EmergencyContact)
            visibility = View.VISIBLE
        }
        getEmergencyContacts()
        handleMyContacts()
    }

    private fun getEmergencyContacts() {
//        if (!TextUtils.isEmpty(Constants.getValue(this@RidesActivity, Constants.USER_ID))) {
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.getEmergencyContactList(
            GlobalUserIdRequest(
                //user_id = Constants.getValue(this@MyVehiclesActivity, Constants.USER_ID)
                user_id = Constants.TEMP_USER_ID
            )
        )
//        }
    }

    private fun handleMyContacts() {
        viewModel.contactResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.response.isEmpty()) {
                            binding.rvRides.visibility = View.GONE
                            binding.noResultsFound.visibility = View.VISIBLE
                        } else {
                            adapter.differ.submitList(data.response)
                            binding.rvRides.apply {
                                adapter = adapter
                                layoutManager = LinearLayoutManager(this@EmergencyContact)
                                visibility = View.VISIBLE
                            }
                            binding.noResultsFound.visibility = View.GONE
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