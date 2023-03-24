package com.ciaorides.ciaorides.utils


import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.ciaorides.ciaorides.databinding.DialogInfoBinding
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse

object InfoPopUpDialog {

    fun showInfoDialog(
        activity: Activity,
        car: VehicleInfoResponse.Response.Car? = null,
        bike: VehicleInfoResponse.Response.Bike? = null,
        auto: VehicleInfoResponse.Response.Auto? = null,

        ) {

        val builder = AlertDialog.Builder(activity)
            .create()
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val infoBinding = DialogInfoBinding.inflate(LayoutInflater.from(activity), null, false)

        builder.setView(infoBinding.root)
        with(infoBinding) {
            car?.let {
                txtVehicleTitle.text = car.travel_type
                txtTotalAmount.text = "Rs " + car.total_amount.toString()
                txtAmount.text = "Rs " + car.amount.toString()
                txtTax.text = "Rs " + car.tax.toString()
                txtCommision.text = "Rs " + car.ciao_commission.toString()
                txtCancelCharge.text = "Rs " + car.cancellation_charges.toString()
                txtPerHead.text = "Rs " + car.amount_per_head.toString()
                txtBaseFare.text = "Rs " + car.base_fare.toString()
                txtPerSeat.text = "Rs " + car.per_seat_amount.toString()
                txtSeatCapacity.text = car.max_seat_capacity.toString()
                txtDistance.text = car.distance.toString()
                txtVehicleType.text = car.sub_vehicle_type.toString()
            }
            bike?.let { bike ->
                txtVehicleTitle.text = bike.vehicle_type
                txtTotalAmount.text = "Rs " + bike.total_amount.toString()
                txtAmount.text = "Rs " + bike.amount.toString()
                txtTax.text = "Rs " + bike.tax.toString()
                txtCommision.text = "Rs " + bike.ciao_commission.toString()
                txtCancelCharge.text = "Rs " + bike.cancellation_charges.toString()
                txtPerHead.text = "Rs " + bike.amount_per_head.toString()
                txtBaseFare.text = "Rs " + bike.base_fare.toString()
                txtPerSeat.text = "Rs " + bike.per_seat_amount.toString()
                txtSeatCapacity.text = bike.max_seat_capacity.toString()
                txtDistance.text = bike.distance.toString()
                txtVehicleType.text = bike.sub_vehicle_type.toString()
            }

            auto?.let { auto ->
                txtVehicleTitle.text = auto.vehicle_type
                txtTotalAmount.text = "Rs " + auto.total_amount.toString()
                txtAmount.text = "Rs " + auto.amount.toString()
                txtTax.text = "Rs " + auto.tax.toString()
                txtCommision.text = "Rs " + auto.ciao_commission.toString()
                txtCancelCharge.text = "Rs " + auto.cancellation_charges.toString()
                txtPerHead.text = "Rs " + auto.amount_per_head.toString()
                txtBaseFare.text = "Rs " + auto.base_fare.toString()
                txtPerSeat.text = "Rs " + auto.per_seat_amount.toString()
                txtSeatCapacity.text = auto.max_seat_capacity.toString()
                txtDistance.text = auto.distance.toString()
                txtVehicleType.text = auto.sub_vehicle_type.toString()
            }
        }

        builder.show()
    }
}