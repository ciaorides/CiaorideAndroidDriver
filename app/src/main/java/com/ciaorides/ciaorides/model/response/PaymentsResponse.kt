package com.ciaorides.ciaorides.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class PaymentsResponse(
    val status: Boolean,
    val message: String,
    val response: PaymentsData
) : Parcelable {
    @Parcelize
    data class PaymentsData(
        var
        data: List<BookingData>? = null,
        val final_data: FinalDataObject,
        val payment_final: PaymentFinalObject
    ) : Parcelable

    @Parcelize
    data class BookingData(
        val Booking_Date: String,
        val Booking_Time : String,
        val Final_Amount: String,
        val booking_id:String
    ) : Parcelable

    @Parcelize
    data class FinalDataObject(
        val online: String,
        val total_trips : String
    ) : Parcelable

    @Parcelize
    data class PaymentFinalObject(
        val payment_id: String,
        val completed_date : String,
        val payment_status : String,
        val upi_id : String,
        val status : String,
        val amount : String
    ) : Parcelable
}
