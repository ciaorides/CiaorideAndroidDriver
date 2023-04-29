package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CancelRideResponse(
    val message: String,
    val response: Boolean,
    val status: Boolean
) : Parcelable