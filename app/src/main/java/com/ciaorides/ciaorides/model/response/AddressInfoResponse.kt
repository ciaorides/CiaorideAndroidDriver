package com.ciaorides.ciaorides.model.response


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressInfoResponse(
    val results: List<Result> = ArrayList<Result>(),
    val status: String
) : Parcelable {

    @Parcelize
    data class Result(
        val formatted_address: String,
    ) : Parcelable

}