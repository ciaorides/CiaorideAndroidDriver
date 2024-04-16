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
            val amount: String,
            val amount_per_head: Int,
            val base_fare: String,
            val ride_charges: String,
            val ciao_commission: String,
            val convenience_charges: String,
            val distance: String,
            val max_seat_capacity: String,
            val payment_gateway_commision: Int,
            val per_seat_amount: Int,
            val sub_vehicle_type: String,
            val sub_vehicle_type_id: String?=null,
            val tax: Int,
            val total_amount: String,
            val vehicle_type: String
        ) : Parcelable

        @Parcelize
        data class Bike(
            val amount: String,
            val amount_per_head: Int,
            val base_fare: String,
            val ride_charges: String,
            val ciao_commission: String,
            val convenience_charges: String,
            val distance: String,
            val max_seat_capacity: String,
            val payment_gateway_commision: Int,
            val per_seat_amount: Int,
            val sub_vehicle_type: String = "",
            val sub_vehicle_type_id: String = "",
            val tax: Int,
            val total_amount: String,
            val vehicle_type: String
        ) : Parcelable

        @Parcelize
        data class Car(
            val amount: String,
            val amount_per_head: Int,
            val base_fare: String,
            val ride_charges: String,
            val ciao_commission: String,
            val convenience_charges: String,
            val distance: String,
            val max_seat_capacity: String,
            val payment_gateway_commision: Int,
            val per_seat_amount: Int,
            val sub_vehicle_type: String,
            val sub_vehicle_type_id: String,
            val tax: Int,
            val total_amount: String,
            val travel_type: String,
            var isSelected: Boolean
        ) : Parcelable
    }
}