package com.ciaorides.ciaorides.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityMainSearchBinding
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.ciaorides.ciaorides.fcm.FcmBookingModel
import com.ciaorides.ciaorides.model.LocationsData
import com.ciaorides.ciaorides.model.MapData
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.response.GetVehicleMapLocationsResponse
import com.ciaorides.ciaorides.model.response.OfferRideVehicleInfo
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.*
import com.ciaorides.ciaorides.utils.Constants.showAlert
import com.ciaorides.ciaorides.view.activities.chat.ChatViewActivity
import com.ciaorides.ciaorides.view.activities.ui.home.RideSelection
import com.ciaorides.ciaorides.view.adapter.MyVehiclesInSearchAdapter
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import com.ciaorides.ciaorides.view.fragments.*
import com.ciaorides.ciaorides.viewmodel.MenuViewModel
import com.ciaorides.ciaorides.viewmodel.SearchViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.annotations.Nullable
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivityMainSearchBinding>(), PaymentResultListener {
    val REQUEST_CODE_CHECK_SETTINGS = 2000
    val LOCATION_PERMISSION_REQ_CODE = 1000
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
    private var sheetOfferRideVehiclesBehavior: BottomSheetBehavior<*>? = null
    private var vehicleSheetBehavior: BottomSheetBehavior<*>? = null
    private var bookResultSheetBehavior: BottomSheetBehavior<*>? = null
    private var sharingVehicleSheetBehavior: BottomSheetBehavior<*>? = null
    private var sheetAvailabilitySheetBehavior: BottomSheetBehavior<*>? = null
    private var bookedFragment: BookRideProgressFragment? = null
    private var sharingFragment: SharingFragment? = null
    private var retryBookDataRequest: BookRideRequest? = null

    private var currentSheetBehavior: BottomSheetBehavior<*>? = null
    var isFirst = true
    var bookingId = ""
    var order_id = ""
    var rider_id = ""
    var ride_start_time = ""
    var ride_end_time = ""
    var total_ride_amount = ""
    var selectedSharingCount = 0
    var driverId = ""
    var isPlaceSelected = false

    var selectedRideType = RideSelection.TAXI.name
    var taxiBookType = BookType.NOW
    var scheduleDateTime: Pair<String, String> = Pair<String, String>("", "")

    var offerRideSelectedVehicle: SelectedVehicle? = null
    var userId = ""

    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter

    @Inject
    lateinit var myVehiclesInSearchAdapter: MyVehiclesInSearchAdapter

    private var mapBottomMargin = 0
    private var childDataHeight = 0

    val menuViewModel: MenuViewModel by viewModels()

    override fun init() {
        binding.progressLayout.root.visibility = View.VISIBLE
        userId = Constants.getValue(this@SearchActivity, Constants.USER_ID)
        intent.getStringExtra(Constants.RIDE_TYPE)?.let {
            selectedRideType = it
        }
        setupMap()
        initData()

        binding.bookedSheet.btnCancel.setOnClickListener {
            showAlert(this@SearchActivity, getString(R.string.cancel_ride_msg), "", true) {
                makeCancelRideCall()
            }
        }
        binding.emergencyButton.setOnClickListener {
            showEmergencyCallsAlert(this@SearchActivity)
        }
        binding.bookedSheet.btnSupport.setOnClickListener {
            showCustomerSupport(this@SearchActivity)
        }

        binding.sheetAvailability.btnBookNow.setOnClickListener {
            Constants.showBookAlert(
                this@SearchActivity,
                "Your ride has been successfully schedule on " + scheduleDateTime.first + " at " + scheduleDateTime.second
            ) {
                resetData()
            }
        }
        binding.bookedSheet.btnPay.setOnClickListener {
            menuViewModel.getRazorPayResponse()
        }
        binding.sheet.btnTryAgain.setOnClickListener {
            with(binding.sheet) {
                tvProgressMsg.text =
                    getString(R.string.please_wait_we_are_in_the_process_of_finding_a_best_rider_for_you)
                llSearRideFailLayout.visibility = View.GONE
                btnCancel.visibility = View.VISIBLE
                pbSearch.visibility = View.VISIBLE
                ivLogo.setImageResource(R.drawable.progree_ing)
                retryBookDataRequest?.let {
                    viewModel.bookRideCall(it)
                }
            }
        }
        binding.sheet.btnCancel.setOnClickListener {
            bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            currentSheetBehavior = vehicleSheetBehavior
        }
        binding.sheet.btnSkip.setOnClickListener {
            bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            currentSheetBehavior = vehicleSheetBehavior
        }

        handleServerCallsResponse()
    }

    fun handleServerCallsResponse(){
        resultReceiver = AddressResultReceiver(Handler(Looper.getMainLooper()))

        // Construct a PlacesClient
        Places.initialize(applicationContext, getString(R.string.google_api_key1))
        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkPermissions()
        enableLocationSettings()
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
        handleCompleteTaxiRideCall()
        handlePlacesAddressCall()
        showBottomSheet()
        addSharingAvailabilityObserver()
        handleMyVehicle()
        handleMapLocations()
        handleRazorPayResponse()

        handleOfferRideCall()
        makeRecentRequest()
    }


    private fun makeCancelRideCall() {
        if (!TextUtils.isEmpty(Constants.getValue(this@SearchActivity, Constants.USER_ID))) {
            binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
            val dataMain = FcmBookingModel()
            dataMain.bookingNumber = bookingId
            dataMain.userId = userId
            dataMain.rideStatus = Constants.RIDE_CANCELLED
            FcmBookUtils.removeBooking(
                driverId = driverId,
                dataMain
            )
            viewModel.cancelTaxiTrip(
                CancelRideRequest(
                    user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID),
                    booking_id = bookingId,
                    order_id = order_id,
                    rider_name = "",
                    rider_id = driverId
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
                            sharingVehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                            mapBottomMargin = binding.sheetAvailability.bottomSheetLayout.height
                            sheetAvailabilitySheetBehavior?.state =
                                BottomSheetBehavior.STATE_EXPANDED
                            currentSheetBehavior = sheetAvailabilitySheetBehavior
                            val availabilityDrivers = AvailabilityDrivers()
                            availabilityDrivers.updateData(
                                binding.sheetAvailability,
                                dataHandler.data.response,
                                4,
                                selectedSharingCount
                            )
                        }

                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do NOthing
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
                            if (taxiBookType == BookType.NOW) {
                                val dataMain = FcmBookUtils.getBookingModel(data)
                                dataMain.sourceAddress = sourceLatLong?.address
                                dataMain.destinationAddress = destinationLatLong?.address
                                dataMain.rideStatus = Constants.PENDING
                                for (item in data.response) {
                                    FcmBookUtils.addBooking(
                                        item.driver_id,
                                        dataMain,
                                        item
                                    )
                                    driverId += if (TextUtils.isEmpty(driverId)) {
                                        item.driver_id
                                    } else {
                                        "," + item.driver_id
                                    }

                                }
                                FcmBookUtils.addDriversToBooingId(
                                    driverId,
                                    data.booking_id.toString()
                                )
                                checkStateOfBookApi()
                            } else {
                                Constants.showBookAlert(
                                    this@SearchActivity,
                                    "Your ride has been successfully schedule on " + scheduleDateTime.first + " at " + scheduleDateTime.second
                                ) {
                                    resetData()
                                }
                            }
                        }

                    }
                }
                is DataHandler.ERROR -> {
                    Handler().postDelayed({
                        with(binding.sheet) {
                            tvProgressMsg.text = getString(R.string.try_again_msg)
                            llSearRideFailLayout.visibility = View.VISIBLE
                            btnCancel.visibility = View.GONE
                            pbSearch.visibility = View.GONE
                            ivLogo.setImageResource(R.drawable.try_again)
                        }
                    }, 10500)
                }
                is DataHandler.LOADING -> {
                    // Do NOthing
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
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }

        }
    }

    private fun resetData() {
        startActivity(Intent(this, SearchActivity::class.java))
        finish()
    }

    private fun updateAfterBookData(res: FcmBookingModel) {
        bookingId = res.bookingNumber.toString()
        binding.bookedSheet.tvCurrentLocation.text = res.sourceAddress
        binding.bookedSheet.tvDestination.text = res.destinationAddress
        binding.bookedSheet.tvVehicleNumber.text = res.driverInfo.number_plate
        binding.bookedSheet.tvName.text = res.driverInfo.first_name
        binding.bookedSheet.tvOtp.text = res.otp.toString()

        total_ride_amount = res.amount.toString()
        order_id = res.orderId
        rider_id = res.driverInfo.driver_id
        binding.bookedSheet.tvVehicleType.text =
            res.driverInfo.vehicle_make + " " + res.driverInfo.vehicle_model
        if (res.driverInfo.r_ratings == null) {
            binding.bookedSheet.tvRating.visibility = View.GONE
        } else {
            binding.bookedSheet.tvRating.text = res.driverInfo.r_ratings
        }

        binding.bookedSheet.callDriver.setOnClickListener {
            try {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + res.driverInfo.mobile)
                startActivity(callIntent)
            } catch (e:Exception){
                e.printStackTrace()
            }
        }
        binding.bookedSheet.etChat.setOnClickListener {
            val intent = Intent(applicationContext, ChatViewActivity::class.java)
            intent.putExtra(Constants.DATA_VALUE, res)
            startActivity(intent)
        }

        res.driverInfo.profile_pic?.let {
            Constants.showGlide(
                applicationContext,
                it,
                binding.bookedSheet.profileImage,
                binding.bookedSheet.progress
            )
        }
        bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        currentSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        mapBottomMargin = binding.bookedSheet.bottomSheetLayout.height
        currentSheetBehavior = bookResultSheetBehavior
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
                isPlaceSelected = true
                fevSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
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
                fevSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
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
            enableLocationSettings()
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
                        this.googleMap?.isMyLocationEnabled = true
                        this.googleMap?.uiSettings?.isMyLocationButtonEnabled = true
