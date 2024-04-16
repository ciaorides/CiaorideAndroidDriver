package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class GlobalUserIdRequest(
    val user_id: String,
    val user_type: String? = ""
) : Parcelable