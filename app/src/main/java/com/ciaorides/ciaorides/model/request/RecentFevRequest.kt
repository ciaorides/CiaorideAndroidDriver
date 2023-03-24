package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
 data class RecentFevRequest(
    val address: String,
    val lat: String,
    val lng: String,
    val mode: String,
    val type: String,
    val user_id: Int
) : Parcelable