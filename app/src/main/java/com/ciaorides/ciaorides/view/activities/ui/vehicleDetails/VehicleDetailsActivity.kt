package com.ciaorides.ciaorides.view.activities.ui.vehicleDetails

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityVehicleDetailsBinding
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.di.NetworkRepository.Companion.setInterfaceInstance
import com.ciaorides.ciaorides.di.NetworkRepository.Companion.setInterfaceInstanceAddVehicle
import com.ciaorides.ciaorides.model.AddVehicleImageUpload
import com.ciaorides.ciaorides.model.ImageUpload
import com.ciaorides.ciaorides.model.request.AddVehicleStage3Request
import com.ciaorides.ciaorides.model.request.VehicleModelRequest
import com.ciaorides.ciaorides.model.response.AddVehicleStage3Response
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.AddVehiclesAdapterMain
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep1Fragment
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep2Fragment
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep3Fragment
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import retrofit2.Response

@AndroidEntryPoint
class VehicleDetailsActivity : BaseActivity<ActivityVehicleDetailsBinding>(),
    AddVehicleImageUpload {
    private val viewModel: ManageVehicleImagesViewModel by viewModels()
    private val imagesArray = ArrayList<AddVehicleStage3Request.VehicleImage>()
    var vehicleId = 0
    private var stageStatus: String? = null
    override fun getViewBinding(): ActivityVehicleDetailsBinding =
        ActivityVehicleDetailsBinding.inflate(layoutInflater)


    private var addVehicleStep1Fragment: AddVehiclesStep1Fragment? = null
    private var addVehicleStep2Fragment: AddVehiclesStep2Fragment? = null
    private var addVehicleStep3Fragment: AddVehiclesStep3Fragment? = null

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.vehicle_details)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        stageStatus = intent.getStringExtra(Constants.STAGE_STATUS)
        vehicleId = intent.getStringExtra(Constants.VEHICLE_ID)?.toIntOrNull()!!

        handleStep3()
        val data = ArrayList<String>()
        data.add(getString(R.string.book_a_taxi))
        data.add(getString(R.string.start_pooling))
        data.add(getString(R.string.travel_intercity))

        val viewPagerAdapter = AddVehiclesAdapterMain(supportFragmentManager)


        // add the fragments
        addVehicleStep1Fragment = AddVehiclesStep1Fragment.newInstance()
        addVehicleStep2Fragment = AddVehiclesStep2Fragment.newInstance()
        addVehicleStep3Fragment = AddVehiclesStep3Fragment.newInstance()
        viewPagerAdapter.add(addVehicleStep1Fragment)
        viewPagerAdapter.add(addVehicleStep2Fragment)
        viewPagerAdapter.add(addVehicleStep3Fragment)


        binding.pagerAddVehicle.adapter = viewPagerAdapter
        binding.pagerAddVehicle.beginFakeDrag()

        binding.btnSave.setOnClickListener {
            if (binding.pagerAddVehicle.currentItem == 0) {
                addVehicleStep1Fragment?.makeFirstStepCall()
            } else if (binding.pagerAddVehicle.currentItem == 1) {
                addVehicleStep2Fragment?.makeFirstStepCall()
            } else if (binding.pagerAddVehicle.currentItem == 2) {
                addVehicleStep3Fragment?.makeFirstStepCall()
            }

        }
        setInterfaceInstanceAddVehicle(this@VehicleDetailsActivity)
        binding.txt1.setTextColor(resources.getColor(R.color.appGray))
        binding.txt2.setTextColor(resources.getColor(R.color.appGray))
        binding.txt3.setTextColor(resources.getColor(R.color.appGray))
        when (stageStatus) {
            "1" -> {
                changeTabs(0)
            }
            "2" -> {
                changeTabs(1)
            }
            "3" -> {
                changeTabs(2)
            }

        }


    }

    fun changeTabs(index: Int) {
        when (index) {
            0 -> {
                binding.txt1.setTextColor(resources.getColor(R.color.white))
                binding.txt2.setTextColor(resources.getColor(R.color.appGray))
                binding.txt3.setTextColor(resources.getColor(R.color.appGray))
                binding.imgRound1.visibility = View.VISIBLE
                binding.imgRound1.setBackgroundResource(R.drawable.circle_blue)
                binding.imgRound2.visibility = View.GONE
                binding.imgRound3.visibility = View.GONE
            }
            1 -> {

                binding.txt1.setTextColor(resources.getColor(R.color.white))
                binding.txt2.setTextColor(resources.getColor(R.color.white))
                binding.txt3.setTextColor(resources.getColor(R.color.appGray))

                binding.imgRound1.setBackgroundResource(R.drawable.circle_green)
                binding.imgRound2.visibility = View.VISIBLE
                binding.imgRound2.setBackgroundResource(R.drawable.circle_blue)
                binding.imgRound3.visibility = View.GONE
                binding.pagerAddVehicle.setCurrentItem(index, true)

            }
            else -> {
                binding.txt1.setTextColor(resources.getColor(R.color.white))
                binding.txt2.setTextColor(resources.getColor(R.color.white))
                binding.txt3.setTextColor(resources.getColor(R.color.white))
                binding.imgRound1.setBackgroundResource(R.drawable.circle_green)
                binding.imgRound2.setBackgroundResource(R.drawable.circle_green)
                binding.imgRound3.visibility = View.VISIBLE
                binding.imgRound3.setBackgroundResource(R.drawable.circle_blue)
                binding.pagerAddVehicle.setCurrentItem(index, true)
            }
        }
    }

    override fun imageUploadResponseHanding(imageUploadResponse: Response<JsonObject>) {
        Log.d("Upload Image", imageUploadResponse.message() + "Upload successful")
        var obj = JSONObject(imageUploadResponse.body().toString())
        val arrayData = obj.getJSONObject("result_arr").getJSONArray("totalFiles")

        for (i in 0..arrayData.length() - 1) {
            imagesArray.add(
                i,
                AddVehicleStage3Request.VehicleImage(
                    arrayData.getJSONObject(i).getString("full_path")
                )
            )
            Log.d("Attached Image", imagesArray.get(i).image)
        }

        viewModel.addVehicle3(
            AddVehicleStage3Request(
                user_id = Constants.getValue(this, Constants.USER_ID),
                vehicle_id = vehicleId.toString(),
                vehicle_step3 = "yes",
                vehicle_images = imagesArray
            )
        )
    }

    private fun handleStep3() {
        viewModel.addVehiclesStage3Response.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    //binding.progressLayout.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        (this as? VehicleDetailsActivity)?.let {
                            Toast.makeText(
                                this, "Vehicle added successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    //binding.progressLayout.root.visibility = View.GONE
                    Toast.makeText(this, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}


