package com.ciaorides.ciaorides.view.fragments

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.BottomSheetVehicleInfoBinding
import com.ciaorides.ciaorides.model.LocationsData
import com.ciaorides.ciaorides.model.request.BookRideRequest
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.InfoPopUpDialog
import com.ciaorides.ciaorides.utils.getCurrentTime
import com.ciaorides.ciaorides.view.activities.ui.home.RideSelection
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import java.util.*


class VehicleInfoFragment(val listener: (Int) -> Unit) {

    private var selectedCar: VehicleInfoResponse.Response.Car? = null
    private var selectedAuto: VehicleInfoResponse.Response.Auto? = null
    private var selectedBike: VehicleInfoResponse.Response.Bike? = null

    var vehicleClickCallBack: ((car: VehicleInfoResponse.Response.Car) -> Unit)? =
        null

    var onBookCallBack: ((request: BookRideRequest, typeOfBook: BookType) -> Unit)? =
        null

    private var selectedVehicle: SelectedVehicle = SelectedVehicle.CAR

    lateinit var vehiclesAdapter: VehiclesAdapter

    lateinit var context: Context
    lateinit var binding: BottomSheetVehicleInfoBinding

    var sourceLatLong: LocationsData? = null
    var destinationLatLong: LocationsData? = null
    var typeOfVehicle: String = RideSelection.TAXI.name

    var selectedGender = GENDER.BOTH
    var seatsCount = "3"

