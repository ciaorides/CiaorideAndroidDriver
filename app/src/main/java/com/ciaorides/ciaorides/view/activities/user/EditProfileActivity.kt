package com.ciaorides.ciaorides.view.activities.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEditProfileBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.UpdateProfileRequest
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.utils.showDateAlert
import com.ciaorides.ciaorides.utils.visible
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(),
    BaseActivity.PermissionsCallBack {
    private var finalUrl: Uri? = null
    private var realPath: String? = null
    private var token: String? = null
    private val PHOTO_CAPTURE = 101
    private var userData: UserDetailsResponse.Response? = null;

    private val viewModel: ProfileViewModel by viewModels()

    override fun getViewBinding(): ActivityEditProfileBinding =
        ActivityEditProfileBinding.inflate(layoutInflater)

    override fun init() {
        binding.toolbar.ivProfileImage.visibility = View.GONE
        binding.toolbar.ivBadge.visibility = View.GONE
        binding.toolbar.ivEdit.visibility = View.VISIBLE
        binding.toolbar.tvHeader.text = getString(R.string.profile)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        binding.personalInfo.edtDOB.setOnClickListener {
            showDateAlert(this@EditProfileActivity, "Select DOB",) {
                binding.personalInfo.edtDOB.setText(
                    Constants.getFormattedDob(
                        Constants.YYYY_MM_DD,
                        Constants.DD_MMM_YYYY,
                        it
                    )
                )
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
            with(binding.personalInfo) {
                edtName.setText(data.first_name)
                edtEmail.setText(data.email_id)
                edtMobile.setText(data.mobile)
                etBio.setText(data.bio)
                setGenderData(data.gender)
                edtDOB.setText(data.dob)
            }
            with(binding.addressInfo) {
                edtAddress1.setText(data.address1)
                edtAddress2.setText(data.address2)
                edtPincode.setText(data.postcode)
            }
            with(binding.socialMedia) {
                edtFacebook.setText(data.facebook)
                edtInstagram.setText(data.instagram)
                edtLinkedin.setText(data.linkedin)
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
                btnUploadPassport.text = resources.getString(R.string.upload_image)
                btnUploadPassport.background.setTint(resources.getColor(R.color.appBlue))

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

        binding.idVerification.btnUploadPassport.setOnClickListener {
            val intent = Intent(this, ImageUploadActivity::class.java)
            intent.putExtra(Constants.IMG_TYPE, "Passport")
            uploadedImgPath.launch(intent)
        }

        binding.btnSubmit.setOnClickListener {
            binding.progressLayout.root.visible(true)
            viewModel.updateUserProfile(
                UpdateProfileRequest(
                    user_id = Constants.getValue(this@EditProfileActivity, Constants.USER_ID),
                    first_name = binding.personalInfo.edtName.text.toString(),
                    last_name = binding.personalInfo.edtName.text.toString(),
                    mobile = binding.personalInfo.edtMobile.text.toString(),
                    dob = if (TextUtils.isEmpty(binding.personalInfo.edtDOB.text.toString())) {
                        ""
                    } else {
                        Constants.getFormattedDob(
                            Constants.DD_MMM_YYYY,
                            Constants.YYYY_MM_DD,
                            binding.personalInfo.edtDOB.text.toString()
                        )
                    },
                    office_email_id = binding.personalInfo.edtEmail.text.toString(),
                    email_id = binding.personalInfo.edtEmail.text.toString(),
                    facebook = binding.socialMedia.edtFacebook.text.toString(),
                    instagram = binding.socialMedia.edtInstagram.text.toString(),
                    twitter = binding.socialMedia.edtLinkedin.text.toString(),
                    linkedin = binding.socialMedia.edtLinkedin.text.toString(),
                    bio = binding.personalInfo.etBio.text.toString(),
                    gender = getSelectedGender(),
                    alternate_number = binding.personalInfo.edtMobile.text.toString(),
                    aadhar_card_id = userData!!.aadhar_card_id,
                    pan_card_id = userData!!.pan_card_id,
                    Government_id = userData!!.government_id,
                    token = token.toString(),
                    driver_license_id = userData!!.driver_license_id,
                    profile_pic = userData!!.profile_pic,
                    driver_license_front = userData!!.driver_license_front,
                    driver_license_back = userData!!.driver_license_back,
                    government_id_front = userData!!.government_id_front,
                    government_id_back = userData!!.government_id_back,
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

    private fun getSelectedGender(): String {
        with(binding.personalInfo) {
            if (rbMale.isChecked) {
                return getString(R.string.male)
            } else if (rbFemale.isChecked) {
                return getString(R.string.female)
            } else {
                return getString(R.string.other)
            }
        }
    }

    private fun checkPermission(permissions: Array<String>, requestCode: Int) {

        if (checkPermissionState(permissions)) {
            capturePhoto()
        } else {
            ActivityCompat.requestPermissions(
                this@EditProfileActivity,
                permissions,
                requestCode
            )
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
            val value = it.data?.getStringExtra("result")
            val imgType = it.data?.getStringExtra("type")
            //  val value = it.data?.getStringExtra("result")
            when (imgType) {
                "Driving Licence" -> {
                    userData?.driver_license_front = value.toString()
                    binding.idVerification.txtDlName.text = value
                }
                "Aadhar" -> {
                    userData?.aadhar_card_front = value.toString()
                    binding.idVerification.txtAdharName.text = value
                }
                "Pan" -> {
                    userData?.pan_card_front = value.toString()
                    binding.idVerification.txtPANName.text = value
                }
                "Passport" -> {
                    binding.idVerification.txtPassportName.text = value
                }
            }

        }
    }
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    binding.ivProfileImage.setImageURI(finalUrl)
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
            binding.progressLayout.root.visible(false)
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT)
                                .show()
                            userData = data.response
                            userData?.let { data ->
                                with(binding.personalInfo) {
                                    edtName.setText(data.first_name)
                                    edtEmail.setText(data.email_id)
                                    edtMobile.setText(data.mobile)
                                    etBio.setText(data.bio)
                                    setGenderData(data.gender)
                                    edtDOB.text = Constants.getFormattedDob(
                                        Constants.YYYY_MM_DD,
                                        Constants.DD_MMM_YYYY,
                                        data.dob,
                                    )
                                }
                                with(binding.addressInfo) {
                                    edtAddress1.setText(data.address1)
                                    edtAddress2.setText(data.address2)
                                    edtPincode.setText(data.postcode)
                                }
                                with(binding.socialMedia) {
                                    edtFacebook.setText(data.facebook)
                                    edtInstagram.setText(data.instagram)
                                    edtLinkedin.setText(data.linkedin)
                                }
                                with(binding.idVerification) {
                                    if (data.driver_license_front.length > 10) {
                                        btnUploadDL.text = "Verified"
                                        btnUploadDL.background.setTint(resources.getColor(R.color.green))
                                    } else {
                                        btnUploadDL.text =
                                            resources.getString(R.string.upload_image)
                                        btnUploadDL.background.setTint(resources.getColor(R.color.appBlue))
                                    }
                                    if (data.pan_card_front.length > 10) {
                                        btnUploadPAN.text = "Verified"
                                        btnUploadPAN.background.setTint(resources.getColor(R.color.green))
                                    } else {
                                        btnUploadPAN.text =
                                            resources.getString(R.string.upload_image)
                                        btnUploadPAN.background.setTint(resources.getColor(R.color.appBlue))
                                    }
                                    if (data.aadhar_card_front.length > 10) {
                                        btnUploadAdhar.text = "Verified"
                                        btnUploadAdhar.background.setTint(resources.getColor(R.color.green))
                                    } else {
                                        btnUploadAdhar.text =
                                            resources.getString(R.string.upload_image)
                                        btnUploadAdhar.background.setTint(resources.getColor(R.color.appBlue))
                                    }
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
            }
        }
    }

    private fun setGenderData(gender: String?) {
        if (gender != null) {
            if (gender.toLowerCase() == "male") {
                binding.personalInfo.rbMale.isChecked = true
            } else if (gender.toLowerCase() == "female") {
                binding.personalInfo.rbFemale.isChecked = true
            } else {
                binding.personalInfo.rbOther.isChecked = true
            }
        }

    }


    fun uploadImage() {

    }
}


