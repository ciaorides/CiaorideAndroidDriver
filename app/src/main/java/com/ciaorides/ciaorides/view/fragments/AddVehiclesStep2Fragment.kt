package com.ciaorides.ciaorides.view.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.AddVehiclesFragmentStep2Binding
import com.ciaorides.ciaorides.model.request.AddVehicleDetailsStage2Request
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.activities.user.ImageUploadActivity
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class AddVehiclesStep2Fragment : Fragment(R.layout.add_vehicles_fragment_step_2) {
    val CAMERA_PERMISSION_CODE = 100
    val PICK_IMAGE = 101
    private var finalUrl: Uri? = null
    private var regImgUrl: Uri? = null
    private var insuranceImgUrl: Uri? = null
    private var fitnessImgUrl: Uri? = null
    private var permitImgUrl: Uri? = null
    private var realPath: String? = null
    private var imgType: String? = null

    private var regImgUrlPath: String? = null
    private var insuranceImgUrlPath: String? = null
    private var fitnessImgUrlPath: String? = null
    private var permitImgUrlPath: String? = null
    val data = ArrayList<Uri>()
    private lateinit var binding: AddVehiclesFragmentStep2Binding
    private val viewModel: ManageVehicleImagesViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddVehiclesFragmentStep2Binding.bind(view)

        handleStep2()
        binding.llReg.setOnClickListener {
             imgType = "reg"

            val intent = Intent(activity, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, imgType)
            uploadedImgPath.launch(intent)
        }

        binding.llFitness.setOnClickListener {
            imgType = "fitness"


            val intent = Intent(activity, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, imgType)
            uploadedImgPath.launch(intent)
        }
        binding.llPermit.setOnClickListener {

            imgType = "permit"

            val intent = Intent(activity, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, imgType)
            uploadedImgPath.launch(intent)
        }

        binding.llInsurance.setOnClickListener {


            imgType = "insurance"

            val intent = Intent(activity, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, imgType)
            uploadedImgPath.launch(intent)
        }
    }

    private var uploadedImgPath = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val value = it.data?.getStringExtra("result")
            val imgType = it.data?.getStringExtra("type")
            //  val value = it.data?.getStringExtra("result")
            when (imgType) {
                "reg" -> {
                    regImgUrlPath = value.toString()
                    Constants.showGlide(requireActivity(), Constants.getImageUrl(regImgUrlPath!!), binding.icCamera, null)
                    Log.d(
                        "Image Path", "reg" +
                                regImgUrlPath
                    )
                }
                "fitness" -> {
                    fitnessImgUrlPath = value.toString()
                    Constants.showGlide(requireActivity(), Constants.getImageUrl(fitnessImgUrlPath!!), binding.imgFitness, null)
                    Log.d(
                        "Image Path", "fitness" +
                                regImgUrlPath
                    )
                }
                "permit" -> {
                    permitImgUrlPath = value.toString()
                    Constants.showGlide(requireActivity(), Constants.getImageUrl(permitImgUrlPath!!), binding.imgPermit, null)
                    Log.d(
                        "Image Path", "  permit" +
                                regImgUrlPath
                    )

                }
                "insurance" -> {
                    insuranceImgUrlPath = value.toString()
                    Constants.showGlide(requireActivity(), Constants.getImageUrl(insuranceImgUrlPath!!), binding.imgInsurance, null)
                    Log.d(
                        "Image Path", "  insurance" +
                                regImgUrlPath
                    )
                }
            }
        }
    }

    fun makeFirstStepCall() {
        //TODO remove below code after all screens done
//        (activity as? VehicleDetailsActivity)?.let {
//            it.changeTabs(2)
//            return
//        }

        if (regImgUrlPath.isNullOrBlank()) {
            Toast.makeText(
                activity, "Upload Vehicle Registration certificate image",
                Toast.LENGTH_LONG
            ).show();
            return
        }

        if (binding.regNum.text!!.toString().isEmpty()) {
            Toast.makeText(
                activity, "Enter registration number",
                Toast.LENGTH_LONG
            ).show();
            return
        }
        if (insuranceImgUrlPath.isNullOrBlank()) {
            Toast.makeText(
                activity, "Upload Vehicle Insurance certificate image",
                Toast.LENGTH_LONG
            ).show();
            return
        }


        if (binding.insuranceNum.text!!.toString().equals("")) {
            Toast.makeText(
                activity, "Enter Insurance number",
                Toast.LENGTH_LONG
            ).show();
            return
        }

        if (fitnessImgUrlPath.isNullOrBlank()) {
            Toast.makeText(
                activity, "Upload Vehicle Fitness certificate image",
                Toast.LENGTH_LONG
            ).show();
            return
        }

        if (binding.fitnessNum.text!!.toString().equals("")) {
            Toast.makeText(
                activity, "Enter Fitness certificate number",
                Toast.LENGTH_LONG
            ).show();
            return
        }
        if (permitImgUrlPath.isNullOrBlank()) {
            Toast.makeText(
                activity, "Upload VehiclePermit certificate image",
                Toast.LENGTH_LONG
            ).show();
            return
        }
        if (binding.permitNum.text!!.toString().equals("")) {
            Toast.makeText(
                activity, "Enter Permit certificate number",
                Toast.LENGTH_LONG
            ).show();
            return
        }

        viewModel.addVehiclesStage2(
            AddVehicleDetailsStage2Request(
                vehicle_id = (activity as? VehicleDetailsActivity)?.let { it.vehicleId }.toString(),
                vehicle_registration_number = binding.regNum.text.toString(),
                fitness_certification_number = binding.fitnessNum.text.toString(),
                vehicle_insurance_number = binding.insuranceNum.text.toString(),
                vehicle_permit_number = binding.permitNum.text.toString(),
                vehicle_registration_image = regImgUrlPath.toString(),
                vehicle_insurance_image = insuranceImgUrlPath.toString(),
                vehicle_permit_image = permitImgUrlPath.toString(),
                vehicle_step2 = "yes",
                fitness_certification_image = fitnessImgUrlPath.toString(),
                number_plate=binding.vehicleNumber.text.toString()
            )
        )

        //to here
    }

    private fun handleStep2() {
        viewModel.addVehiclesStage2Response.observe(requireActivity()) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    //binding.progressLayout.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        (activity as? VehicleDetailsActivity)?.let {
                            it.changeTabs(2)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    //binding.progressLayout.root.visibility = View.GONE
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }
        }
    }

    companion object {
        fun newInstance(): AddVehiclesStep2Fragment {
            val args = Bundle()
            val fragment = AddVehiclesStep2Fragment()
            fragment.arguments = args
            return fragment
        }
    }
}



