package com.ciaorides.ciaorides.view.activities.user

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityProfileBinding
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.BaseActivity

class ProfileActivity : BaseActivity<ActivityProfileBinding>() {
    override fun getViewBinding(): ActivityProfileBinding =
        ActivityProfileBinding.inflate(layoutInflater)

    override fun init() {
        updateToolBar(binding.toolbar.ivBadge)
        binding.toolbar.ivProfileImage.visibility = View.GONE
        binding.toolbar.ivBadge.visibility = View.GONE
        binding.toolbar.ivEdit.visibility = View.VISIBLE
        binding.toolbar.tvHeader.text = getString(R.string.profile)
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        val userData =
            intent.getParcelableExtra(Constants.DATA_VALUE) as? UserDetailsResponse.Response
        userData?.let {
            with(binding) {
                tvDob.text = userData.dob
                tvEmail.text = userData.email_id
                tvGender.text = userData.gender
                tvAlternativeMobile.text = userData.alternate_number
                tvMobile.text = userData.mobile
                var address = ""
                if (!TextUtils.isEmpty(userData.address1)) {
                    address = userData.address1
                }
                if (!TextUtils.isEmpty(userData.address2)) {
                    address = "," + userData.address2
                }
                if (!TextUtils.isEmpty(userData.postcode)) {
                    address = "," + userData.postcode
                }
                tvAddress.text = address
                tvInstagram.text = userData.instagram
                tvFacebook.text = userData.facebook
                tvLinkedIn.text = userData.linkedin
                var fullName = userData.first_name
                if (!TextUtils.isEmpty(userData.first_name)) {
                    fullName = fullName + " " + userData.last_name
                }
                tvName.text = fullName

                if (userData.aadhar_card_verified != "no") {
                    tvDrivingVerify.text = getString(R.string.verified)
                    tvDrivingVerify.setBackgroundColor(Color.WHITE)
                    tvDrivingVerify.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.green
                        )
                    )
                }
                if (userData.pan_card_verified != "no") {
                    tvPanVerfy.text = getString(R.string.verified)
                    tvPanVerfy.setBackgroundColor(Color.WHITE)
                    tvPanVerfy.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.green
                        )
                    )
                }
                if (userData.aadhar_card_verified != "no") {
                    tvAadhar.text = getString(R.string.verified)
                    tvAadhar.setBackgroundColor(Color.WHITE)
                    tvAadhar.setTextColor(ContextCompat.getColor(applicationContext, R.color.green))
                }
            }

        }
    }


}