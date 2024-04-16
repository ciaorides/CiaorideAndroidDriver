package com.ciaorides.ciaorides.model.response

import java.io.Serializable

data class EarningsResponse(

    val status: Boolean,
    val message: String,
    val response: resultsData,
    val payments: List<PaymentGraphData>,
    val total_amount : String
) : Serializable {
    data class resultsData(
        val resultdata1: List<EarningsData>,
        val resultdata2 : PaymentObject
    ):Serializable

    data class FinalObject(
        val online : String,
        val total_trips : Int
    ):Serializable

    data class PaymentObject(
        val final_data: FinalObject,
        val payment_pending : PaymentData,
        val payment_completed : PaymentData
    ):Serializable

    data class PaymentData(
        val payment_id : String,
        val completed_date : String,
        val payment_status : String,
        val upi_id : String,
        val status : String,
        val amount : String
    ): Serializable
    data class EarningsData(
        val Ride_Date: String,
        val Ride_Time: String,
        val Final_Amout: String
    ) : Serializable

    data class PaymentGraphData(
        val day: String,
        val total_amount : String
    ) : Serializable
}



