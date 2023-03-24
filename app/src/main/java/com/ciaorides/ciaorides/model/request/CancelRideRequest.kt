package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CancelRideRequest(
    val booking_id: String,
    val user_id: String
) : Parcelable