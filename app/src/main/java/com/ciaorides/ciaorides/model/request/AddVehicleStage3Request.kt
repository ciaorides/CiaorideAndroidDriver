package com.ciaorides.ciaorides.model.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddVehicleStage3Request(
    val user_id: String,
    val vehicle_id: String,
    val vehicle_step3: String,
    val vehicle_images: List<VehicleImage>
) : Parcelable {

    @Parcelize
    data class VehicleImage(
        var image: String
    ) : Parcelable
}