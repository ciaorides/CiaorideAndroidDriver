package com.ciaorides.ciaorides.view.activities.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentHomeBinding
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.ciaorides.ciaorides.model.request.DriverCheckInRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.request.RejectRideRequest
import com.ciaorides.ciaorides.model.response.BookResp
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import com.ciaorides.ciaorides.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter
    private var vehicleSheetBehavior: BottomSheetBehavior<*>? = null
    private var _binding: FragmentHomeBinding? = null
    var googleMap: GoogleMap? = null
    private val binding get() = _binding!!

    var selectedVehicleId = ""
    var currentLatLng: LatLng? = null

    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: HomeViewModel by viewModels()
    var broadCastReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initData()
        return binding.root
    }

    private fun initData() {
        setupMap()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        checkPermissions()
        handleMyVehicle()
        handleCheckIn()
        handleCheckInStatus()
        handleRejectRideResponse()
        binding.progressLayout.root.visibility = View.VISIBLE
        viewModel.checkInStatus(
            GlobalUserIdRequest(
                driver_id = Constants.getValue(requireActivity(), Constants.USER_ID)
            )
        )
        binding.btnStart.setOnClickListener {
            vehiclesCall()
        }

        vehicleSheetBehavior = BottomSheetBehavior.from(binding.vehiclesSheet.bottomSheetLayout)
        vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.vehiclesSheet.btnStartRide.visibility = View.GONE
        binding.vehiclesSheet.btnStartRide.setOnClickListener {
            makeCheckInCall(Constants.ONLINE)
        }
        binding.searchingSheet.btnCancel.setOnClickListener {
            makeCheckInCall(Constants.OFFLINE)
        }
        binding.searchingSheet.btnPauseSearch.setOnClickListener {
            makeCheckInCall(Constants.OFFLINE)
        }

        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                Toast.makeText(activity, "Reciev", Toast.LENGTH_SHORT).show()
                updateSearchState(Constants.ONLINE)
            }
        }

        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(broadCastReceiver!!, IntentFilter(Constants.FCM_TOKEN))

    }

    private fun makeCheckInCall(state: String) {
        viewModel.checkIn(
            DriverCheckInRequest(
                check_in_status = state,
                vehicle_id = selectedVehicleId,
                from_lng = currentLatLng?.longitude.toString(),
                from_lat = currentLatLng?.latitude.toString(),
                driver_id = Constants.getValue(requireActivity(), Constants.USER_ID)
            )
        )
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun vehiclesCall() {
        if (!TextUtils.isEmpty(Constants.getValue(requireActivity(), Constants.USER_ID))) {
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.getMyVehicles(
                GlobalUserIdRequest(
                    user_id = Constants.TEMP_USER_ID
                )
            )
        }
    }

    private fun handleMyVehicle() {
        viewModel.myVehicleResponse.observe(requireActivity()) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            updateVehicles(data)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleCheckIn() {
        viewModel.checkInResponse.observe(requireActivity()) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            updateSearchState(data.otherValue)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleCheckInStatus() {
        viewModel.checkInStatusResponse.observe(requireActivity()) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            updateSearchState(data.response.status)
                            getBookingChanges()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateSearchState(otherValue: String?) {
        if (otherValue == Constants.ONLINE) {
            binding.vehiclesSheet.bottomSheetLayout.visibility = View.GONE
            binding.searchingSheet.bottomSheetLayout.visibility = View.VISIBLE
        } else {
            binding.searchingSheet.bottomSheetLayout.visibility = View.GONE
        }
    }

    private fun updateVehicles(vehicleData: MyVehicleResponse) {
        binding.vehiclesSheet.rvCars.apply {
            adapter = vehiclesAdapter
        }
        vehiclesAdapter.selectedVehicle { car ->
            selectedVehicleId = car.id
            binding.vehiclesSheet.btnStartRide.visibility = View.VISIBLE
        }
        val cars = vehicleData.response.filter {
            it.vehicle_type == "car"
        }
        if (cars.isNotEmpty()) {
            binding.vehiclesSheet.cardCars.visibility = View.VISIBLE
            vehiclesAdapter.differ.submitList(cars)
            vehiclesAdapter.selectedPosition = -1
            vehiclesAdapter.notifyDataSetChanged()
        } else {
            binding.vehiclesSheet.cardCars.visibility = View.GONE
        }
        binding.vehiclesSheet.bottomSheetLayout.visibility = View.VISIBLE
        vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        val bikes = vehicleData.response.filter {
            it.vehicle_type == "bike"
        }
        if (bikes.isNotEmpty()) {
            binding.vehiclesSheet.cardBike.visibility = View.VISIBLE
        } else {
            binding.vehiclesSheet.cardBike.visibility = View.GONE
        }

        val auto = vehicleData.response.filter {
            it.vehicle_type == "auto"
        }
        if (auto.isNotEmpty()) {
            binding.vehiclesSheet.cardAuto.visibility = View.VISIBLE
        } else {
            binding.vehiclesSheet.cardAuto.visibility = View.GONE
        }
        binding.vehiclesSheet.rvCarsMain.setOnClickListener {
            if (binding.vehiclesSheet.rvCars.visibility == View.VISIBLE) {
                manageCar()
            } else {
                selectedVehicleId = ""
                binding.vehiclesSheet.btnStartRide.visibility = View.GONE
                hideAllCards()
                binding.vehiclesSheet.rvCars.visibility = View.VISIBLE
                binding.vehiclesSheet.ivDrop.rotation = 180f
                binding.vehiclesSheet.cardCars.strokeColor =
                    ContextCompat.getColor(requireActivity(), R.color.appBlue)
            }
        }
        binding.vehiclesSheet.cardAuto.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.vehiclesSheet.cardAuto.strokeColor =
                ContextCompat.getColor(requireActivity(), R.color.appBlue)
            selectedVehicleId = auto[0].id
            binding.vehiclesSheet.btnStartRide.visibility = View.VISIBLE
        }
        binding.vehiclesSheet.cardBike.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.vehiclesSheet.cardBike.strokeColor =
                ContextCompat.getColor(requireActivity(), R.color.appBlue)
            selectedVehicleId = bikes[0].id
            binding.vehiclesSheet.btnStartRide.visibility = View.VISIBLE

        }

    }

    private fun hideAllCards() {
        binding.vehiclesSheet.cardAuto.strokeColor =
            ContextCompat.getColor(requireActivity(), R.color.grayLight)
        binding.vehiclesSheet.cardBike.strokeColor =
            ContextCompat.getColor(requireActivity(), R.color.grayLight)
    }

    private fun manageCar() {
        binding.vehiclesSheet.rvCars.visibility = View.GONE
        binding.vehiclesSheet.ivDrop.rotation = 0f
        // selectedCar = null
        if (vehiclesAdapter.selectedPosition != -1) {
            val temp = vehiclesAdapter.selectedPosition
            vehiclesAdapter.selectedPosition = -1
            vehiclesAdapter.notifyItemChanged(temp)
        }
        binding.vehiclesSheet.cardCars.strokeColor =
            ContextCompat.getColor(requireActivity(), R.color.grayLight)
    }


    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        try {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    activity,
                    R.raw.map_style
                )
            )
        } catch (e: Resources.NotFoundException) {
        }

        googleMap.setOnMapClickListener {
            //TODO Get location address and set to destination
        };
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        } else {
            getCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        val locationManager =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        val locationListener = LocationListener { location ->
            // val latitute = location.latitude
            // val longitute = location.longitude
            //  Log.i("test", "Latitute: $latitute ; Longitute: $longitute")
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )
        this.googleMap?.isMyLocationEnabled = true
        this.googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        getDeviceLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        // Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
//                        getCurrentLocation()
                        this.googleMap?.isMyLocationEnabled = true
                        this.googleMap?.uiSettings?.isMyLocationButtonEnabled = true
                        getDeviceLocation()
                    }
                } else {
                    Toast.makeText(requireActivity(), "Permission Denied", Toast.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Log.d("##Location", "getDeviceLocation")
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("##Location", "task.isSuccessful")
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        Log.d("##Location", "lastKnownLocation not null")
                        currentLatLng = LatLng(
                            lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude
                        )
                        this.googleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng!!, 12.0f
                            )
                        )

                        val marker = displayOnMarker(
                            currentLatLng!!,
                            R.drawable.ic_location,
                            title = "Your are here"
                        )

                        marker?.showInfoWindow()

                    }
                } else {
                    Log.d("TAG", "Current location is null. Using defaults.")
                    Log.e("TAG", "Exception: %s", task.exception)
                    googleMap?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), 14.0f
                            )
                    )
                    googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun displayOnMarker(
        latLng: LatLng,
        icon: Int,
        title: String = "",
        rotation: Float = 0f
    ): Marker {
        return googleMap?.addMarker(
            MarkerOptions().position(latLng)
                .icon(
                    BitmapFromVector(
                        requireContext(),
                        icon
                    )
                ).rotation(rotation)
                .title(title)
        )!!
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        broadCastReceiver?.let {
            LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(
                it
            )
        }
        _binding = null
    }

    override fun onPause() {
        super.onPause()
    }

    private fun getBookingChanges() {
        val messagesRef = Firebase.database.reference.child(FcmBookUtils.BOOKING)
            .child(Constants.getValue(requireActivity(), Constants.USER_ID)).child("bookingData")
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookRideResponse = snapshot.getValue(BookResp::class.java)
                if (bookRideResponse != null) {
                    updateRideDetails(bookRideResponse)
                } else {
                    binding.searchingSheet.bottomSheetLayout.visibility = View.VISIBLE
                    binding.localRideSheet.bottomSheetLayout.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireActivity(), "cancel Testing", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateRideDetails(bookRideResponse: BookResp) {
        binding.searchingSheet.bottomSheetLayout.visibility = View.GONE
        binding.localRideSheet.bottomSheetLayout.visibility = View.VISIBLE

        binding.localRideSheet.btnAccept.setOnClickListener {

        }
        binding.localRideSheet.btnReject.setOnClickListener {
            binding.progressLayout.root.visibility = View.VISIBLE
            viewModel.rejectRide(
                RejectRideRequest(
                    order_id = bookRideResponse.order_id.toString(),
                    driver_id = Constants.getValue(requireActivity(), Constants.USER_ID),
                    user_id = "2214"
                )
            )
        }

        /*with(binding.localRideSheet){

        }*/
    }

    private fun handleRejectRideResponse() {
        viewModel.rejectRideResponse.observe(requireActivity()) { dataHandler ->
            binding.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            binding.searchingSheet.bottomSheetLayout.visibility = View.VISIBLE
                            binding.localRideSheet.bottomSheetLayout.visibility = View.GONE
                            val bookingData =
                                Firebase.database.reference.child(FcmBookUtils.BOOKING)
                                    .child(Constants.getValue(requireActivity(), Constants.USER_ID))
                                    .child("bookingData")
                            bookingData.removeValue()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}
