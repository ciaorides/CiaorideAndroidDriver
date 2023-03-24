package com.ciaorides.ciaorides.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityMainSearchBinding
import com.ciaorides.ciaorides.model.LocationsData
import com.ciaorides.ciaorides.model.MapData
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.response.BookRideResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.*
import com.ciaorides.ciaorides.utils.Constants.showAlert
import com.ciaorides.ciaorides.view.activities.chat.ChatViewActivity
import com.ciaorides.ciaorides.view.activities.ui.home.RideSelection
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import com.ciaorides.ciaorides.view.fragments.*
import com.ciaorides.ciaorides.viewmodel.SearchViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject


@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivityMainSearchBinding>() {
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private val viewModel: SearchViewModel by viewModels()
    var resultReceiver: ResultReceiver? = null
    var sourceLatLong: LocationsData? = null
    var destinationLatLong: LocationsData? = null
    var googleMap: GoogleMap? = null
    private lateinit var actSource: AutocompleteSupportFragment
    private lateinit var actDestination: AutocompleteSupportFragment

    private var selectedCar: VehicleInfoResponse.Response.Car? = null
    private var bookProgressSheetBehavior: BottomSheetBehavior<*>? = null
    private var fevSheetBehavior: BottomSheetBehavior<*>? = null
    private var vehicleSheetBehavior: BottomSheetBehavior<*>? = null
    private var bookResultSheetBehavior: BottomSheetBehavior<*>? = null
    private var sharingVehicleSheetBehavior: BottomSheetBehavior<*>? = null
    private var sheetAvailabilitySheetBehavior: BottomSheetBehavior<*>? = null
    private var bookedFragment: BookRideProgressFragment? = null
    private var sharingFragment: SharingFragment? = null
    var isFirst = true
    var bookingId = ""

    var selectedRideType = RideSelection.TAXI.name

    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter

    private var mapBottomMargin = 0
    private var childDataHeight = 0


    override fun init() {
        intent.getStringExtra(Constants.RIDE_TYPE)?.let {
            selectedRideType = it
        }
        resultReceiver = AddressResultReceiver(Handler(Looper.getMainLooper()))

        // Construct a PlacesClient
        Places.initialize(applicationContext, getString(R.string.google_api_key1))
        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupMap()
        initData()
        checkPermissions()
        makeRecentRequest()
        getPrevInfo()
        getVehicleInfo()
        initSourcePlacesApi()
        initDestinationPlacesApi()
        binding.layoutSearch.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }
        addFevObserver()
        handleBookRideCall()
        cancelObserver()
        handlePlacesAddressCall()
        showBottomSheet()
        addSharingAvailabilityObserver()
        binding.bookedSheet.btnCancel.setOnClickListener {
            showAlert(this@SearchActivity, getString(R.string.cancel_ride_msg), "", true) {
                makeCancelRideCall()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun makeCancelRideCall() {
        if (!TextUtils.isEmpty(Constants.getValue(this@SearchActivity, Constants.USER_ID))) {
            binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
            viewModel.cancelRide(
                CancelRideRequest(
                    user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID),
                    booking_id = bookingId
                )
            )
        }

    }

    private fun addFevObserver() {
        viewModel.recentFevResponse.observe(this) {
            Toast.makeText(
                this@SearchActivity,
                "Location added to your favourites.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun addSharingAvailabilityObserver() {
        viewModel.availabilityResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.response.isNotEmpty()) {
                            //TODO
                            sharingVehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                            mapBottomMargin = binding.sheetAvailability.bottomSheetLayout.height
                            sheetAvailabilitySheetBehavior?.state =
                                BottomSheetBehavior.STATE_EXPANDED
                            val availabilityDrivers = AvailabilityDrivers()
                            availabilityDrivers.updateData(
                                binding.sheetAvailability,
                                dataHandler.data.response,
                                4
                            )
                        }

                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun handlePlacesAddressCall() {
        viewModel.locationInfo.observe(this) { response ->
            sourceLatLong?.let {
                if (response.results.isNotEmpty()) {
                    it.address = response.results[0].formatted_address
                    actSource.setText(it.address)
                    if (isFirst) {
                        callApi()
                        isFirst = false
                    }

                }
            }
        }
    }

    private fun handleBookRideCall() {
        viewModel.bookRideResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.response.isNotEmpty()) {
                            updateAfterBookData(data.response[0])
                        }

                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    private fun cancelObserver() {
        viewModel.cancelRideResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.layoutSearch.progressLayout.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        Toast.makeText(applicationContext, data.message, Toast.LENGTH_SHORT).show()
                        resetData()
                    }
                }
                is DataHandler.ERROR -> {
                    binding.layoutSearch.progressLayout.root.visibility = View.GONE
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }
    }

    private fun resetData() {
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }

    private fun updateAfterBookData(response: BookRideResponse.Response) {
        bookingId = response.id
        binding.bookedSheet.tvCurrentLocation.text = sourceLatLong!!.address
        binding.bookedSheet.tvDestination.text = destinationLatLong!!.address
        binding.bookedSheet.tvVehicleNumber.text = response.number_plate
        binding.bookedSheet.tvName.text = response.first_name
        binding.bookedSheet.tvVehicleType.text = response.vehicle_model
        binding.bookedSheet.tvChat.setOnClickListener {
            val intent = Intent(this, ChatViewActivity::class.java)
            startActivity(intent)
        }


        bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        mapBottomMargin = binding.bookedSheet.bottomSheetLayout.height
        bookResultSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED


    }

    private fun initSourcePlacesApi() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key1))
        }
        actSource =
            supportFragmentManager.findFragmentById(R.id.actSource) as AutocompleteSupportFragment
        actSource!!.setPlaceFields(
            listOf(

                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL

            )
        )
        actSource.setCountries("IN")
        actSource.setHint(getString(R.string.your_location))
        actSource.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)?.visibility =
            View.GONE
        val etSource =
            actSource.view?.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        etSource?.typeface =
            Typeface.createFromAsset(assets, Constants.FONT_INTER_REG)
        etSource?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        etSource?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text.isNullOrEmpty()) {
                    sourceLatLong = null
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }

        })
        actSource.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                actSource.setText(place.address)
                sourceLatLong = LocationsData(place.latLng, place.address)
                if (sourceLatLong != null && destinationLatLong != null) {
                    callApi()

                }
            }

            override fun onError(status: Status) {
                actSource.setText("")
            }
        })

    }

    private fun initDestinationPlacesApi() {
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key1))
        }
        actDestination =
            supportFragmentManager.findFragmentById(R.id.actDestination) as AutocompleteSupportFragment

        actDestination!!.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL,
            )
        )
        actDestination.setCountries("IN")
        actDestination.setHint(getString(R.string.search_for_destination))
        actDestination.view?.findViewById<View>(com.google.android.libraries.places.R.id.places_autocomplete_search_button)?.visibility =
            View.GONE
        val etSource =
            actDestination.view?.findViewById<EditText>(com.google.android.libraries.places.R.id.places_autocomplete_search_input)
        etSource?.typeface =
            Typeface.createFromAsset(assets, Constants.FONT_INTER_REG)
        etSource?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        etSource?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (text.isNullOrEmpty()) {
                    destinationLatLong = null
                }
            }

            override fun afterTextChanged(editable: Editable?) {

            }

        })
        actDestination.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                destinationLatLong = LocationsData(place.latLng, place.address)
                if (sourceLatLong != null && destinationLatLong != null) {
                    callApi()
                }
                addToApi(
                    place.latLng.latitude.toString(),
                    place.latLng.longitude.toString(),
                    place.address,
                    "recent"
                )
            }

            override fun onError(status: Status) {
                actDestination.setText("")
            }
        })

    }

    private fun makeRecentRequest() {
        if (!TextUtils.isEmpty(Constants.getValue(this@SearchActivity, Constants.USER_ID))) {
            viewModel.makeRecentRequest(
                RecentSearchRequest(
                    user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID).toInt(),
                    type = TempConstants.TYPE,
                    mode = TempConstants.MODE,
                    from_lat = TempConstants.FROM_LAT,
                    from_lng = TempConstants.FROM_LAT,
                )
            )
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this@SearchActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SearchActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@SearchActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@SearchActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        } else {
            getCurrentLocation()
        }
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
                            this@SearchActivity,
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
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun initData() {
        binding.layoutSearch.toolbar.tvHeader.text = getString(R.string.search)
        binding.layoutSearch.ivInterChange.setOnClickListener {
            interChangeLocations()
        }
        binding.layoutSearch.ivCurrentLocationFev.setOnClickListener {
            sourceLatLong?.address?.let {
                Constants.showFevAlert(this@SearchActivity, it, {
                    sourceLatLong?.let { data ->
                        addToApi(
                            data.latLong?.latitude.toString(),
                            data.latLong?.longitude.toString(),
                            data.address.toString(),
                            "favorite"
                        )
                    }
                }, {
                    sourceLatLong?.let { data ->
                        addToApi(
                            data.latLong?.latitude.toString(),
                            data.latLong?.longitude.toString(),
                            data.address.toString(),
                            "favorite"
                        )
                    }
                })

            }
        }
        binding.layoutSearch.ivDestinationLocationFev.setOnClickListener {
            destinationLatLong?.address?.let {
                Constants.showFevAlert(this@SearchActivity, it, {
                    sourceLatLong?.let { data ->
                        addToApi(
                            data.latLong?.latitude.toString(),
                            data.latLong?.longitude.toString(),
                            data.address.toString(),
                            "favorite"
                        )
                    }
                }, {
                    destinationLatLong?.let { data ->
                        addToApi(
                            data.latLong?.latitude.toString(),
                            data.latLong?.longitude.toString(),
                            data.address.toString(),
                            "favorite"
                        )
                    }
                })

            }
        }
    }

    override fun getViewBinding(): ActivityMainSearchBinding =
        ActivityMainSearchBinding.inflate(layoutInflater)

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap


