package com.ciaorides.ciaorides.model.request


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class DeleteBankDetailsRequest(
    val user_id: String,
    val bank_id: String
) : Parcelable