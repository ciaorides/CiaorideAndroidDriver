package com.ciaorides.ciaorides.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.AddVehiclesFragmentStep3Binding
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.ImageUpload
import com.ciaorides.ciaorides.model.request.AddVehicleDetailsStage2Request
import com.ciaorides.ciaorides.model.request.AddVehicleStage3Request
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.activities.user.ImageUploadActivity
import com.ciaorides.ciaorides.view.adapter.VehicleImagesAdapter
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import com.google.gson.JsonObject
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File

@AndroidEntryPoint
class AddVehiclesStep3Fragment : Fragment(R.layout.add_vehicles_fragment_step_3) {
    private lateinit var imagesAdapter: VehicleImagesAdapter
    val CAMERA_PERMISSION_CODE = 100
    private var finalUrl: Uri? = null
    private var realPath: String? = null
    private var vehicleImgUrlPath: String? = null

    private var imgType: String? = null
    val data = ArrayList<Uri>()
    private lateinit var binding: AddVehiclesFragmentStep3Binding
    private val viewModel: ManageVehicleImagesViewModel by viewModels()
    val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddVehiclesFragmentStep3Binding.bind(view)
        imagesAdapter = VehicleImagesAdapter(data)

        handleAddVehiclesStage3Response()
        binding.rviewVehicleImgs.adapter = imagesAdapter
        binding.rviewVehicleImgs.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.icCamera3.setOnClickListener {
            imgType = "vehicleImages"

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
                "vehicleImages" -> {
                    vehicleImgUrlPath = value.toString()
                    Constants.showGlide(requireActivity(), Constants.getImageUrl(vehicleImgUrlPath!!), binding.icCamera3, null)

                    Log.d(
                        "Image Path", "Vehicle" +
                                vehicleImgUrlPath
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(): AddVehiclesStep3Fragment {
            val fragment = AddVehiclesStep3Fragment()
            return fragment
        }
    }

    fun makeFirstStepCall() {
    /*    val stringData = "1"
        val requestBody: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), stringData)

        viewModel.vehicleImageUpload(descriptionList, requestBody)
        */

        var vehicleImage = AddVehicleStage3Request.VehicleImage(
            image = vehicleImgUrlPath!!
        )

        var listImages = arrayListOf(
            vehicleImage
        )
        viewModel.addVehicle3(
            AddVehicleStage3Request(
                user_id = Constants.getValue(requireActivity(), Constants.USER_ID),
                vehicle_id = (activity as? VehicleDetailsActivity)?.let { it.vehicleId }.toString(),
                vehicle_step3 = "yes",
                vehicle_images = listImages
            )
        )
    }

    fun handleAddVehiclesStage3Response(){
        viewModel.addVehiclesStage3Response.observe(requireActivity()){dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    //binding.progressLayout.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        (activity as? VehicleDetailsActivity)?.let {
                            Toast.makeText(
                                requireActivity(), "Vehicle added successfully",
                                Toast.LENGTH_LONG
                            ).show()
                            requireActivity().finish()
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
}