    fun updateData(
        context: Context,
        binding: BottomSheetVehicleInfoBinding,
        vehicleData: VehicleInfoResponse,
        vehiclesAdapter: VehiclesAdapter,
        sourceLatLong: LocationsData,
        destinationLatLong: LocationsData,
        typeOfVehicle: String,
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
                    binding.tvAutoPrice.text = auto.total_amount.toString()
                } ?: run {
                    binding.cardAuto.visibility = View.GONE
                }
                resp.bike?.let { bike ->
                    binding.cardBike.visibility = View.VISIBLE
                    binding.tvPrice.text = bike.total_amount.toString()
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
            callCardSchedule()
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
            selectedAuto = vehicleData.response?.auto
        }
        binding.cardBike.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.cardBike.strokeColor =
                ContextCompat.getColor(context, R.color.appBlue)
            selectedVehicle = SelectedVehicle.BIKE
            selectedBike = vehicleData.response?.bike
        }

        binding.btnBookNow.setOnClickListener {
            if (typeOfVehicle == RideSelection.OUT_STATION.name) {
                callCardSchedule()
            } else {
                if (selectedVehicle == SelectedVehicle.AUTO) {
                    onBookCallBack?.let {
                        selectedAuto?.let {
                            it(
                                getAutoRequestObject(
                                    selectedAuto!!, getCurrentTime(),
                                    BookType.NOW.name.lowercase(Locale.getDefault())
                                ), BookType.NOW
                            )
                        }
                    }
                    return@setOnClickListener
                } else if (selectedVehicle == SelectedVehicle.BIKE) {
                    onBookCallBack?.let {
                        selectedBike?.let {
                            it(
                                getBikeRequestObject(
                                    selectedBike!!, getCurrentTime(),
                                    BookType.NOW.name.lowercase(Locale.getDefault())
                                ), BookType.NOW
                            )
                        }
                    }
                    return@setOnClickListener
                }
                if (selectedVehicle == SelectedVehicle.CAR && selectedCar == null) {
                    Toast.makeText(context, "Please select vehicle.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onBookCallBack?.let {
                    selectedCar?.let {
                        it(
                            getCarRequestObject(
                                selectedCar!!, getCurrentTime(),
                                BookType.NOW.name.lowercase(Locale.getDefault())
                            ), BookType.NOW
                        )
                    }
                }
            }
        }
    }

    private fun callCardSchedule() {
        if (selectedVehicle == SelectedVehicle.AUTO) {
            Constants.showScheduleAlert(
                context as Activity
            ) { dateTime ->
                onBookCallBack?.let {
                    selectedAuto?.let {
                        it(
                            getAutoRequestObject(
                                selectedAuto!!,
                                dateTime,
                                BookType.LATER.name.lowercase(Locale.getDefault())
                            ), BookType.LATER
                        )
                    }
                }
            }
            return
        } else if (selectedVehicle == SelectedVehicle.BIKE) {
            Constants.showScheduleAlert(
                context as Activity
            ) { dateTime ->
                onBookCallBack?.let {
                    selectedBike?.let {
                        it(
                            getBikeRequestObject(
                                selectedBike!!,
                                dateTime,
                                BookType.LATER.name.lowercase(Locale.getDefault())
                            ), BookType.LATER
                        )
                    }
                }
            }
            return
        }
        if (selectedVehicle == SelectedVehicle.CAR && selectedCar == null) {
            Toast.makeText(context, "Please select vehicle.", Toast.LENGTH_SHORT).show()
            return
        }
        Constants.showScheduleAlert(
            context as Activity
        ) { dateTime ->
            onBookCallBack?.let {
                selectedCar?.let {
                    it(
                        getCarRequestObject(
                            selectedCar!!,
                            dateTime,
                            BookType.LATER.name.lowercase(Locale.getDefault())
                        ), BookType.LATER
                    )
                }
            }
        }
    }

    private fun getCarRequestObject(
        selectedCar: VehicleInfoResponse.Response.Car,
        time: String,
        type: String
    ) =
        BookRideRequest(
            user_id = Constants.getValue(context, Constants.USER_ID),
            from_lat = sourceLatLong?.latLong?.latitude?.toString()!!,
            from_lng = sourceLatLong?.latLong!!.longitude.toString(),
            to_lat = destinationLatLong?.latLong?.latitude.toString(),
            to_lng = destinationLatLong?.latLong?.longitude.toString(),
            from_address = sourceLatLong?.address.toString(),
            to_address = destinationLatLong?.address.toString(),
            vehicle_type = selectedCar.travel_type,
            sub_vehicle_type = selectedCar.sub_vehicle_type_id,
            gender = selectedGender.name,
            seats_required = seatsCount,
            ride_type = type,
            ride_time = time,
            mode = if (typeOfVehicle == RideSelection.OUT_STATION.name) "outstation" else "city",
            user_type = "driver"
        )

    private fun getAutoRequestObject(
        vehicle: VehicleInfoResponse.Response.Auto,
        time: String,
        type: String
    ) =
        BookRideRequest(
            user_id = Constants.getValue(context, Constants.USER_ID),
            from_lat = sourceLatLong?.latLong?.latitude?.toString()!!,
            from_lng = sourceLatLong?.latLong!!.longitude.toString(),
            to_lat = destinationLatLong?.latLong?.latitude.toString(),
            to_lng = destinationLatLong?.latLong?.longitude.toString(),
            from_address = sourceLatLong?.address.toString(),
            to_address = destinationLatLong?.address.toString(),
            vehicle_type = "auto",
            sub_vehicle_type = vehicle.sub_vehicle_type_id ?: "",
            gender = "men",
            seats_required = "3",
            ride_type = type,
            ride_time = time,
            mode = if (typeOfVehicle == RideSelection.OUT_STATION.name) "outstation" else "city",
            user_type = "driver"
        )

    private fun getBikeRequestObject(
        vehicle: VehicleInfoResponse.Response.Bike,
        time: String,
        type: String
    ) =
        BookRideRequest(
            user_id = Constants.getValue(context, Constants.USER_ID),
            from_lat = sourceLatLong?.latLong?.latitude?.toString()!!,
            from_lng = sourceLatLong?.latLong!!.longitude.toString(),
            to_lat = destinationLatLong?.latLong?.latitude.toString(),
            to_lng = destinationLatLong?.latLong?.longitude.toString(),
            from_address = sourceLatLong?.address.toString(),
            to_address = destinationLatLong?.address.toString(),
            vehicle_type = "bike",
            sub_vehicle_type = vehicle.sub_vehicle_type,
            gender = "men",
            seats_required = "2",
            ride_type = type,
            ride_time = time,
            mode = if (typeOfVehicle == RideSelection.OUT_STATION.name) "outstation" else "city",
            user_type = "driver"
        )


    private fun handleOutStationViews() {
        binding.cardBike.visibility = View.GONE
        binding.cardAuto.visibility = View.GONE
        binding.llOutStationRide.visibility = View.VISIBLE
        binding.tvBoth.setOnClickListener {
            selectedGender = GENDER.BOTH
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
        }
        binding.tvMale.setOnClickListener {
            selectedGender = GENDER.MALE
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
        }
        binding.tvFemale.setOnClickListener {
            selectedGender = GENDER.FEMALE
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
                    seatsCount = counter.toString()
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
        listener.invoke(binding.rvCars.height)
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

    fun onBookClicked(listener: (BookRideRequest, BookType) -> Unit) {
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

enum class BookType {
    NOW,
    LATER,
}

enum class GENDER {
    BOTH,
    MALE,
    FEMALE,
}