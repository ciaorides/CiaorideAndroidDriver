package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserDetailsResponse(
    val message: String,
    val response: Response,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val aadhar_card_back: String,
        val aadhar_card_front: String,
        val aadhar_card_id: String,
        val aadhar_card_verified: String,
        val address1: String,
        val address2: String,
        val address_verified: String,
        val alternate_number: String,
        val bio: String,
        val city_id: String,
        val country_id: String,
        val created_on: String,
        val delete_status: String,
        val dob: String,
        val doc_updated: String,
        val driver_license_back: String,
        val driver_license_front: String,
        val driver_license_id: String,
        val driver_license_verified: String,
        val email_id: String,
        val email_id_verified: String,
        val facebook: String,
        val first_name: String,
        val gender: String,
        val government_id: String,
        val government_id_back: String,
        val government_id_front: String,
        val government_id_verified: String,
        val id: String,
        val instagram: String,
        val ios_token: String,
        val last_name: String,
        val lattitude: String,
        val linkedin: String,
        val longitude: String,
        val mobile: String,
        val mobile_verified: String,
        val modified_on: String,
        val office_email_id: String,
        val office_email_id_verified: String,
        val pan_card_back: String,
        val pan_card_front: String,
        val pan_card_id: String,
        val pan_card_verified: String,
        val password: String,
        val payment_mode: String,
        val photo_verified: String,
        val postcode: String,
        val profile_percentage: Int,
        val profile_pic: String,
        val ratings: String,
        val rides_posted: String,
        val state_id: String,
        val status: String,
        val token: String,
        val twitter: String,
        val user_type: String,
        val userid: String
    ) : Parcelable
}