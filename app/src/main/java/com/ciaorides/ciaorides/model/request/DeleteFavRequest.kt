package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DeleteFavRequest(
    val favourite_id: String,
    val user_id: String
) : Parcelable