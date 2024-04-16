package com.ciaorides.ciaorides.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RazorPayResponse(
    val status: Boolean,
    val message: String,
    val response: RazorPayObject
): Parcelable {

    @Parcelize
    data class RazorPayObject(
        val razorpay_id: String,
        val razor_key: String,
        val razor_secret_key: String
    ) : Parcelable
}

