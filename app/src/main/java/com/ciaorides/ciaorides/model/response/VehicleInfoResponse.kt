package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable


@Parcelize
data class VehicleInfoResponse(
    val distance: String,
    val message: String,
    val response: Response? = null,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val auto: Auto? = null,
        val bike: Bike? = null,
        val car: List<Car>? = null
    ) : Parcelable {
        @Parcelize
        data class Auto(
            val amount: Int,
            val amount_per_head: Int,
            val base_fare: Int,
            val cancellation_charges: Int,
            val ciao_commission: Int,
            val distance: String,
            val max_seat_capacity: String,
            val payment_gateway_commision: Int,
            val per_seat_amount: Int,
            val sub_vehicle_type: String,
            val tax: Int,
            val total_amount: Int,
            val vehicle_type: String
        ) : Parcelable

        @Parcelize
        data class Bike(
            val amount: Int,
            val amount_per_head: Int,
            val base_fare: Int,
            val cancellation_charges: Int,
            val ciao_commission: Int,
            val distance: String,
            val max_seat_capacity: String,
            val payment_gateway_commision: Int,
            val per_seat_amount: Int,
            val sub_vehicle_type: String,
            val tax: Int,
            val total_amount: Int,
            val vehicle_type: String
        ) : Parcelable

        @Parcelize
        data class Car(
            val amount: Int,
            val amount_per_head: Int,
            val base_fare: Int,
            val cancellation_charges: Int,
            val ciao_commission: Int,
            val distance: String,
            val max_seat_capacity: String,
            val payment_gateway_commision: Int,
            val per_seat_amount: Int,
            val sub_vehicle_type: String,
            val tax: Int,
            val total_amount: Int,
            val travel_type: String,
            var isSelected: Boolean
        ) : Parcelable
    }
}