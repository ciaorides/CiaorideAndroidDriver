package com.ciaorides.ciaorides.view.activities.menu

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityMyVehiclesBinding
import com.ciaorides.ciaorides.model.request.DeleteVehicleRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.adapter.MyVehiclesAdapter
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyVehiclesActivity : BaseActivity<ActivityMyVehiclesBinding>() {
    override fun getViewBinding(): ActivityMyVehiclesBinding =
        ActivityMyVehiclesBinding.inflate(layoutInflater)

    private val viewModel: MenuViewModel by viewModels()

    @Inject
    lateinit var myVehiclesAdapter: MyVehiclesAdapter

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.my_vehicles)
        binding.addVehicle.setOnClickListener {
            val intent = Intent(this, VehicleDetailsActivity::class.java)
            intent.putExtra(Constants.STAGE_STATUS, "1")
            intent.putExtra(Constants.VEHICLE_ID, "0")
            startActivity(intent)
        }
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        binding.rvVehicles.apply {
            adapter = myVehiclesAdapter
            layoutManager = LinearLayoutManager(this@MyVehiclesActivity)
            visibility = View.VISIBLE
        }
        handleMyVehicle()
        handleDeleteVehicle()
        vehiclesCall()
        myVehiclesAdapter.onDeleteClicked { vehicle ->
            Constants.showDeleteVehicleAlert(this@MyVehiclesActivity) {
                if (it) {
                    binding.progressLayout.root.visibility = View.VISIBLE
                    makeVehicleDeleteCall(vehicle.id)
                }
            }
        }
        myVehiclesAdapter.onUpdateClick { vehicle ->
            val intent = Intent(this, VehicleDetailsActivity::class.java)
            if (vehicle.vehicle_step2 == "no")
                intent.putExtra(Constants.STAGE_STATUS, "2")
            else
                intent.putExtra(Constants.STAGE_STATUS, "3")

            intent.putExtra(Constants.VEHICLE_ID, vehicle.id)
            startActivity(intent)
        }
    }

    private fun vehiclesCall() {
//        if (!TextUtils.isEmpty(Constants.getValue(this@MyVehiclesActivity, Constants.USER_ID))) {
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.getMyVehicles(
                GlobalUserIdRequest(
                    user_id = Constants.getValue(this@MyVehiclesActivity, Constants.USER_ID)
                    //user_id = Constants.TEMP_USER_ID
                )
            )
//        }
    }

    private fun makeVehicleDeleteCall(id: String) {
        viewModel.deleteVehicle(
            DeleteVehicleRequest(
                user_id =Constants.getValue(this@MyVehiclesActivity, Constants.USER_ID),
                vehicle_id = id
            )
        )
    }

    private fun handleDeleteVehicle() {
        viewModel.deleteVehicleResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT)
                                .show()
                            vehiclesCall()
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

    override fun onRestart() {
        vehiclesCall()
        super.onRestart()
    }

    private fun handleMyVehicle() {
        viewModel.myVehicleResponse.observe(this) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            myVehiclesAdapter.differ.submitList(data.response)
                            binding.rvVehicles.apply {
                                adapter = myVehiclesAdapter
                                layoutManager = LinearLayoutManager(this@MyVehiclesActivity)
                                visibility = View.VISIBLE
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