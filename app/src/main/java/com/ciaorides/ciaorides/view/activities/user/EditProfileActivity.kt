package com.ciaorides.ciaorides.view.activities.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityEditProfileBinding
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.ProfileViewModel
import java.io.File


class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>(),
    BaseActivity.PermissionsCallBack {
    private var finalUrl: Uri? = null
    private var realPath: String? = null
    private val PHOTO_CAPTURE = 101

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
        val userData =
            intent.getParcelableExtra(Constants.DATA_VALUE) as? UserDetailsResponse.Response
        userData?.let { data ->
            with(binding.personalInfo) {
                edtName.setText(data.first_name)
                edtEmail.setText(data.email_id)
                edtMobile.setText(data.mobile)
                etBio.setText(data.bio)
                ediGender.setText(data.gender)
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

        }
        permission = this




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
                    binding.idVerification.txtDlName.text = value
                }
                "Aadhar" -> {
                    binding.idVerification.txtAdharName.text = value
                }
                "Pan" -> {
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

    fun uploadImage() {

    }
}