//                        getCurrentLocation()
                        enableLocationSettings()
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun initData() {
        checkStateOfBookApi()
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
                binding.layoutSearch.ivCurrentLocationFev.setImageDrawable(getDrawable(R.drawable.baseline_favorite_24))
            }
        }
        binding.layoutSearch.ivDestinationLocationFev.setOnClickListener {
            destinationLatLong?.address?.let {
                Constants.showFevAlert(this@SearchActivity, it, {
                    destinationLatLong?.let { data ->
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
                binding.layoutSearch.ivDestinationLocationFev.setImageDrawable(getDrawable(R.drawable.baseline_favorite_24))
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
                    if (!isPlaceSelected)
                        getLocationOnMaps(lastKnownLocation!!)
                } else {
                    binding.progressLayout.root.visibility = View.GONE
                    Log.d("TAG", "Current location is null. Using defaults.")
                    Log.e("TAG", "Exception: %s", task.exception)
                    googleMap?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), 11f
                            )
                    )
                    googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }

        } catch (e: SecurityException) {
            binding.progressLayout.root.visibility = View.GONE
            Log.e("Exception: %s", e.message, e)
        }
    }

    inner class AddressResultReceiver(handler: Handler?) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            binding.progressLayout.root.visibility = View.GONE
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
                    Log.d("Location Updated", "Last known location")
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
        val locationListener : LocationListener = LocationListener { location ->
            // val latitute = location.latitude
            // val longitute = location.longitude
              Log.i("test", "Latitute: ${location.latitude} ; Longitute: ${location.longitude}")
            lastKnownLocation = location
            Constants.saveValue(this, Constants.LAST_KNOWN_LOCATION_LATITUDE, lastKnownLocation!!.latitude.toString())
            Constants.saveValue(this, Constants.LAST_KNOWN_LOCATION_LATITUDE, lastKnownLocation!!.latitude.toString())
            if (!isPlaceSelected)
                getLocationOnMaps(lastKnownLocation!!)
        }
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0L,
            0f,
            locationListener
        )

        this.googleMap?.isMyLocationEnabled = true
        this.googleMap?.uiSettings?.isMyLocationButtonEnabled = true
