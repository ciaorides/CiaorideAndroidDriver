package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class BankDetailsResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val account_holder_name: String,
        val account_number: String,
        val bank_name: String,
        val country_id: String,
        val created_on: String,
        val id: String,
        val ifsc_code: String,
        val user_id: String
    ) : Parcelable
}