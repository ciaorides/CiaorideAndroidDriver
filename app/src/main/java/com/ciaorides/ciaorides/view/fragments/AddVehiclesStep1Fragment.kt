package com.ciaorides.ciaorides.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.AddVehiclesFragmentBinding
import com.ciaorides.ciaorides.model.request.AddVehicleDetailsRequest
import com.ciaorides.ciaorides.model.request.BrandsRequest
import com.ciaorides.ciaorides.model.request.VehicleModelRequest
import com.ciaorides.ciaorides.model.response.VehicleBrandsResponse
import com.ciaorides.ciaorides.model.response.VehicleModelsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.adapter.VehicleBrandsAdapter
import com.ciaorides.ciaorides.view.adapter.VehicleModelsAdapter
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddVehiclesStep1Fragment : Fragment(R.layout.add_vehicles_fragment) {
    private lateinit var vehicleBrandsAdapter: VehicleBrandsAdapter
    private lateinit var vehicleModelsAdapter: VehicleModelsAdapter
    var brandId: String? = null
    var modelId: String? = null
    var vehicleType: String? = null

    private lateinit var binding: AddVehiclesFragmentBinding
    private val viewModel: ManageVehicleImagesViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddVehiclesFragmentBinding.bind(view)

        handleVehicleModelResponse()
        handleVehicleBrandResponse()

        binding.tVBrand.setOnClickListener {
            if (vehicleType == null || vehicleType?.isEmpty() == true) {
                Toast.makeText(requireActivity(), "Please select vehicle type", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.getVehicleBrands(
                    BrandsRequest(
                        vehicle_type = vehicleType.toString()
                    )
                )
            }
        }
        binding.tVVehicleType.setOnClickListener {
            openDialogForVehicleType()
        }

        binding.tVModelName.setOnClickListener {
            if (brandId == null || brandId?.isEmpty() == true) {
                Toast.makeText(requireActivity(), "Please select Brand", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.getVehicleModels(
                    VehicleModelRequest(
                        brand_id = brandId.toString()
                    )
                )
            }
        }

    }

    private fun handleVehicleBrandResponse() {
        viewModel.vehicleBrandsResponse.observe(requireActivity()) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            openDialogForVehicleBrand(data)

                        } else
                            Toast.makeText(
                                requireActivity(),
                                data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleVehicleModelResponse() {
        viewModel.vehicleModelsResponse.observe(requireActivity()) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            openDialogForVehicleModel(data)

                        } else Toast.makeText(
                            requireActivity(),
                            dataHandler.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun openDialogForVehicleType() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.car_type_layout)
        val imgCar = dialog.findViewById(R.id.llCar) as LinearLayout
        val imgAuto = dialog.findViewById(R.id.llAuto) as LinearLayout
        val imgBike = dialog.findViewById(R.id.llBike) as LinearLayout
        (dialog.findViewById(R.id.ivClose) as ImageView).setOnClickListener {
            dialog.dismiss()
        }
        imgAuto.setOnClickListener {
            vehicleType = "auto"
            binding.tVVehicleType.text = "Auto"
            dialog.dismiss()
            /* if (vehicleType.toString() == "auto") {
                 vehicleType = "auto"
                 binding.tVVehicleType.text = "Auto"
                 dialog.dismiss()
             } else {
                 brandId = ""
                 modelId = ""
                 vehicleType = "auto"
                 binding.tVVehicleType.text = "Auto"
                 binding.tVBrand.text = resources.getString(R.string.select_brand)
                 binding.tVModelName.text = resources.getString(R.string.select_model)
                 dialog.dismiss()
             }*/
        }
        imgCar.setOnClickListener {
            vehicleType = "car"
            binding.tVVehicleType.text = "Car"
            dialog.dismiss()
        }
        imgBike.setOnClickListener {
            vehicleType = "bike"
            binding.tVVehicleType.text = "Bike"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openDialogForVehicleBrand(data: VehicleBrandsResponse) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.vehicle_brand_layout)
        val rViewBrands = dialog.findViewById(R.id.rViewBrands) as RecyclerView

        (dialog.findViewById(R.id.ivClose) as ImageView).setOnClickListener {
            dialog.dismiss()
        }
        rViewBrands.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        vehicleBrandsAdapter = VehicleBrandsAdapter(data)

        // Setting the Adapter with the recyclerview
        rViewBrands.adapter = vehicleBrandsAdapter

        vehicleBrandsAdapter.onItemClicked { brand ->
            binding.tVBrand.text = brand.title
            brandId = brand.id
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun openDialogForVehicleModel(data: VehicleModelsResponse) {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.vehicle_model_layout)
        val rViewModel = dialog.findViewById(R.id.rViewModel) as RecyclerView

        (dialog.findViewById(R.id.ivClose) as ImageView).setOnClickListener {
            dialog.dismiss()
        }
        rViewModel.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        vehicleModelsAdapter = VehicleModelsAdapter(data)

        // Setting the Adapter with the recyclerview
        rViewModel.adapter = vehicleModelsAdapter

        vehicleModelsAdapter.onItemClicked { model ->
            binding.tVModelName.text = model.title
            modelId = model.id
            dialog.dismiss()
        }
        dialog.show()
    }

    companion object {
        fun newInstance(): AddVehiclesStep1Fragment {
            val args = Bundle()
            val fragment = AddVehiclesStep1Fragment()
            fragment.arguments = args
            return fragment
        }
    }

    fun makeFirstStepCall() {
        //TODO remove below code after all screens done
//        (activity as? VehicleDetailsActivity)?.let {
//            it.changeTabs(1)
//            return
//        }
        //to here

        if (TextUtils.isEmpty(vehicleType)) {
            Toast.makeText(activity, "Please select vehicle type.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (TextUtils.isEmpty(brandId)) {
            Toast.makeText(activity, "Please select vehicle brand.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (TextUtils.isEmpty(modelId)) {
            Toast.makeText(activity, "Please select vehicle model.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        viewModel.addVehiclesStage1(
            AddVehicleDetailsRequest(
                brand_id = brandId,
                vehicle_type = vehicleType,
                model_id = modelId,
                vehicle_step1 = "yes",
                user_id = Constants.getValue(requireActivity(), Constants.USER_ID)
            )
        )
        handleStep1()
    }

    private fun handleStep1() {
        viewModel.addVehiclesStageResponse.observe(requireActivity()) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    //binding.progressLayout.root.visibility = View.GONE
//                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
//                        .show()
                    dataHandler.data?.let { data ->
                        (activity as? VehicleDetailsActivity)?.let {
                            it.vehicleId = dataHandler.data.response
                            it.changeTabs(1)

                        }
                    }
                }
                is DataHandler.ERROR -> {
                    //binding.progressLayout.root.visibility = View.GONE
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}

