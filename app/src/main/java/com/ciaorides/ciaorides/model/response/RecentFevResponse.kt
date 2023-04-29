package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RecentFevResponse(
    val message: String,
    val status: Boolean
) : Parcelable