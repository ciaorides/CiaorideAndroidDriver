package com.ciaorides.ciaorides.model.response


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyVehicleResponse(
    val message: String,
    val response: List<Response>,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val car_type: String,
        val color: String,
        val country: String,
        val created_on: String,
        val fitness_certification_image: String,
        val fitness_certification_number: String,
        val id: String,
        val make_id: String,
        val model_id: String,
        val number_plate: String,
        val status: String,
        val user_id: String,
        val vehicle_images: List<VehicleImage>,
        val vehicle_insurance_image: String,
        val vehicle_insurance_number: String,
        val vehicle_makes: List<VehicleMake>,
        val vehicle_permit_image: String,
        val vehicle_permit_number: String,
        val vehicle_picture: String,
        val vehicle_registration_image: String,
        val vehicle_type: String,
        val vehicle_step1: String,
        val vehicle_step2: String,
        val vehicle_step3: String,
        val vehicle_verified:String,
        val year: String
    ) : Parcelable {
        @Parcelize
        data class VehicleImage(
            val created_date: String,
            val id: String,
            val user_id: String,
            val vehicle_id: String,
            val vehicle_image: String
        ) : Parcelable

        @Parcelize
        data class VehicleMake(
            val created_on: String,
            val id: String,
            val modified_on: String,
            val title: String,
            val vehicle_type: String
        ) : Parcelable
    }
}