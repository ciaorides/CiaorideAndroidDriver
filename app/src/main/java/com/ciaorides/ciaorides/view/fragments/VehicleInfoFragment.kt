package com.ciaorides.ciaorides.view.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.BottomSheetVehicleInfoBinding
import com.ciaorides.ciaorides.databinding.FragmentSearchHistoryListDialogBinding
import com.ciaorides.ciaorides.model.LocationsData
import com.ciaorides.ciaorides.model.request.BookRideRequest
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.InfoPopUpDialog
import com.ciaorides.ciaorides.view.activities.ui.home.RideSelection
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


class VehicleInfoFragment {

    private var selectedCar: VehicleInfoResponse.Response.Car? = null

    var vehicleClickCallBack: ((car: VehicleInfoResponse.Response.Car) -> Unit)? =
        null

    var onBookCallBack: ((request: BookRideRequest) -> Unit)? =
        null

    private var selectedVehicle: SelectedVehicle = SelectedVehicle.CAR

    lateinit var vehiclesAdapter: VehiclesAdapter

    lateinit var context: Context
    lateinit var binding: BottomSheetVehicleInfoBinding

    var sourceLatLong: LocationsData? = null
    var destinationLatLong: LocationsData? = null
    var typeOfVehicle: String = RideSelection.TAXI.name