//        val locationResult = fusedLocationProviderClient.lastLocation

        //  val sydney = LatLng(-34.0, 151.0)
        // googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        //  googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        try {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )
        } catch (e: NotFoundException) {
        }

        googleMap.setOnMapClickListener {
            //TODO Get location address and set to destination
        };
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
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("##Location", "task.isSuccessful")
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        Log.d("##Location", "lastKnownLocation not null")
                        var currentLatlng = LatLng(
                            lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude
                        )
                        this.googleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatlng, 15.0f
                            )
                        )
                        val marker = googleMap?.addMarker(
                            MarkerOptions().position(currentLatlng)
                                .icon(
                                    BitmapFromVector(
                                        getApplicationContext(),
                                        R.drawable.ic_location
                                    )
                                )
                                .title("Your are here")
                        )
                        marker?.showInfoWindow()
                        // fetchaddressfromlocation(lastKnownLocation!!)
                        sourceLatLong = LocationsData(currentLatlng, "")
                        viewModel.getPlaceDetails(
                            getString(R.string.google_api_key1),
                            lastKnownLocation!!.latitude.toString() + "," +
                                    lastKnownLocation!!.longitude.toString()
                        )
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

    inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            if (resultCode == Constants.SUCCESS_RESULT) {
                val address = resultData.getString(Constants.ADDRESS)
                val locaity = resultData.getString(Constants.LOCAITY)
                val state = resultData.getString(Constants.STATE)
                val district = resultData.getString(Constants.DISTRICT)
                val country = resultData.getString(Constants.COUNTRY)
                val postcode = resultData.getString(Constants.POST_CODE)
                var loc = ""
                if (address != null) {
                    // loc = address
                }
                if (locaity != null) {
                    loc = locaity
                }
                if (state != null) {
                    loc = "$loc, $state"
                }
                if (district != null) {
                    loc = "$loc, $district"
                }
                if (country != null) {
                    loc = "$loc, $country"
                }
                if (postcode != null) {
                    loc = "$loc, $postcode"
                }
                //   binding.etCurrentLocation.setText(loc)
                lastKnownLocation?.let { location ->
                    sourceLatLong =
                        LocationsData(LatLng(location.latitude, location.longitude), loc)
                    actSource.setText(loc)
                }

            } else {
                /*Toast.makeText(
                    APPWIDGET_SERVICE,
                    resultData.getString(Constants.RESULT_DATA_KEY),
                    Toast.LENGTH_SHORT
                ).show()*/
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
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

    private fun getVehicleInfo() {
        viewModel.vehicleInfoResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.layoutSearch.progressLayout.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        updateVehicleSheetInfo(data)
                    }
                    drawLine()
                }
                is DataHandler.ERROR -> {
                    binding.layoutSearch.progressLayout.root.visibility = View.GONE
                    dataHandler.message?.let { showAlert(this@SearchActivity, it) }
                }
            }

        }
    }

    private fun updateVehicleSheetInfo(data: VehicleInfoResponse) {
        childDataHeight = binding.vehiclesSheet.llOutStationRide.height
        if (selectedRideType == RideSelection.TAXI.name) {
            binding.vehiclesSheet.llOutStationRide.visibility = View.GONE
        }
        val vehicleFragment = VehicleInfoFragment()
        vehicleFragment.updateData(
            this,
            binding.vehiclesSheet,
            data,
            vehiclesAdapter,
            sourceLatLong!!,
            destinationLatLong!!,
            selectedRideType
        )
        mapBottomMargin =
            if (binding.vehiclesSheet.llOutStationRide.visibility == View.VISIBLE)
                binding.vehiclesSheet.bottomSheetLayout.height - childDataHeight
            else binding.vehiclesSheet.bottomSheetLayout.height

        vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        changeHeader(getString(R.string.booking))

        vehicleFragment.onCarClicked { car ->
            selectedCar = car
        }
        vehicleFragment.onBookClicked { request ->
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            mapBottomMargin = binding.sheet.bottomSheetLayout.height
            bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            if (sourceLatLong != null && destinationLatLong != null) {
                bookedFragment = BookRideProgressFragment()
                bookedFragment?.updateData(binding.sheet)
                viewModel.bookRideCall(request)
                binding.emergencyButton.visibility = View.VISIBLE
            }
        }
    }

    private fun getPrevInfo() {
        viewModel.recentSearchesResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    // binding.progressBar.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        updateFevSearchData(data)
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateFevSearchData(data: RecentSearchesResponse) {
        val searchFragment = SearchHistoryFragment()
        searchFragment.setUpTabLayout(binding.fevSheet, data, supportFragmentManager)
        searchFragment.onRecentClicked { recent ->
            if (!TextUtils.isEmpty(recent.lat) && !TextUtils.isEmpty(recent.lng)) {
                destinationLatLong = LocationsData(
                    LatLng(
                        recent.lat.toDouble(),
                        recent.lng.toDouble()
                    ),
                    recent.address
                )
                addRecent(
                    destinationLatLong!!
                )
            }
            drawLine()
            fevSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        fevSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        mapBottomMargin = binding.fevSheet.bottomSheetLayout.height
    }


    private fun callApi() {
        if (sourceLatLong != null && destinationLatLong != null) {
            binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
            if (selectedRideType == RideSelection.TAXI.name || selectedRideType == RideSelection.OUT_STATION.name) {
                viewModel.getVehicleInfo(
                    VehicleInfoRequest(
                        from_lat = sourceLatLong?.latLong?.latitude.toString(),
                        from_lng = sourceLatLong?.latLong?.longitude.toString(),
                        to_lat = destinationLatLong?.latLong?.latitude.toString(),
                        to_lng = destinationLatLong?.latLong?.longitude.toString(),
                        travel_type = "city",
                        user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID)
                    )
                )
            } else {
                sharingFragment = SharingFragment()
                sharingFragment?.handleViews(binding.sharingBottomSheet) {
                    if (it) {
                        viewModel.checkAvailability(
                            CheckAvailabilityRequest(
                                from_lat = sourceLatLong?.latLong?.latitude.toString(),
                                from_lng = sourceLatLong?.latLong?.longitude.toString(),
                                to_lat = destinationLatLong?.latLong?.latitude.toString(),
                                to_lng = destinationLatLong?.latLong?.longitude.toString(),
                                user_id = Constants.getValue(
                                    this@SearchActivity,
                                    Constants.USER_ID
                                ),
                                mode = "outstation",
                                gender = "men",
                                seats_required = "3",
                                ride_type = "later",
                                ride_time = "2022-11-03 20:40:00",
                                vehicle_type = "car",
                                sub_vehicle_type = "Mini",
                                from_address = "Shiva Teja Nilayam H.No.6-2-656,Secretariat Hills,Dr YSR Enclave, Secretariat Employees Colony,Neknampur Village,Manikonda (PO),Gandipet Mandal, Neknampur, Ibrahim Bagh, Hyderabad, Telangana 500089, India",
                                to_address = "Survey No. 64, Mind Space, Madhapur, Hyderabad, Telangana 500081, India"
                            )
                        )
                    }
                }
                fevSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                mapBottomMargin = binding.sharingBottomSheet.bottomSheetLayout.height
                sharingVehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED


                /*viewModel.getSharingVehicles(
                    RidesSharingRequest(
                        from_lat = sourceLatLong?.latLong?.latitude.toString(),
                        from_lng = sourceLatLong?.latLong?.longitude.toString(),
                        to_lat = destinationLatLong?.latLong?.latitude.toString(),
                        to_lng = destinationLatLong?.latLong?.longitude.toString(),
                        travel_type = "city",
                        user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID)
                    )
                )*/
                /*viewModel.checkAvailability(
                    CheckAvailabilityRequest(
                        from_lat = sourceLatLong?.latLong?.latitude.toString(),
                        from_lng = sourceLatLong?.latLong?.longitude.toString(),
                        to_lat = destinationLatLong?.latLong?.latitude.toString(),
                        to_lng = destinationLatLong?.latLong?.longitude.toString(),
                        user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID),
                        mode = "outstation",
                        gender = "men",
                        seats_required = "3",
                        ride_type = "later",
                        ride_time = "2022-11-03 20:40:00",
                        vehicle_type = "car",
                        sub_vehicle_type = "Mini",
                        from_address = "Shiva Teja Nilayam H.No.6-2-656,Secretariat Hills,Dr YSR Enclave, Secretariat Employees Colony,Neknampur Village,Manikonda (PO),Gandipet Mandal, Neknampur, Ibrahim Bagh, Hyderabad, Telangana 500089, India",
                        to_address = "Survey No. 64, Mind Space, Madhapur, Hyderabad, Telangana 500081, India")
                )*/
            }

        }

    }

    private fun drawLine() {
        val url = sourceLatLong?.latLong?.let { source ->
            destinationLatLong?.latLong?.let { destination ->
                getDirectionURL(
                    source,
                    destination,
                    getString(R.string.google_api_key1)
                )
            }
        }
        url?.let {
            GetDirection(url).execute()
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, Pair<List<List<LatLng>>, String>>() {
        override fun doInBackground(vararg params: Void?): Pair<List<List<LatLng>>, String> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()
            var km = ""
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()

                for (i in 0 until respObj.routes[0].legs[0].steps.size) {
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    if (i == 0) {
                        km = respObj.routes[0].legs[0].distance.text
                    }

                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Pair(result, km)
        }

        override fun onPostExecute(result: Pair<List<List<LatLng>>, String>) {
            googleMap?.clear()
            val marker = googleMap?.addMarker(
                MarkerOptions().position(sourceLatLong?.latLong!!)
                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_location))
                    .title(result.second)
            )
            marker?.showInfoWindow()
            googleMap?.addMarker(MarkerOptions().position(destinationLatLong?.latLong!!))

            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    sourceLatLong?.latLong!!,
                    12F
                )
            )
            val lineoption = PolylineOptions()
            for (i in result.first.indices) {
                lineoption.addAll(result.first[i])
                lineoption.width(10f)
                lineoption.color(ContextCompat.getColor(applicationContext, R.color.tabIndicator))
                lineoption.geodesic(true)
            }
            googleMap?.addPolyline(lineoption)
            binding.layoutSearch.progressLayout.root.visibility = View.GONE
        }
    }


    private fun addRecent(destinationLatLong: LocationsData) {
        if (sourceLatLong != null) {
            callApi()
        }
        actDestination.setText(destinationLatLong.address)
    }

    private fun interChangeLocations() {
        val temp = sourceLatLong
        sourceLatLong = destinationLatLong
        destinationLatLong = temp

        actSource.setText(sourceLatLong?.address)
        actDestination.setText(destinationLatLong?.address)
        callApi()
    }

    private fun addToApi(lat: String, long: String, address: String, type: String) {
        if (!TextUtils.isEmpty(Constants.getValue(this@SearchActivity, Constants.USER_ID))) {
            viewModel.addToRecent(
                RecentFevRequest(
                    user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID).toInt(),
                    mode = "city",
                    lat = lat,
                    lng = long,
                    address = address,
                    type = type
                )
            )
        }
    }

    private fun bookRide() {
        /*viewModel.bookRideCall(BookRideRequest(

        ))*/
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

    private fun showBottomSheet() {
        bookProgressSheetBehavior = BottomSheetBehavior.from(binding.sheet.bottomSheetLayout)
        bookProgressSheetBehavior?.setBottomSheetCallback(handler)
        bookProgressSheetBehavior?.peekHeight = 0


        fevSheetBehavior = BottomSheetBehavior.from(binding.fevSheet.bottomSheetLayout)
        fevSheetBehavior?.setBottomSheetCallback(handler)
        fevSheetBehavior?.peekHeight = 0

        vehicleSheetBehavior = BottomSheetBehavior.from(binding.vehiclesSheet.bottomSheetLayout)
        vehicleSheetBehavior?.setBottomSheetCallback(handler)
        vehicleSheetBehavior?.peekHeight = 0

        bookResultSheetBehavior = BottomSheetBehavior.from(binding.bookedSheet.bottomSheetLayout)
        bookResultSheetBehavior?.setBottomSheetCallback(handler)
        bookResultSheetBehavior?.peekHeight = 0

        sharingVehicleSheetBehavior =
            BottomSheetBehavior.from(binding.sharingBottomSheet.bottomSheetLayout)
        sharingVehicleSheetBehavior?.setBottomSheetCallback(handler)
        sharingVehicleSheetBehavior?.peekHeight = 0

        sheetAvailabilitySheetBehavior =
            BottomSheetBehavior.from(binding.sheetAvailability.bottomSheetLayout)
        sheetAvailabilitySheetBehavior?.setBottomSheetCallback(handler)
        sheetAvailabilitySheetBehavior?.peekHeight = 0

    }

    private val handler = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.layoutSearch.mapFrame.setPadding(
                    0,
                    0,
                    0,
                    mapBottomMargin
                )

                binding.llEmergency.setPadding(
                    0,
                    0,
                    50,
                    mapBottomMargin + 50
                )

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    fevSheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.layoutSearch.mapFrame.setPadding(0, 0, 0, 0)
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private fun changeHeader(title: String) {
        binding.layoutSearch.toolbar.tvHeader.text = title
    }

}


