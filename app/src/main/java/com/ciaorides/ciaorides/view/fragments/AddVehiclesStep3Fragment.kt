package com.ciaorides.ciaorides.view.fragments

import android.Manifest
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
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.adapter.VehicleImagesAdapter
import com.ciaorides.ciaorides.viewmodel.ManageVehicleImagesViewModel
import com.google.gson.JsonObject
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
    private var imgType: String? = null
    val data = ArrayList<Uri>()
    private lateinit var binding: AddVehiclesFragmentStep3Binding
    private val viewModel: ManageVehicleImagesViewModel by viewModels()
    val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddVehiclesFragmentStep3Binding.bind(view)
        imagesAdapter = VehicleImagesAdapter(data)
        binding.rviewVehicleImgs.adapter = imagesAdapter
        binding.rviewVehicleImgs.layoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL,
            false
        )

        binding.icCamera3.setOnClickListener {
            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )
            imgType = "vehicleImages"
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
//        val i = Intent()
//        i.type = "image/*"
//        i.action = Intent.ACTION_GET_CONTENT
//        resultGalleryLauncher.launch(i);

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

//    private var resultGalleryLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            when (result.resultCode) {
//                AppCompatActivity.RESULT_OK -> {
//                    finalUrl = result.data?.data
//                    manageImageTypes()
//                }
//                AppCompatActivity.RESULT_CANCELED -> {
//                    Toast.makeText(
//                        activity, "Photo capture cancelled.",
//                        Toast.LENGTH_LONG
//                    ).show();
//                }
//                else -> {
//                    Toast.makeText(
//                        activity, "Failed to capture the photo",
//                        Toast.LENGTH_LONG
//                    ).show();
//                }
//
//            }
//        }

    fun getRealPathFromURI(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = requireActivity().managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


    private var resultGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    finalUrl = result.data?.data
                    // manageImageTypes()
                    data.add(finalUrl!!)
                    // binding.rviewVehicleImgs.adapter = imagesAdapter
                    imagesAdapter.notifyItemChanged(data.size)
                    realPath = getRealPathFromURI(result.data?.data)
                    val file = File(realPath.toString())
                    var imagePartFile: MultipartBody.Part? = null
                    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    imagePartFile =
                        MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                    descriptionList.add(imagePartFile)

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
            "vehicleImages" -> {
                data.add(finalUrl!!)
                // binding.rviewVehicleImgs.adapter = imagesAdapter
                imagesAdapter.notifyItemChanged(data.size)
                //callImageUploadApi()
                val file = File(realPath.toString())
                var imagePartFile: MultipartBody.Part? = null
                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                imagePartFile =
                    MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                descriptionList.add(imagePartFile)
//                val file = File(realPath.toString())
//                var imagePartFile: MultipartBody.Part? = null
//                val requestBody =
//                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
//                imagePartFile = MultipartBody.Part.createFormData("image[]", file.name, requestBody)
//
//                descriptionList.add(imagePartFile)
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
        // viewModel.imageUpload(descriptionList)
    }

    companion object {
        fun newInstance(): AddVehiclesStep3Fragment {
            val fragment = AddVehiclesStep3Fragment()
            return fragment
        }
    }

    fun makeFirstStepCall() {
        val stringData = "1"
        val requestBody: RequestBody =
            RequestBody.create("text/plain".toMediaTypeOrNull(), stringData)

        viewModel.vehicleImageUpload(descriptionList, requestBody)
    }


}



