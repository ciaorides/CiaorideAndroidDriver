package com.ciaorides.ciaorides.view.activities.menu

import android.content.Intent
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmergencyContact : BaseActivity<ActivityEmergencyContactBinding>() {
    override fun getViewBinding(): ActivityEmergencyContactBinding =
        ActivityEmergencyContactBinding.inflate(layoutInflater)

    @Inject
    lateinit var contactAdapter: EmergencyContactAdapter

    private val viewModel: MenuViewModel by viewModels()

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.emergency_contact)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        contactAdapter.onDeleteClicked { user ->
            Constants.showDeleteVehicleAlert(this@EmergencyContact) {
                if (it) {
                    binding.progressLayout.root.visibility = View.VISIBLE
//                    user.id?.let { it1 -> deleteBankDetails(it1) }
                }
            }
        }
        contactAdapter.onEditClicked { user ->
            val intent =  Intent(this,AddBankActivity::class.java)
//            intent.putExtra(Constants.KEY_BANK_DETAILS,bankModel)
            startActivity(intent)
        }

        binding.rvRides.apply {
            adapter = contactAdapter
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
                user_id = Constants.getValue(this@EmergencyContact, Constants.USER_ID)
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
                            contactAdapter.differ.submitList(data.response)
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