//        getDeviceLocation()
    }

    private fun getLocationOnMaps(lastKnownLocation: Location) {
        try {
            binding.progressLayout.root.visibility = View.GONE
            Log.d("##Location", "lastKnownLocation not null")
            var currentLatlng = LatLng(
                lastKnownLocation.latitude,
                lastKnownLocation.longitude
            )
            this.googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentLatlng, 11f
                )
            )

            val marker = displayOnMarker(
                currentLatlng,
                R.drawable.ic_location,
                title = "Your are here"
            )
            //aet vehicles
            if (selectedRideType == RideSelection.TAXI.name) {
                viewModel.getOnlineMapVehicles(
                    GetOnlineMapVehiclesRequest(
                        user_id = userId,
                        from_lat = currentLatlng.latitude.toString(),
                        from_lng = currentLatlng.longitude.toString(),
                        radius = "20"
                    )
                )
            }

            marker?.showInfoWindow()
            // fetchaddressfromlocation(lastKnownLocation!!)
            sourceLatLong = LocationsData(currentLatlng, "")
            viewModel.getPlaceDetails(
                getString(R.string.google_api_key1),
                lastKnownLocation!!.latitude.toString() + "," +
                        lastKnownLocation!!.longitude.toString()
            )
        } catch (e: Exception){
            println(e.stackTrace)
        }
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
                    dataHandler.message?.let {
                        globalAlert(this@SearchActivity, it, "Ok", "") {
                            makeRecentRequest()
                        }
                    }
                }
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }

        }
    }

    private fun updateVehicleSheetInfo(data: VehicleInfoResponse) {
        childDataHeight = binding.vehiclesSheet.llOutStationRide.height
        if (selectedRideType == RideSelection.TAXI.name) {
            binding.vehiclesSheet.llOutStationRide.visibility = View.GONE
        }
        val vehicleFragment = VehicleInfoFragment {
            updateVehicleChangeMargins()
        }
        vehicleFragment.updateData(
            this,
            binding.vehiclesSheet,
            data,
            vehiclesAdapter,
            sourceLatLong!!,
            destinationLatLong!!,
            selectedRideType
        )
        updateVehicleChangeMargins()
        vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        currentSheetBehavior = vehicleSheetBehavior
        changeHeader(getString(R.string.booking))

        vehicleFragment.onCarClicked { car ->
            selectedCar = car
        }
        vehicleFragment.onBookClicked { request, type ->
            retryBookDataRequest = request
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            mapBottomMargin = binding.sheet.bottomSheetLayout.height
            currentSheetBehavior = bookProgressSheetBehavior

            if (sourceLatLong != null && destinationLatLong != null) {
                bookedFragment = BookRideProgressFragment()
                bookedFragment?.updateData(binding.sheet)
                viewModel.bookRideCall(request)
                taxiBookType = type
                scheduleDateTime = getDateTime(request.ride_time)
            }
        }
    }

    private fun updateVehicleChangeMargins() {
        mapBottomMargin = binding.vehiclesSheet.bottomSheetLayout.height

        if (binding.vehiclesSheet.llOutStationRide.visibility == View.VISIBLE)
            mapBottomMargin -= childDataHeight

        if (binding.vehiclesSheet.cardCars.visibility == View.GONE)
            mapBottomMargin -= binding.vehiclesSheet.cardCars.height

        if (binding.vehiclesSheet.cardAuto.visibility == View.GONE)
            mapBottomMargin -= binding.vehiclesSheet.cardAuto.height

        if (binding.vehiclesSheet.cardBike.visibility == View.GONE)
            mapBottomMargin -= binding.vehiclesSheet.cardBike.height

        if (binding.vehiclesSheet.rvCars.visibility == View.GONE) {
            mapBottomMargin -= binding.vehiclesSheet.rvCars.height
        }
        binding.layoutSearch.mapFrame.setPadding(
            0,
            0,
            0,
            mapBottomMargin - 50
        )
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
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }

        }
    }

    private fun updateFevSearchData(data: RecentSearchesResponse) {
        val searchFragment = SearchHistoryFragment()
        searchFragment.setUpTabLayout(binding.fevSheet, data, supportFragmentManager)
        searchFragment.onRecentClicked { recent ->
            if (!TextUtils.isEmpty(recent.lat) && !TextUtils.isEmpty(recent.lng)) {
                fevSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
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
        }
        fevSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        currentSheetBehavior = fevSheetBehavior
        mapBottomMargin = binding.fevSheet.bottomSheetLayout.height
    }


    private fun callApi() {
        if (sourceLatLong != null && destinationLatLong != null) {
            binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
            if (selectedRideType == RideSelection.TAXI.name ) {
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
            } else if (selectedRideType == RideSelection.OUT_STATION.name){
                viewModel.getVehicleInfo(
                    VehicleInfoRequest(
                        from_lat = sourceLatLong?.latLong?.latitude.toString(),
                        from_lng = sourceLatLong?.latLong?.longitude.toString(),
                        to_lat = destinationLatLong?.latLong?.latitude.toString(),
                        to_lng = destinationLatLong?.latLong?.longitude.toString(),
                        travel_type = "outstation",
                        user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID)
                    )
                )
            } else {
                if (!intent.getBooleanExtra(Constants.IS_RIDE_OFFER, false)) {
                    sharingFragment = SharingFragment()
                    sharingFragment?.handleViews(binding.sharingBottomSheet) {
                        if (it.first) {
                            selectedSharingCount = it.second
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
                                    seats_required = it.second.toString(),
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
                    currentSheetBehavior = sharingVehicleSheetBehavior
                } else {
                    sheetOfferRideVehiclesBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                    mapBottomMargin = binding.sheetOfferRideVehicles.bottomSheetLayout.height
                    sheetOfferRideVehiclesBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
                    currentSheetBehavior = sheetOfferRideVehiclesBehavior
                    vehiclesCall()
                }


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
                    11f
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
        isPlaceSelected = true
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

        sheetOfferRideVehiclesBehavior =
            BottomSheetBehavior.from(binding.sheetOfferRideVehicles.bottomSheetLayout)
        sheetOfferRideVehiclesBehavior?.setBottomSheetCallback(handler)
        sheetOfferRideVehiclesBehavior?.peekHeight = 0

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
                    mapBottomMargin - 30
                )
                binding.llEmergency.setPadding(
                    0,
                    0,
                    50,
                    mapBottomMargin + 50
                )
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.layoutSearch.mapFrame.setPadding(0, 0, 0, 0)
            } else if (newState == BottomSheetBehavior.STATE_DRAGGING) bottomSheet.post {
                currentSheetBehavior?.setState(
                    BottomSheetBehavior.STATE_EXPANDED
                )
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    private fun changeHeader(title: String) {
        binding.layoutSearch.toolbar.tvHeader.text = title
    }

    private fun vehiclesCall() {
        if (!TextUtils.isEmpty(Constants.getValue(this@SearchActivity, Constants.USER_ID))) {
            binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
            viewModel.getMyVehicles(
                GlobalUserIdRequest(
                    user_id = Constants.getValue(applicationContext, Constants.USER_ID)
                )
            )
        }
    }

    private fun handleMyVehicle() {
        viewModel.myVehicleResponse.observe(this) { dataHandler ->
            binding.layoutSearch.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            val offerRideRegister = OfferRideRegister()
                            offerRideRegister.updateData(
                                this,
                                binding.sheetOfferRideVehicles,
                                data.response,
                                myVehiclesInSearchAdapter,
                                sourceLatLong!!,
                                destinationLatLong!!
                            ) {
                                makeOfferRideApiCall(it)
                            }
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }
        }
    }

    private fun makeOfferRideApiCall(offerRideVehicleInfo: OfferRideVehicleInfo) {
        binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
        viewModel.offerRide(
            OfferARideRequest(
                user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID),
                vehicle_id = offerRideVehicleInfo.vehicleId,
                from_lat = sourceLatLong?.latLong?.latitude.toString(),
                from_lng = sourceLatLong?.latLong?.longitude.toString(),
                from_address = sourceLatLong?.address.toString(),
                to_lat = destinationLatLong?.latLong?.latitude.toString(),
                to_lng = destinationLatLong?.latLong?.longitude.toString(),
                to_address = destinationLatLong?.address.toString(),
                mode = "outstation",
                vehicle_type = offerRideVehicleInfo.vehicleType,
                gender = offerRideVehicleInfo.gender,
                seats_available = offerRideVehicleInfo.seatsAvailable,
                ride_type = "later",
                amount_per_head = offerRideVehicleInfo.amountPerHead,
                ride_time = offerRideVehicleInfo.rideTime,
                middle_seat_empty = offerRideVehicleInfo.middleSeatEmpty
            )
        )
    }

    private fun handleOfferRideCall() {
        viewModel.offerRideResponse.observe(this) { dataHandler ->
            binding.layoutSearch.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            Constants.showBookAlert(
                                this@SearchActivity,
                                "Your sharing ride has registered successfully"
                            ) {
                            }
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }

        }
    }

    private fun handleMapLocations() {
        viewModel.mapLocationsResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status && data.response.isNotEmpty()) {
                            displayMarkers(data.response)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do Nothing
                }
            }
        }
    }

    private fun displayMarkers(response: List<GetVehicleMapLocationsResponse.Response>) {
        for (item in response) {
            displayOnMarker(
                LatLng(item.lat.toDouble(), item.lng.toDouble()),
                when (item.vehicle_type) {
                    "bike" -> {
                        R.drawable.ic_small_bike
                    }
                    "auto" -> R.drawable.ic_small_auto
                    else -> R.drawable.ic_small_car
                },
                rotation = if (item.vehicle_type == "bike" || item.vehicle_type == "auto") 0f else getRandom()
            )
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
                        applicationContext,
                        icon
                    )
                ).rotation(rotation)
                .title(title)
        )!!
    }

    private fun getRandom(): Float {
        return 0 + Random().nextFloat() * (360 - 0)
    }

    private fun checkStateOfBookApi() {
        val messagesRef = Firebase.database.reference.child(FcmBookUtils.BOOKING)
            .child(FcmBookUtils.ACTIVE_BOOKINGS).child(FcmBookUtils.USERS).child(userId)
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String()::class.java)?.let { getBookingInfo(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getBookingInfo(bookingId: String) {
        val messagesRef =
            Firebase.database.reference.child(FcmBookUtils.BOOKING).child(FcmBookUtils.RIDES)
                .child(bookingId)
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val map = (snapshot.value as? HashMap<*, *>)
                    if (map != null) {
                        for ((key, value) in map) {
                            val data =
                                snapshot.child(key.toString()).getValue(FcmBookingModel::class.java)
                            if (data?.rideStatus == Constants.APPROVED) {
                                updateAfterBookData(data)
                            } else if (data?.rideStatus == Constants.REACHED) {
                                updateAfterBookData(data)
                                binding.bookedSheet.tvRideMessage.text = "Driver Reached"
                            } else if (data?.rideStatus == Constants.RIDE_COMPLETED) {
                                updateAfterBookData(data)
                                binding.bookedSheet.tvRideMessage.text =
                                    "Your ride completed successfully. Please pay your amount"
                                binding.bookedSheet.btnPay.isVisible = true
                                binding.bookedSheet.payLayout.isVisible = false
                            } else if (data?.rideStatus == Constants.PAYMENT_COMPLETED) {
                                // Do nothing as of now.
                                // Close booking result sheet.
                                currentSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                                bookResultSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
                                makeRecentRequest()
                            }
                            //Toast.makeText(applicationContext, data?.rideStatus, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e:Exception){
                    println(e.stackTrace)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun payAmountOnline(amount: Float) {
        val checkout = Checkout()
        Checkout.preload(applicationContext)
        checkout.setKeyID("rzp_test_QBcx9z9QSuTAtz")
        checkout.setImage(com.razorpay.R.drawable.rzp_logo);
        val amount = (amount.toFloat() * 100).roundToInt().toInt()
        val requestData = JSONObject()
        try {
            // to put name
            requestData.put("name", getString(R.string.app_name))

            // put description
            requestData.put("description", "Test payment")

            // to set theme color
            //requestData.put("theme.color", "")

            // put the currency
            requestData.put("currency", "INR")

            // put amount
            requestData.put("amount", amount)

            // put mobile number
            requestData.put("prefill.contact", Constants.getValue(this, Constants.USER_ID))

            // put email
            // requestData.put("prefill.email", "chaitanyamunje@gmail.com")

            // open razorpay to checkout activity
            checkout.open(this@SearchActivity, requestData)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        paymentSuccessAlert(this@SearchActivity)
        callTaxiPaymentCompleteAPI(razorpayPaymentID)

        // Till payment is successful, not completing the ride.
        /*FcmBookUtils.updateApprovedStatus(
            bookingId,
            rider_id,
            Constants.PAYMENT_COMPLETED
        )*/
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(applicationContext, "Payment failed. Please try again!", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onBackPressed() {
        editAlert(this@SearchActivity) {
            super.onBackPressed()
        }
    }

    private fun callTaxiPaymentCompleteAPI(razorpayPaymentID: String?){
        if (!TextUtils.isEmpty(Constants.getValue(this@SearchActivity, Constants.USER_ID))) {
            viewModel.completeTaxiTrip(
                CompleteOfferRideRequest(
                    amount = total_ride_amount,
                    order_id = order_id ,
                    rider_id = rider_id,
                    user_id = Constants.getValue(this@SearchActivity, Constants.USER_ID),
                    booking_id = bookingId,
                    transaction_id = razorpayPaymentID!!,
                    payment_gateway_provider = "razorpay",
                    payment_status = "paid",
                    ride_start_time = ride_start_time,
                    ride_end_time = ride_end_time,
                    payment_type = "online"
                )
            )
        }
    }

    private fun handleCompleteTaxiRideCall() {
        viewModel.completeTaxiRideResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            FcmBookUtils.updateApprovedStatus(
                                bookingId,
                                rider_id,
                                Constants.PAYMENT_COMPLETED
                            )
                            Toast.makeText(this, data.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    protected fun enableLocationSettings() {
        try {
            if (isLocationEnabled()) {
                getCurrentLocation()
            } else {
                val locationRequest: LocationRequest = LocationRequest.create()
                    .setInterval(0L)
                    .setFastestInterval(0L)
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                LocationServices
                    .getSettingsClient(this)
                    .checkLocationSettings(builder.build())
                    .addOnSuccessListener(
                        this
                    ) { response: LocationSettingsResponse? -> }
                    .addOnFailureListener(
                        this
                    ) { ex: java.lang.Exception? ->
                        if (ex is ResolvableApiException) {
                            // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                                ex.startResolutionForResult(
                                    this@SearchActivity,
                                    REQUEST_CODE_CHECK_SETTINGS
                                )
                            } catch (sendEx: SendIntentException) {
                                // Ignore the error.
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            println(e.printStackTrace())

            val lastSavedLocation = Location("")
            try {
                lastSavedLocation.latitude =
                    Constants.getValue(this, Constants.LAST_KNOWN_LOCATION_LATITUDE).toDouble()
                lastSavedLocation.longitude =
                    Constants.getValue(this, Constants.LAST_KNOWN_LOCATION_LONGITUDE).toDouble()
                if (!isPlaceSelected)
                    getLocationOnMaps(lastSavedLocation)
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (REQUEST_CODE_CHECK_SETTINGS == requestCode) {
            if (RESULT_OK == resultCode) {
                //user clicked OK, you can startUpdatingLocation(...);
                getCurrentLocation()
            } else {
                // Do nothing as of now
                //user clicked cancel: informUserImportanceOfLocationAndPresentRequestAgain();
                /*showAlert(this@SearchActivity, getString(R.string.enable_location), "", false) {
                    enableLocationSettings()
                }*/
            }
        }
    }

    private fun handleRazorPayResponse() {
        menuViewModel.razorPayResponse.observe(this){dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            payAmountOnline(total_ride_amount.toFloat())
                        } else {
                            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {
                    // Do nothing
                }
            }
        }
    }

}



