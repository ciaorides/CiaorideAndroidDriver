package com.ciaorides.ciaorides.view.activities.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityImageUploadBinding
import com.ciaorides.ciaorides.di.NetworkRepository.Companion.setInterfaceInstance
import com.ciaorides.ciaorides.model.ImageUpload
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.ProfileViewModel
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File


@AndroidEntryPoint
class ImageUploadActivity() : BaseActivity<ActivityImageUploadBinding>(),
    BaseActivity.PermissionsCallBack, Parcelable, ImageUpload {

    private val viewModel: ProfileViewModel by viewModels()
    private var finalUrl: Uri? = null
    private var realPath: String? = null
    private var img1Status: Boolean? = false
    private var img2Status: Boolean? = false
    private var photosUrl: String? = null
    private var imgType: String? = null
    private var imgValue: String? = null
    val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
    override fun init() {

        setInterfaceInstance(this)
        binding = ActivityImageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgType = intent.getStringExtra(Constants.IMG_TYPE)

        binding.toolbar.tvHeader.text = imgType
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.visibility = View.VISIBLE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        binding.ivImgClose1.setOnClickListener {
            img1Status = false;
            binding.llImg1.visibility = View.GONE
            descriptionList.removeAt(0)
        }

        binding.ivImgClose2.setOnClickListener {
            img2Status = false;
            binding.llImg2.visibility = View.GONE
            descriptionList.removeAt(1)
        }
        binding.btnSave.setOnClickListener {


            if (imgType == "Driving Licence") {
                if (img1Status == true && img2Status == true) {

//                    val stringData = "1"
//                    val stringDataRequestBody: RequestBody =
//                        RequestBody.create("text/plain".toMediaTypeOrNull(), stringData)
//                    realPath?.let { it1 -> getMultipartData(it1) }
//
//                        ?.let { it2 -> viewModel.imageUpload(it2, stringDataRequestBody) }
//


                    when (imgType) {
                        "reg" -> {
                            imgValue = "6"
                        }
                        "fitness" -> {
                            imgValue = "8"
                        }
                        "permit" -> {
                            imgValue = "9"
                        }
                        "insurance" -> {
                            imgValue = "7"
                        }
                        "Driving Licence" -> {
                            imgValue = "1"
                        }
                        "Aadhar" -> {
                            imgValue = "2"
                        }
                        "Pan" -> {
                            imgValue = "3"
                        }
                        "Passport" -> {
                            imgValue = "11"
                        }
                        "vehicleImages" -> {
                            imgValue = "10"
                        }
                    }

                    val stringDataRequestBody: RequestBody =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), "1")
                    viewModel.imageUpload(descriptionList, stringDataRequestBody)
                } else
                    Toast.makeText(
                        this, getString(R.string.upload_licence),
                        Toast.LENGTH_LONG
                    ).show();

            } else {
//                val file = File(realPath.toString())
//                var imagePartFile: MultipartBody.Part? = null
//                val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//                imagePartFile = MultipartBody.Part.createFormData("image[]", file.name, requestBody)
//                descriptionList.add(imagePartFile)


                when (imgType) {
                    "reg" -> {
                        imgValue = "6"
                    }
                    "fitness" -> {
                        imgValue = "8"
                    }
                    "permit" -> {
                        imgValue = "9"
                    }
                    "insurance" -> {
                        imgValue = "7"
                    }
                    "Driving Licence" -> {
                        imgValue = "1"
                    }
                    "Aadhar" -> {
                        imgValue = "2"
                    }
                    "Pan" -> {
                        imgValue = "3"
                    }
                    "Passport" -> {
                        imgValue = "11"
                    }
                    "vehicleImages" -> {
                        imgValue = "10"
                    }
                }

                val stringDataRequestBody: RequestBody =
                    RequestBody.create("text/plain".toMediaTypeOrNull(), "1")
                viewModel.imageUpload(descriptionList, stringDataRequestBody)
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
        // handleApiResponse()
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

//        val i = Intent()
//        i.type = "image/*"
//        i.action = Intent.ACTION_GET_CONTENT
//        resultGalleryLauncher.launch(i);
        // pass the constant to compare it
        // with the returned requestCode

        // pass the constant to compare it
        // with the returned requestCode
        //  startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE)

    }

    fun getRealPathFromURI(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


//    var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result: ActivityResult ->
//        if (result.resultCode
//            == RESULT_OK
//        ) {
//            val data = result.data
//            // do your operation from here....
//            if (data != null
//                && data.data != null
//            ) {
//                val selectedImageUri = data.data
//                lateinit var selectedImageBitmap: Bitmap
//                try {
//                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(
//                        this.contentResolver,
//                        selectedImageUri
//                    )
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                imageView.setImageBitmap(
//                    selectedImageBitmap
//                )
//            }
//        }
//    }


    private var resultGalleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {


                    realPath = getRealPathFromURI(result.data?.data)
                    if (img1Status == false) {
                        binding.ivImg1.setImageURI(result.data?.data)
                        img1Status = true
                        binding.llImg1.visibility = View.VISIBLE
                    } else if (img2Status == false) {

                        binding.ivImg2.setImageURI(result.data?.data)
                        img2Status = true
                        binding.llImg2.visibility = View.VISIBLE
                    }

                    val file = File(realPath.toString())
                    var imagePartFile: MultipartBody.Part? = null
                    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    imagePartFile =
                        MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                    descriptionList.add(imagePartFile)

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
                    val file = File(realPath.toString())
                    var imagePartFile: MultipartBody.Part? = null
                    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    imagePartFile =
                        MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                    descriptionList.add(imagePartFile)

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

    override fun imageUploadResponseHanding(imageUploadResponse: Response<JsonObject>) {

        //val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()?.fromJson(Gson().toJson(imageUploadResponse), ImageUploadResponse::class.java)
        Log.d("Upload Image", imageUploadResponse.message() + "Upload successful")
        var obj = JSONObject(imageUploadResponse.body().toString())
        val arrayData = obj.getJSONObject("result_arr").getJSONArray("totalFiles")
        Log.d("Upload Image", arrayData.getJSONObject(0).getString("full_path"))

        val intent = Intent()
        intent.putExtra("result", arrayData.getJSONObject(0).getString("full_path"))
        intent.putExtra("type", imgType)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

//    private fun handleApiResponse() {
//        viewModel.imageUploadResponse.observe(this) { dataHandler ->
//            when (dataHandler) {
//                is DataHandler.SUCCESS -> {
//                    // binding.progressBar.root.visibility = View.GONE
//                }
//                is DataHandler.ERROR -> {
//                    //  binding.progressBar.root.visibility = View.GONE
//                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//
//        }
//    }
}

