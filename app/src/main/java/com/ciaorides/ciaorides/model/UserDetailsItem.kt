package com.ciaorides.ciaorides.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserDetailsItem(
    val email: String,
    val gender: String,
    val id: Int,
    val name: String,
    val status: String
) : Parcelable