package com.ciaorides.ciaorides.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetTransactionRequest(
    var rider_id : String
): Parcelable

@Parcelize
data class WithdrawTransactionRequest(
    var rider_id : String,
    var  amount: String
): Parcelable

@Parcelize
data class AddTransactionRequest(
    var rider_id : String,
    var amount: String,
    var status: Int,
    var transaction_id : String
): Parcelable