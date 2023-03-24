package com.ciaorides.ciaorides.view.fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.AddVehiclesFragmentBinding
import com.ciaorides.ciaorides.databinding.AddVehiclesFragmentStep2Binding
import com.ciaorides.ciaorides.model.request.AddVehicleDetailsRequest
import com.ciaorides.ciaorides.model.request.BrandsRequest
import com.ciaorides.ciaorides.model.request.VehicleModelRequest
import com.ciaorides.ciaorides.model.response.VehicleBrandsResponse
import com.ciaorides.ciaorides.model.response.VehicleModelsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.adapter.VehicleBrandsAdapter
import com.ciaorides.ciaorides.view.adapter.VehicleImagesAdapter
import com.ciaorides.ciaorides.view.adapter.VehicleModelsAdapter
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class AddVehiclesStep2Fragment : Fragment(R.layout.add_vehicles_fragment_step_2) {
    private lateinit var imagesAdapter: VehicleImagesAdapter
    val CAMERA_PERMISSION_CODE = 100
    val PICK_IMAGE = 101
    private var finalUrl: Uri? = null
    private var regImgUrl: Uri? = null
    private var insuranceImgUrl: Uri? = null
    private var fitnessImgUrl: Uri? = null
    private var permitImgUrl: Uri? = null
    private var realPath: String? = null
    private var imgType: String? = null
    val data = ArrayList<Uri>()
    private lateinit var binding: AddVehiclesFragmentStep2Binding
    private val viewModel: ManageVehicleImagesViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddVehiclesFragmentStep2Binding.bind(view)

        binding.llReg.setOnClickListener {

            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )

            imgType = "reg"
        }
        binding.llFitness.setOnClickListener {

            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )

            imgType = "fitness"
        }
        binding.llPermit.setOnClickListener {

            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )

            imgType = "permit"
        }

        binding.llInsurance.setOnClickListener {

            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )

            imgType = "insurance"
        }
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int) {

        if (checkPermissionState(permissions)) {
            Constants.showDialog(requireActivity()) {
                if (it == 1) {
                    capturePhoto()
                } else if (it == 2) {
                    captureFromGallery()
                }
            }
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    permissions,
                    requestCode
                )
            }
        }
    }

    private fun checkPermissionState(permissions: Array<String>): Boolean {
        var isPermissionGranted = true
        for (permission in permissions) {
            if (activity?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        permission
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = false
                break
            }
        }
        return isPermissionGranted
    }


    private fun captureFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultGalleryLauncher.launch(intent)
    }

    private fun capturePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val path = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "cogniwound"
        )

        if (!path.exists()) {
            path.mkdir()
        }

        val imageFile = File.createTempFile("carides", ".jpg", path)


        finalUrl = activity?.let {
            FileProvider.getUriForFile(
                it,
                requireActivity().packageName.toString() + ".provider",
                imageFile
            )
        }

        realPath = imageFile.toString()

        intent.putExtra(MediaStore.EXTRA_OUTPUT, finalUrl)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val resInfoList: List<ResolveInfo> = requireActivity().packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            requireActivity().grantUriPermission(
                packageName,
                finalUrl,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        resultLauncher.launch(intent)
    }

    private var resultGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    finalUrl = result.data?.data
                    manageImageTypes()
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    Toast.makeText(
                        activity, "Photo capture cancelled.",
                        Toast.LENGTH_LONG
                    ).show();
                }
                else -> {
                    Toast.makeText(
                        activity, "Failed to capture the photo",
                        Toast.LENGTH_LONG
                    ).show();
                }

            }
        }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    manageImageTypes()

                }
                AppCompatActivity.RESULT_CANCELED -> {
                    Toast.makeText(
                        activity, "Photo capture cancelled.",
                        Toast.LENGTH_LONG
                    ).show();
                }
                else -> {
                    Toast.makeText(
                        activity, "Failed to capture the photo",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }

    private fun manageImageTypes() {
        when (imgType) {
            "reg" -> {
                binding.icCamera.setImageURI(finalUrl)
                regImgUrl = finalUrl
                callImageUploadApi()
            }
            "fitness" -> {
                binding.imgFitness.setImageURI(finalUrl)
                fitnessImgUrl = finalUrl
                callImageUploadApi()
            }

            "permit" -> {
                binding.imgPermit.setImageURI(finalUrl)
                permitImgUrl = finalUrl
                callImageUploadApi()
            }
            "insurance" -> {
                binding.imgInsurance.setImageURI(finalUrl)
                insuranceImgUrl = finalUrl
                callImageUploadApi()
            }

        }
    }

    private fun callImageUploadApi() {
        val file = File(realPath.toString())
        var imagePartFile: MultipartBody.Part? = null
        val requestBody =
            RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        imagePartFile = MultipartBody.Part.createFormData("image[]", file.name, requestBody)
        val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
        descriptionList.add(imagePartFile)
        viewModel.imageUpload(descriptionList)
    }

    fun makeFirstStepCall(){
        //TODO remove below code after all screens done
        (activity as? VehicleDetailsActivity)?.let {
            it.changeTabs(2)
            return
        }
        //to here
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