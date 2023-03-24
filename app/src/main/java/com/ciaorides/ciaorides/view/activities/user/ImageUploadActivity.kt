package com.ciaorides.ciaorides.view.activities.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityImageUploadBinding
import com.ciaorides.ciaorides.model.request.ImageUploadRequest
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.activities.OtpActivity
import com.ciaorides.ciaorides.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@AndroidEntryPoint
class ImageUploadActivity() : BaseActivity<ActivityImageUploadBinding>(),
    BaseActivity.PermissionsCallBack, Parcelable {

    private val viewModel: ProfileViewModel by viewModels()
    private var finalUrl: Uri? = null
    private var realPath: String? = null
    private var img1Status: Boolean? = false
    private var img2Status: Boolean? = false
    override fun init() {


        binding = ActivityImageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imgType = intent.getStringExtra(Constants.IMG_TYPE)

        binding.toolbar.tvHeader.text = imgType
        binding.toolbar.profileView.visibility =View.GONE
        binding.toolbar.ivMenu.visibility = View.VISIBLE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        binding.ivImgClose1.setOnClickListener {
            img1Status = false;
            binding.llImg1.visibility = View.GONE
        }

        binding.ivImgClose2.setOnClickListener {
            img2Status = false;
            binding.llImg2.visibility = View.GONE
        }
        binding.btnSave.setOnClickListener {


            if (imgType == "Driving Licence") {
                if (img1Status == true && img2Status == true) {

                    val file = File(realPath.toString())
                    var imagePartFile: MultipartBody.Part? = null
                    val requestBody =
                        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                    imagePartFile = MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                    val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
                    descriptionList.add(imagePartFile)
                    viewModel.imageUpload(descriptionList)

//                    viewModel.imageUpload(
//                        ImageUploadRequest(
//                            upload_type = "1",
//                            image = imageList
//                        )
//                    )

                  /*  val result = finalUrl.toString()
                    val intent = Intent()
                    intent.putExtra("result", result)
                    intent.putExtra("type", imgType)
                    setResult(Activity.RESULT_OK, intent)
                    finish()*/

                } else
                    Toast.makeText(
                        this, getString(R.string.upload_licence),
                        Toast.LENGTH_LONG
                    ).show();

            } else {
                /*val result = finalUrl.toString()
                val intent = Intent()
                intent.putExtra("result", result)
                intent.putExtra("type", imgType)
                setResult(Activity.RESULT_OK, intent)
                finish()*/
                val file = File(realPath.toString())
                var imagePartFile: MultipartBody.Part? = null
                val requestBody =
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                imagePartFile = MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
                descriptionList.add(imagePartFile)
                viewModel.imageUpload(descriptionList)
            }
        }
        binding.icCamera.setOnClickListener {
            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )
        }
        handleApiResponse()
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int) {

        if (checkPermissionState(permissions)) {
            Constants.showDialog(this) {
                if (it == 1) {
                    capturePhoto()
                } else if (it == 2) {
                    captureFromGallery()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                requestCode
            )
        }
    }

    private fun captureFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultGalleryLauncher.launch(intent)
    }

    private var resultGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    finalUrl = result.data?.data
                    if (img1Status == false) {
                        binding.ivImg1.setImageURI(finalUrl)
                        img1Status = true
                        binding.llImg1.visibility = View.VISIBLE
                    } else if (img2Status == false) {

                        binding.ivImg2.setImageURI(finalUrl)
                        img2Status = true
                        binding.llImg2.visibility = View.VISIBLE
                    }
                }
                RESULT_CANCELED -> {
                    Toast.makeText(
                        this, "Photo capture cancelled.",
                        Toast.LENGTH_LONG
                    ).show();
                }
                else -> {
                    Toast.makeText(
                        this, "Failed to capture the photo",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
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


        finalUrl = FileProvider.getUriForFile(
            applicationContext,
            applicationContext.packageName.toString() + ".provider",
            imageFile
        )

        realPath = imageFile.toString()

        intent.putExtra(MediaStore.EXTRA_OUTPUT, finalUrl)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val resInfoList: List<ResolveInfo> = packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                finalUrl,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {

                    if (img1Status == false) {
                        binding.ivImg1.setImageURI(finalUrl)
                        img1Status = true
                        binding.llImg1.visibility = View.VISIBLE
                    } else if (img2Status == false) {

                        binding.ivImg2.setImageURI(finalUrl)
                        img2Status = true
                        binding.llImg2.visibility = View.VISIBLE
                    }
                    //  binding.icCamera.setImageURI(finalUrl)
                }
                RESULT_CANCELED -> {
                    Toast.makeText(
                        this, "Photo capture cancelled.",
                        Toast.LENGTH_LONG
                    ).show();
                }
                else -> {
                    Toast.makeText(
                        this, "Failed to capture the photo",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }

    constructor(parcel: Parcel) : this() {
        finalUrl = parcel.readParcelable(Uri::class.java.classLoader)
        realPath = parcel.readString()
        img1Status = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        img2Status = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun onPermissionGranted() {
        capturePhoto()
    }

    override fun getViewBinding(): ActivityImageUploadBinding =
        ActivityImageUploadBinding.inflate(layoutInflater)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(finalUrl, flags)
        parcel.writeString(realPath)
        parcel.writeValue(img1Status)
        parcel.writeValue(img2Status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageUploadActivity> {
        override fun createFromParcel(parcel: Parcel): ImageUploadActivity {
            return ImageUploadActivity(parcel)
        }

        override fun newArray(size: Int): Array<ImageUploadActivity?> {
            return arrayOfNulls(size)
        }
    }

    private fun handleApiResponse() {
        viewModel.imageUploadResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                   // binding.progressBar.root.visibility = View.GONE
                }
                is DataHandler.ERROR -> {
                  //  binding.progressBar.root.visibility = View.GONE
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }
}