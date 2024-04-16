package com.ciaorides.ciaorides.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateProfileRequest(
    val user_id: String,
    val first_name: String,
    val last_name: String,
    val mobile: String,
    val dob: String,
    val office_email_id: String,
    val email_id: String,
    val facebook: String,
    val instagram: String,
    val twitter: String,
    val linkedin: String,
    val bio: String,
    val gender: String,
    val alternate_number: String,
    val aadhar_card_id: String,
    val pan_card_id: String,
    val government_id: String,
    val token: String,
    val driver_license_id: String,
    val profile_pic: String,
    val driver_license_front: String,
    val driver_license_back: String,
    val government_id_front: String,
    val government_id_back: String,
    val pan_card_front: String,
    val pan_card_back: String,
    val aadhar_card_front: String,
    val aadhar_card_back: String
) : Parcelable
