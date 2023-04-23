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
        val account_holder_name: String?=null,
        val account_number: String?=null,
        val bank_name: String?=null,
        val country_id: String?=null,
        val created_on: String?=null,
        val id: String?=null,
        val ifsc_code: String?=null,
        val user_id: String?=null
    ) : Parcelable
}