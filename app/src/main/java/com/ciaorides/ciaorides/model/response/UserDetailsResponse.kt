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
        var aadhar_card_back: String,
        var aadhar_card_front: String,
        var aadhar_card_id: String,
        var aadhar_card_verified: String,
        var address1: String,
        var address2: String,
        var address_verified: String,
        var alternate_number: String,
        var bio: String,
        var city_id: String,
        var country_id: String,
        var created_on: String,
        var delete_status: String,
        var dob: String,
        var doc_updated: String,
        var driver_license_back: String,
        var driver_license_front: String,
        var driver_license_id: String,
        var driver_license_verified: String,
        var email_id: String,
        var email_id_verified: String,
        var facebook: String,
        var first_name: String,
        var gender: String,
        var government_id: String,
        var government_id_back: String,
        var government_id_front: String,
        var government_id_verified: String,
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
        var pan_card_front: String,
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
        var token: String,
        val twitter: String,
        val user_type: String,
        val userid: String
    ) : Parcelable
}