    fun updateData(
        context: Context,
        binding: BottomSheetVehicleInfoBinding,
        vehicleData: VehicleInfoResponse,
        vehiclesAdapter: VehiclesAdapter,
        sourceLatLong: LocationsData,
        destinationLatLong: LocationsData,
        typeOfVehicle: String = RideSelection.TAXI.name
    ) {
        this.context = context
        this.binding = binding
        this.vehiclesAdapter = vehiclesAdapter
        this.sourceLatLong = sourceLatLong
        this.destinationLatLong = destinationLatLong
        this.typeOfVehicle = typeOfVehicle
        binding.rvCars.apply {
            adapter = vehiclesAdapter
        }
        if (typeOfVehicle == RideSelection.OUT_STATION.name) {
            handleOutStationViews()
            binding.cardCars.strokeColor =
                ContextCompat.getColor(context, R.color.grayLight)
        }

        vehiclesAdapter.selectedVehicle { car ->
            selectedCar = car
            selectedVehicle = SelectedVehicle.CAR
            vehicleClickCallBack?.invoke(car)
            binding.tvSeatCount.text = "0"
        }

        vehiclesAdapter.infoClickCallBack { car ->
            InfoPopUpDialog.showInfoDialog(context as Activity, car)
        }

        vehicleData.response?.let { resp ->
            if (resp.car?.isNotEmpty() == true) {
                vehiclesAdapter.differ.submitList(resp.car)
                binding.cardCars.visibility = View.VISIBLE
            } else {
                binding.cardCars.visibility = View.GONE
            }
            if (typeOfVehicle == RideSelection.TAXI.name) {
                resp.auto?.let { auto ->
                    binding.cardAuto.visibility = View.VISIBLE
                    binding.tvAutoPrice.text = auto.amount.toString()
                } ?: run {
                    binding.cardAuto.visibility = View.GONE
                }
                resp.bike?.let { bike ->
                    binding.cardBike.visibility = View.VISIBLE
                    binding.tvAutoPrice.text = bike.amount.toString()
                } ?: run {
                    binding.cardBike.visibility = View.GONE
                }
            } else {
                binding.cardBike.visibility = View.GONE
                binding.cardAuto.visibility = View.GONE
            }

        }

        binding.rvCarsMain.setOnClickListener {
            if (typeOfVehicle == RideSelection.TAXI.name) {
                if (binding.rvCars.visibility == View.VISIBLE) {
                    manageCar()
                } else {
                    hideAllCards()
                    binding.rvCars.visibility = View.VISIBLE
                    binding.ivDrop.rotation = 180f
                    binding.cardCars.strokeColor =
                        ContextCompat.getColor(context, R.color.appBlue)
                }
            }

        }

        binding.cardSchedule.setOnClickListener {

            Constants.showScheduleAlert(
                context as Activity, {

                    onBookCallBack?.let {
                        selectedCar?.let {
                            val request = BookRideRequest(
                                user_id = Constants.getValue(context, Constants.USER_ID),
                                from_lat = sourceLatLong.latLong?.latitude?.toString()!!,
                                from_lng = sourceLatLong.latLong.longitude.toString(),
                                to_lat = destinationLatLong.latLong?.latitude.toString(),
                                to_lng = destinationLatLong.latLong?.longitude.toString(),
                                from_address = sourceLatLong.address.toString(),
                                to_address = destinationLatLong.address.toString(),
                                vehicle_type = it.travel_type,
                                sub_vehicle_type = it.sub_vehicle_type,
                                gender = "men",
                                seats_required = "3",
                                ride_type = "later",
                                ride_time = "",
                                mode = "city"
                            )
                            it(request)
                        }

                    }
                }


            )
        }

        binding.ivAutoInfo.setOnClickListener {
            vehicleData.response?.auto?.let { auto ->
                InfoPopUpDialog.showInfoDialog(context as Activity, auto = auto)
            }
        }

        binding.ivInfo.setOnClickListener {
            vehicleData.response?.bike?.let { bike ->
                InfoPopUpDialog.showInfoDialog(context as Activity, bike = bike)
            }
        }

        binding.cardAuto.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.cardAuto.strokeColor =
                ContextCompat.getColor(context, R.color.appBlue)
            selectedVehicle = SelectedVehicle.AUTO
        }
        binding.cardBike.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.cardBike.strokeColor =
                ContextCompat.getColor(context, R.color.appBlue)
            selectedVehicle = SelectedVehicle.BIKE
        }

        binding.btnBookNow.setOnClickListener {
            if (selectedVehicle == SelectedVehicle.CAR && selectedCar == null) {
                Toast.makeText(context, "Please select vehicle.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onBookCallBack?.let {

                selectedCar?.let {
                    val request = BookRideRequest(
                        user_id = Constants.getValue(context, Constants.USER_ID),
                        from_lat = sourceLatLong.latLong?.latitude?.toString()!!,
                        from_lng = sourceLatLong.latLong.longitude.toString(),
                        to_lat = destinationLatLong.latLong?.latitude.toString(),
                        to_lng = destinationLatLong.latLong?.longitude.toString(),
                        from_address = sourceLatLong.address.toString(),
                        to_address = destinationLatLong.address.toString(),
                        vehicle_type = it.travel_type,
                        sub_vehicle_type = it.sub_vehicle_type,
                        gender = "men",
                        seats_required = "3",
                        ride_type = "later",
                        ride_time = "",
                        mode = "city"
                    )
                    it(request)
                }


            }
        }

    }

    private fun handleOutStationViews() {
        binding.cardBike.visibility = View.GONE
        binding.cardAuto.visibility = View.GONE
        binding.llOutStationRide.visibility = View.VISIBLE
        binding.tvBoth.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
        }
        binding.tvMale.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
        }
        binding.tvFemale.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
        }

        binding.tvAddSeats.setOnClickListener {
            if (selectedCar != null) {
                var counter = binding.tvSeatCount.text.toString().toInt()
                counter += 1
                if (selectedCar?.max_seat_capacity?.toInt()!! >= counter) {
                    binding.tvSeatCount.text = counter.toString()
                }
            }
            binding.tvRemoveSeats.setOnClickListener {
                if (selectedCar != null) {
                    var counter = binding.tvSeatCount.text.toString().toInt()
                    counter -= 1
                    if (counter >= 0) {
                        binding.tvSeatCount.text = counter.toString()
                    }
                }
            }
        }
    }

    private fun manageCar() {
        binding.rvCars.visibility = View.GONE
        binding.ivDrop.rotation = 0f
        selectedCar = null
        if (vehiclesAdapter.selectedPosition != -1) {
            val temp = vehiclesAdapter.selectedPosition
            vehiclesAdapter.selectedPosition = -1
            vehiclesAdapter.notifyItemChanged(temp)
        }
        binding.cardCars.strokeColor = ContextCompat.getColor(context, R.color.grayLight)
    }


    fun onCarClicked(listener: (VehicleInfoResponse.Response.Car) -> Unit) {
        vehicleClickCallBack = listener
    }

    fun onBookClicked(listener: (BookRideRequest) -> Unit) {
        onBookCallBack = listener
    }


    private fun hideAllCards() {
        binding.cardAuto.strokeColor = ContextCompat.getColor(context, R.color.grayLight)
        binding.cardBike.strokeColor = ContextCompat.getColor(context, R.color.grayLight)
    }

}

enum class SelectedVehicle {
    CAR,
    AUTO,
    BIKE
}