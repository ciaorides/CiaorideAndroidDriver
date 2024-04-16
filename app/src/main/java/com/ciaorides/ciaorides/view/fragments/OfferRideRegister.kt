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
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.model.response.OfferRideVehicleInfo
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.ui.home.RideSelection
import com.ciaorides.ciaorides.view.adapter.MyVehiclesInSearchAdapter
import java.util.*


class OfferRideRegister {

    private var selectedCar: MyVehicleResponse.Response? = null
    private var selectedBike: VehicleInfoResponse.Response.Bike? = null
    private var selectedGender = Constants.BOTH

    var vehicleClickCallBack: ((car: VehicleInfoResponse.Response.Car) -> Unit)? =
        null

    var onBookCallBack: ((request: BookRideRequest, typeOfBook: BookType) -> Unit)? =
        null

    private var selectedVehicle: SelectedVehicle = SelectedVehicle.CAR

    lateinit var vehiclesAdapter: MyVehiclesInSearchAdapter

    lateinit var context: Context
    lateinit var binding: BottomSheetVehicleInfoBinding

    var sourceLatLong: LocationsData? = null
    var destinationLatLong: LocationsData? = null
    var typeOfVehicle: String = RideSelection.OUT_STATION.name

    fun updateData(
        context: Context,
        binding: BottomSheetVehicleInfoBinding,
        vehicleData: List<MyVehicleResponse.Response>,
        vehiclesAdapter: MyVehiclesInSearchAdapter,
        sourceLatLong: LocationsData,
        destinationLatLong: LocationsData,
        okCallBack: ((OfferRideVehicleInfo) -> Unit?)
    ) {
        this.context = context
        this.binding = binding
        this.vehiclesAdapter = vehiclesAdapter
        this.sourceLatLong = sourceLatLong
        this.destinationLatLong = destinationLatLong
        val carData = vehicleData.filter { (it.vehicle_type == "car") }
        val bikeData = vehicleData.filter { (it.vehicle_type == "bike") }
        if (bikeData.isNotEmpty()) {
            binding.cardBike.visibility = View.VISIBLE
        } else {
            binding.cardBike.visibility = View.GONE
        }

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
            binding.tvSeatCount.text = "0"
        }

        vehiclesAdapter.infoClickCallBack { car ->
            //  InfoPopUpDialog.showInfoDialog(context as Activity, car)
        }

        if (carData.isNotEmpty()) {
            vehiclesAdapter.differ.submitList(carData)
            binding.cardCars.visibility = View.VISIBLE
        } else {
            binding.cardCars.visibility = View.GONE
        }
        binding.cardAuto.visibility = View.GONE

        binding.rvCarsMain.setOnClickListener {
            if (binding.rvCars.visibility == View.VISIBLE) {
                manageCar()
            } else {
                hideAllCards()
                binding.rvCars.visibility = View.VISIBLE
                binding.llOutStationRide.visibility = View.VISIBLE
                binding.ivDrop.rotation = 180f
                binding.cardCars.strokeColor =
                    ContextCompat.getColor(context, R.color.appBlue)
            }

        }

        binding.btnBookNow.setOnClickListener {
            if (selectedVehicle == SelectedVehicle.BIKE) {
                Constants.showScheduleAlert(
                    context as Activity
                ) { dateTime ->
                    val dataObj = OfferRideVehicleInfo()
                    dataObj.rideTime = dateTime;
                    dataObj.amountPerHead ="500"
                    dataObj.gender = selectedGender
                    dataObj.vehicleType = selectedVehicle.name.toLowerCase()
                    dataObj.vehicleId = bikeData[0].id
                    dataObj.seatsAvailable ="1"
                    dataObj.middleSeatEmpty ="yes"
                    okCallBack.invoke(dataObj)
                    /*Constants.showBookAlert(
                        context,
                        "Your sharing ride has registered successfully"
                    ) {
                        context.finish()
                    }*/
                }
                return@setOnClickListener
            }
            if (selectedCar == null && binding.tvSeatCount.text.toString().toInt() == 0) {
                Toast.makeText(context, "Please select vehicle.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Constants.showScheduleAlert(
                context as Activity
            ) { dateTime ->
                val dataObj = OfferRideVehicleInfo()
                dataObj.rideTime = dateTime;
                dataObj.amountPerHead ="500"
                dataObj.gender = selectedGender
                dataObj.vehicleType = selectedVehicle.name.toLowerCase()
                dataObj.vehicleId = selectedCar?.id!!
                dataObj.seatsAvailable = binding.tvSeatCount.text.toString()
                dataObj.middleSeatEmpty ="yes"
                okCallBack.invoke(dataObj)
            }

        }


        binding.ivInfo.setOnClickListener {
            // InfoPopUpDialog.showInfoDialog(context as Activity, bike = bike)
        }

        binding.cardBike.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.cardBike.strokeColor =
                ContextCompat.getColor(context, R.color.appBlue)
            selectedVehicle = SelectedVehicle.BIKE
            // selectedBike = vehicleData.response?.bike
        }

    }

    private fun handleOutStationViews() {
        binding.cardAuto.visibility = View.GONE
        binding.llOutStationRide.visibility = View.VISIBLE
        binding.tvBoth.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
            selectedGender = Constants.BOTH
        }
        binding.tvMale.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg)
            selectedGender = Constants.MALE
        }
        binding.tvFemale.setOnClickListener {
            binding.tvBoth.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvMale.setBackgroundResource(R.drawable.gender_rounded_bg)
            binding.tvFemale.setBackgroundResource(R.drawable.gender_rounded_bg_selected)
            selectedGender = Constants.FE_MALE
        }
        binding.cardSchedule.visibility = View.GONE
        binding.btnBookNow.text = "Schedule Now"

        binding.tvAddSeats.setOnClickListener {
            if (selectedCar != null) {
                var counter = binding.tvSeatCount.text.toString().toInt()
                counter += 1
                if (4 >= counter) {
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
        binding.llOutStationRide.visibility = View.GONE
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
