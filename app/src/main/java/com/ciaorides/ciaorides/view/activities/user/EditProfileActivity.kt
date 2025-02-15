package com.ciaorides.ciaorides.view.activities.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ciaorides.ciaorides.BuildConfig
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEditProfileBinding
import com.ciaorides.ciaorides.di.NetworkRepository.Companion.setInterfaceInstance
import com.ciaorides.ciaorides.model.ImageUpload
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.UpdateProfileRequest
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.utils.DateUtils
import com.ciaorides.ciaorides.utils.ImageUtils
import com.ciaorides.ciaorides.utils.showImageDialog
import com.ciaorides.ciaorides.utils.visible
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.ProfileViewModel
import com.google.gson.JsonObject
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(),
    BaseActivity.PermissionsCallBack,ImageUpload {
    private var selectedDate: Long? = null
    private var finalUrl: Uri? = null
    private var realPath: String? = null
    private var token: String? = null
    private val PHOTO_CAPTURE = 101
    private var userData: UserDetailsResponse.Response? = null;
    private var isDatePickerOpen: Boolean = false
    private val genderList = listOf("Select Your Gender","Male", "Female", "Other","Don't Want to Specify")
    private var userProfileURL: String? = null
    val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
    private val viewModel: ProfileViewModel by viewModels()

    //  private val viewModel1: HomeViewModel by viewModels()
    override fun getViewBinding(): ActivityEditProfileBinding =
        ActivityEditProfileBinding.inflate(layoutInflater)

    override fun init() {
        registerListener()
        setGenderListAdapter()
        setInterfaceInstance(this)
        binding.toolbar.ivProfileImage.visibility = View.GONE
        binding.toolbar.ivBadge.visibility = View.GONE
        binding.toolbar.ivEdit.visibility = View.VISIBLE
        binding.toolbar.tvHeader.text = getString(R.string.profile)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        binding.idVerification.apply {
            dlFront.setOnClickListener {
                showImageDialog(this@EditProfileActivity, this.dlFront.drawable)
            }
            dlBack.setOnClickListener {
                showImageDialog(this@EditProfileActivity, this.dlBack.drawable)
            }
            ivPanFront.setOnClickListener {
                showImageDialog(this@EditProfileActivity, this.ivPanFront.drawable)
            }
            ivPanBack.setOnClickListener {
                showImageDialog(this@EditProfileActivity, this.ivPanBack.drawable)
            }
            ivAadhaarFront.setOnClickListener {
                showImageDialog(this@EditProfileActivity, this.ivAadhaarFront.drawable)
            }
            ivAadhaarBack.setOnClickListener {
                showImageDialog(this@EditProfileActivity, this.ivAadhaarBack.drawable)
            }
        }
        userData =
            intent.getParcelableExtra(Constants.DATA_VALUE) as? UserDetailsResponse.Response
        token =
            applicationContext.getSharedPreferences(Constants.MAIN_PREF, MODE_PRIVATE)
                .getString(Constants.FCM_TOKEN, "").toString()
        if (!TextUtils.isEmpty(Constants.getValue(this@EditProfileActivity, Constants.USER_ID))) {
            viewModel.getUserDetails(
                GlobalUserIdRequest(
                    user_id = Constants.getValue(this@EditProfileActivity, Constants.USER_ID)
                )
            )
        }
        userData?.let { data ->
            val a=data.gender.replaceFirstChar{it.toUpperCase()}
            val genderPosition = genderList.indexOf(a)
            with(binding.personalInfo) {
                edtName.setText(data.first_name)
                edtEmail.setText(data.email_id)
                edtMobile.setText(data.mobile)
                etBio.setText(data.bio)
                ediGender.setSelection(if(genderPosition<0)0  else genderPosition)
                edtDOB.text = data.dob
            }
            with(binding.addressInfo) {
                edtAddress1.setText(data.address1)
                edtAddress2.setText(data.address2)
                edtPincode.setText(data.postcode)
            }
            with(binding.idVerification) {
                if (data.driver_license_front.length > 10) {
                    btnUploadDL.text = "Verified"
                    btnUploadDL.background.setTint(resources.getColor(R.color.green))
                } else {
                    btnUploadDL.text = resources.getString(R.string.upload_image)
                    btnUploadDL.background.setTint(resources.getColor(R.color.appBlue))
                }
                if (data.pan_card_front.length > 10) {
                    btnUploadPAN.text = "Verified"
                    btnUploadPAN.background.setTint(resources.getColor(R.color.green))
                } else {
                    btnUploadPAN.text = resources.getString(R.string.upload_image)
                    btnUploadPAN.background.setTint(resources.getColor(R.color.appBlue))
                }
                if (data.aadhar_card_front.length > 10) {
                    btnUploadAdhar.text = "Verified"
                    btnUploadAdhar.background.setTint(resources.getColor(R.color.green))
                } else {
                    btnUploadAdhar.text = resources.getString(R.string.upload_image)
                    btnUploadAdhar.background.setTint(resources.getColor(R.color.appBlue))
                }
            }

        }
        permission = this
        handleUserResponse()

        binding.idVerification.btnUploadDL.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, "Driving Licence")
            uploadedImgPath.launch(intent)
        }

        binding.idVerification.btnUploadAdhar.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, "Aadhar")
            uploadedImgPath.launch(intent)
        }

        binding.idVerification.btnUploadPAN.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, "Pan")
            uploadedImgPath.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            val userMailID=binding.personalInfo.edtEmail.text.toString()
            val isEmailIdValid =
                Patterns.EMAIL_ADDRESS.matcher(userMailID)
                    .matches()
            if (userMailID.isNotBlank()&& !isEmailIdValid){
                Toast.makeText(this, "Email ID is Incorrect", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.updateUserProfile(
                UpdateProfileRequest(
                    user_id = Constants.getValue(this@EditProfileActivity, Constants.USER_ID),
                    first_name = binding.personalInfo.edtName.text.toString(),
                    last_name = binding.personalInfo.edtName.text.toString(),
                    mobile = binding.personalInfo.edtMobile.text.toString(),
                    dob = binding.personalInfo.edtDOB.text.toString(),
                    office_email_id = userMailID,
                    email_id = userMailID,
                    bio = binding.personalInfo.etBio.text.toString(),
                    gender = binding.personalInfo.ediGender.selectedItem.toString().toLowerCase(),
                    alternate_number = binding.personalInfo.edtMobile.text.toString(),
                    aadhar_card_id = userData!!.aadhar_card_id,
                    pan_card_id = userData!!.pan_card_id,
                    token = token.toString(),
                    driver_license_id = userData!!.driver_license_id,
                    profile_pic = userProfileURL.toString(),
                    driver_license_front = userData!!.driver_license_front,
                    driver_license_back = userData!!.driver_license_back,
                    pan_card_front = userData!!.pan_card_front,
                    pan_card_back = userData!!.pan_card_back,
                    aadhar_card_front = userData!!.aadhar_card_front,
                    aadhar_card_back = userData!!.aadhar_card_back
                )
            )
        }
        binding.rvProfile.setOnClickListener {
            checkPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun registerListener() {
        binding.personalInfo.edtDOB.setOnClickListener {
            pickDate()
        }
        binding.personalInfo.ediGender.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    Log.d(TAG, "onItemSelected: Gender::${genderList[position]}")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int) {

        if (checkPermissionState(false)) {
            capturePhoto()
        } else {
            /*ActivityCompat.requestPermissions(
                this@EditProfileActivity,
                permissions,
                requestCode
            )*/
            TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.MANAGE_MEDIA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
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

    private var uploadedImgPath = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val value = it.data?.getStringExtra("path")
            val imgType = it.data?.getStringExtra("type")
            val imageFront = it.data?.getStringExtra(Constants.FRONT)
            val imageBack = it.data?.getStringExtra(Constants.BACK)
            //  val value = it.data?.getStringExtra("result")
            when (imgType) {
                "Driving Licence" -> {
                    binding.idVerification.llDrivingLicence.visibility=View.VISIBLE
                    binding.idVerification.txtDlName.text = value.toString()
                    userData?.driver_license_front = imageFront ?: ""
                    userData?.driver_license_back = imageBack ?: ""
                    Constants.showGlide(this,BuildConfig.IMAGE_BASE_URL+imageFront, binding.idVerification.dlFront)
                    Constants.showGlide(this, BuildConfig.IMAGE_BASE_URL+imageBack, binding.idVerification.dlBack)
                }

                "Aadhar" -> {
                    binding.idVerification.llAadhaar.visibility=View.VISIBLE
                    binding.idVerification.txtAdharName.text = value.toString()
                    userData?.aadhar_card_front = imageFront ?: ""
                    userData?.aadhar_card_back = imageBack ?: ""
                    Constants.showGlide(this, BuildConfig.IMAGE_BASE_URL+imageFront, binding.idVerification.ivAadhaarFront)
                    Constants.showGlide(this, BuildConfig.IMAGE_BASE_URL+imageBack, binding.idVerification.ivAadhaarBack)
                }

                "Pan" -> {
                    binding.idVerification.llPanCard.visibility=View.VISIBLE
                    binding.idVerification.txtPANName.text = value.toString()
                    userData?.pan_card_front = imageFront ?: ""
                    userData?.pan_card_back = imageBack ?: ""
                    Constants.showGlide(this, BuildConfig.IMAGE_BASE_URL+imageFront, binding.idVerification.ivPanFront)
                    Constants.showGlide(this, BuildConfig.IMAGE_BASE_URL+imageBack, binding.idVerification.ivPanBack)
                }
            }

        }
    }
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    binding.progressLayout.root.visible(true)
                    val file = File(realPath.toString())
                    var imagePartFile: MultipartBody.Part? = null
                    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                    imagePartFile =
                        MultipartBody.Part.createFormData("image[]", file.name, requestBody)
                    descriptionList.add(imagePartFile)

                    val stringDataRequestBody: RequestBody =
                        RequestBody.create("text/plain".toMediaTypeOrNull(), "4")
                    viewModel.imageUpload(descriptionList, stringDataRequestBody)
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

    override fun onPermissionGranted() {
        capturePhoto()
    }

    private fun handleUserResponse() {
        viewModel.userDetailsResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            userData = data.response
                            userData?.let { data ->
                                val a=data.gender.replaceFirstChar{it.toUpperCase()}
                                val genderPosition = genderList.indexOf(a)
                                userProfileURL = data.profile_pic
                                Constants.saveValue(
                                    this,
                                    Constants.USER_IMAGE,
                                    data.profile_pic
                                )
                                Constants.showGlide(
                                    this,
                                    BuildConfig.IMAGE_BASE_URL+data.profile_pic,
                                    binding.ivProfilePhoto,
                                    applyCircleCrop = true
                                )
                                if (data.driver_license_front.isNotBlank()) {
                                    binding.idVerification.llDrivingLicence.visibility =
                                        View.VISIBLE
                                    Constants.showGlide(
                                        this,
                                        BuildConfig.IMAGE_BASE_URL+data.driver_license_front,
                                        binding.idVerification.dlFront,
                                    )
                                    Constants.showGlide(
                                        this,
                                        BuildConfig.IMAGE_BASE_URL+data.driver_license_back,
                                        binding.idVerification.dlBack,
                                    )
                                }
                                if (data.pan_card_front.isNotBlank()) {
                                    binding.idVerification.llPanCard.visibility =
                                        View.VISIBLE
                                    Constants.showGlide(
                                        this,
                                        BuildConfig.IMAGE_BASE_URL+data.pan_card_front,
                                        binding.idVerification.ivPanFront,
                                    )
                                    Constants.showGlide(
                                        this,
                                        BuildConfig.IMAGE_BASE_URL+data.pan_card_back,
                                        binding.idVerification.ivPanBack,
                                    )
                                }
                                if (data.aadhar_card_front.isNotBlank()) {
                                    binding.idVerification.llAadhaar.visibility =
                                        View.VISIBLE
                                    Constants.showGlide(
                                        this,
                                        BuildConfig.IMAGE_BASE_URL+data.aadhar_card_front,
                                        binding.idVerification.ivAadhaarFront,
                                    )
                                    Constants.showGlide(
                                        this,
                                        BuildConfig.IMAGE_BASE_URL+data.aadhar_card_back,
                                        binding.idVerification.ivAadhaarBack,
                                    )
                                }
                                with(binding.personalInfo) {
                                    edtName.setText(data.first_name)
                                    edtEmail.setText(data.email_id)
                                    edtMobile.setText(data.mobile)
                                    etBio.setText(data.bio)
                                    ediGender.setSelection(if(genderPosition<0)0  else genderPosition)
                                    edtDOB.text = data.dob
                                }
                                with(binding.addressInfo) {
                                    edtAddress1.setText(data.address1)
                                    edtAddress2.setText(data.address2)
                                    edtPincode.setText(data.postcode)
                                }
                                with(binding.idVerification) {
                                    if (data.driver_license_verified.equals("yes", true)) {
                                        btnUploadDL.text = getString(R.string.verified)
                                        btnUploadDL.background.setTint(resources.getColor(R.color.green))
                                        btnUploadDL.isClickable=false
                                    } else if (data.driver_license_front.isNotBlank()&& data.driver_license_verified.equals("no", true)) {
                                        btnUploadDL.text = getString(R.string.pending)
                                        btnUploadDL.background.setTint(resources.getColor(R.color.green))
                                    } else {
                                        btnUploadDL.text =
                                            resources.getString(R.string.upload_image)
                                        btnUploadDL.background.setTint(resources.getColor(R.color.appBlue))
                                    }

                                    if (data.pan_card_verified.equals("yes", true)) {
                                        btnUploadPAN.text = getString(R.string.verified)
                                        btnUploadPAN.background.setTint(resources.getColor(R.color.green))
                                        btnUploadPAN.isClickable=false
                                    } else if (data.pan_card_front.isNotBlank()&&data.pan_card_verified.equals("no", true)) {
                                        btnUploadPAN.text = getString(R.string.pending)
                                        btnUploadPAN.background.setTint(resources.getColor(R.color.green))
                                    } else {
                                        btnUploadPAN.text =
                                            resources.getString(R.string.upload_image)
                                        btnUploadPAN.background.setTint(resources.getColor(R.color.appBlue))
                                    }

                                    if (data.aadhar_card_verified.equals("yes", true)) {
                                        btnUploadAdhar.text = getString(R.string.verified)
                                        btnUploadAdhar.background.setTint(resources.getColor(R.color.green))
                                        btnUploadAdhar.isClickable=false
                                    } else if (data.aadhar_card_front.isNotBlank()&&data.aadhar_card_verified.equals("no", true)) {
                                        btnUploadAdhar.text = getString(R.string.pending)
                                        btnUploadAdhar.background.setTint(resources.getColor(R.color.green))
                                    } else {
                                        btnUploadAdhar.text =
                                            resources.getString(R.string.upload_image)
                                        btnUploadAdhar.background.setTint(resources.getColor(R.color.appBlue))
                                    }
                                    txtDlName.text = data.driver_license_front
                                    txtPANName.text= data.pan_card_front
                                    txtAdharName.text= data.aadhar_card_front
                                }

                            }
                        } else Toast.makeText(
                            applicationContext,
                            data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }
        }
    }


    fun uploadImage() {

    }
    private fun pickDate() {
        val mDatePicker = DateUtils.datePicker(selectedDate)
        if (!isDatePickerOpen) {
            isDatePickerOpen = true
            mDatePicker.show(
                supportFragmentManager, ""
            )
            mDatePicker.addOnPositiveButtonClickListener { selection ->
                selectedDate= selection
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val selectedDate: String = dateFormat.format(Date(selection))
                binding.personalInfo.edtDOB.text = selectedDate
                isDatePickerOpen = false
            }
            mDatePicker.addOnCancelListener {
                isDatePickerOpen = false
            }
            mDatePicker.addOnNegativeButtonClickListener {
                isDatePickerOpen = false
            }
        }
    }

    private fun setGenderListAdapter() {
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderList)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.personalInfo.ediGender.adapter = genderAdapter
    }

    override fun imageUploadResponseHanding(imageUploadResponse: Response<JsonObject>?) {
        binding.progressLayout.root.visible(false)
        if (imageUploadResponse == null) return
        //val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()?.fromJson(Gson().toJson(imageUploadResponse), ImageUploadResponse::class.java)
        Log.d("Upload Image", imageUploadResponse.message() + "Upload successful")
        val obj = JSONObject(imageUploadResponse.body().toString())
        val arrayData = obj.getJSONObject("result_arr").getJSONArray("totalFiles")
        Log.d("Upload Image", arrayData.getJSONObject(0).getString("full_path"))
        userProfileURL = arrayData.getJSONObject(0).getString("file_path_url").toString()
        if (userProfileURL.toString().isNotEmpty()) {

            Constants.showGlide(
                binding.ivProfilePhoto.context,
                BuildConfig.IMAGE_BASE_URL + userProfileURL,
                binding.ivProfilePhoto,
                applyCircleCrop = true
            )
        }
    }

    override fun onRestart() {
        super.onRestart()
        setInterfaceInstance(this)
    }

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}



