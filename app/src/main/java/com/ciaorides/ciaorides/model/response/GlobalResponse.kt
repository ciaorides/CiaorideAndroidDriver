package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class GlobalResponse(
    val message: String,
    val status: Boolean,
    var otherValue: String? = ""
) : Parcelable