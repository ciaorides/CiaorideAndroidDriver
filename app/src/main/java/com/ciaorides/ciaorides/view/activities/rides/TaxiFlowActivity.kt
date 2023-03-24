package com.ciaorides.ciaorides.view.activities.rides

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
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
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityTaxiFlowBinding
import com.ciaorides.ciaorides.model.LocationsData
import com.ciaorides.ciaorides.model.MapData
import com.ciaorides.ciaorides.model.request.RecentSearchRequest
import com.ciaorides.ciaorides.model.request.VehicleInfoRequest
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.*
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.activities.ui.home.RideSelection
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import com.ciaorides.ciaorides.view.fragments.BookRideProgressFragment
import com.ciaorides.ciaorides.view.fragments.SearchHistoryFragment
import com.ciaorides.ciaorides.view.fragments.VehicleInfoFragment
import com.ciaorides.ciaorides.viewmodel.TaxiViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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
class TaxiFlowActivity : BaseActivity<ActivityTaxiFlowBinding>() {
    private var lastKnownLocation: Location? = null
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var sourceLatLong: LocationsData? = null
    private var destinationLatLong: LocationsData? = null
    private lateinit var placesClient: PlacesClient
    private val viewModel: TaxiViewModel by viewModels()
    private lateinit var actSource: AutocompleteSupportFragment
    private lateinit var actDestination: AutocompleteSupportFragment
    private var resultReceiver: ResultReceiver? = null
    var isFirst = true
    lateinit var activity: TaxiFlowActivity
    private var fevSheetBehavior: BottomSheetBehavior<*>? = null
    private var vehicleSheetBehavior: BottomSheetBehavior<*>? = null
    private var bookProgressSheetBehavior: BottomSheetBehavior<*>? = null

    private var bookedFragment: BookRideProgressFragment? = null
    private var mapBottomMargin = 0
    private var childDataHeight = 0

    private var selectedCar: VehicleInfoResponse.Response.Car? = null

    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter

    override fun init() {
        activity = this@TaxiFlowActivity
        setupMap()
        setupData()
        handleClicks()
        handlePermissions()
        initPlaces()
        resultReceiver = AddressResultReceiver(Handler(Looper.getMainLooper()))
        handleObservers()
        showBottomSheet()
    }


    private fun handleObservers() {
        handlePlacesAddressCall()
        handleSearchPrevInfo()
        makeRecentRequest()
        vehicleInfoObserver()
        handleBookRideCall()
    }

    private fun initPlaces() {
        Places.initialize(applicationContext, getString(R.string.google_api_key1))
        placesClient = Places.createClient(this)
        initSourcePlacesApi()
        initDestinationPlacesApi()
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
                /*addToApi(
                    place.latLng.latitude.toString(),
                    place.latLng.longitude.toString(),
                    place.address,
                    "recent"
                )*/
            }

            override fun onError(status: Status) {
                actDestination.setText("")
            }
        })

    }

    private fun handlePermissions() {
        PermissionUtils.checkPermissions(
            this@TaxiFlowActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) {
            if (it) {
                getCurrentLocation()
            }
        }
    }

    private fun setupData() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        binding.layoutSearch.toolbar.tvHeader.text = getString(R.string.search)
    }

    private fun handleClicks() {

    }

    override fun getViewBinding(): ActivityTaxiFlowBinding =
        ActivityTaxiFlowBinding.inflate(layoutInflater)

    private fun setupMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        try {
            googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style
                )
            )
        } catch (e: Resources.NotFoundException) {
        }

        googleMap.setOnMapClickListener {
            //TODO Get location address and set to destination
        };
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val locationListener = LocationListener {}
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

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
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
                                    bitmapFromVector(
                                        applicationContext,
                                        R.drawable.ic_location
                                    )
                                )
                                .title("Your are here")
                        )
                        marker?.showInfoWindow()
                        sourceLatLong = LocationsData(currentLatlng, "")
                        viewModel.getPlaceDetails(
                            getString(R.string.google_api_key1),
                            lastKnownLocation!!.latitude.toString() + "," +
                                    lastKnownLocation!!.longitude.toString()
                        )
                    }
                } else {
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
                            this@TaxiFlowActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
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

            }
        }
    }

    private fun makeRecentRequest() {
        if (!TextUtils.isEmpty(Constants.getValue(activity, Constants.USER_ID))) {
            viewModel.makeRecentRequest(
                RecentSearchRequest(
                    user_id = Constants.getValue(activity, Constants.USER_ID).toInt(),
                    type = TempConstants.TYPE,
                    mode = TempConstants.MODE,
                    from_lat = TempConstants.FROM_LAT,
                    from_lng = TempConstants.FROM_LAT,
                )
            )
        }
    }

    private fun handlePlacesAddressCall() {
        viewModel.locationInfo.observe(this) { response ->
            sourceLatLong?.let {
                if (response.results.isNotEmpty()) {
                    it.address = response.results[0].formatted_address
                    actSource.setText(it.address)
                    if (isFirst) {
                        // callApi()
                        isFirst = false
                    }

                }
            }
        }
    }

    private fun handleSearchPrevInfo() {
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

    private fun addRecent(destinationLatLong: LocationsData) {
        if (sourceLatLong != null) {
            callApi()
        }
        actDestination.setText(destinationLatLong.address)
    }

    private fun showBottomSheet() {

        fevSheetBehavior = BottomSheetBehavior.from(binding.fevSheet.bottomSheetLayout)
        fevSheetBehavior?.setBottomSheetCallback(handler)
        fevSheetBehavior?.peekHeight = 0

        vehicleSheetBehavior = BottomSheetBehavior.from(binding.vehiclesSheet.bottomSheetLayout)
        vehicleSheetBehavior?.setBottomSheetCallback(handler)
        vehicleSheetBehavior?.peekHeight = 0

        bookProgressSheetBehavior = BottomSheetBehavior.from(binding.bookRideProgressSheet.bottomSheetLayout)
        bookProgressSheetBehavior?.setBottomSheetCallback(handler)
        bookProgressSheetBehavior?.peekHeight = 0


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

    private val handler = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.layoutSearch.mapFrame.setPadding(
                    0,
                    0,
                    0,
                    mapBottomMargin
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
                    .icon(bitmapFromVector(getApplicationContext(), R.drawable.ic_location))
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

    private fun callApi() {
        if (sourceLatLong != null && destinationLatLong != null) {
            binding.layoutSearch.progressLayout.root.visibility = View.VISIBLE
            viewModel.getVehicleInfo(
                VehicleInfoRequest(
                    from_lat = sourceLatLong?.latLong?.latitude.toString(),
                    from_lng = sourceLatLong?.latLong?.longitude.toString(),
                    to_lat = destinationLatLong?.latLong?.latitude.toString(),
                    to_lng = destinationLatLong?.latLong?.longitude.toString(),
                    travel_type = "city",
                    user_id = Constants.getValue(activity, Constants.USER_ID)
                )
            )
        }
    }

    private fun vehicleInfoObserver() {
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
                    dataHandler.message?.let { Constants.showAlert(activity, it) }
                }
            }

        }
    }

    private fun updateVehicleSheetInfo(data: VehicleInfoResponse) {
        childDataHeight = binding.vehiclesSheet.llOutStationRide.height
        binding.vehiclesSheet.llOutStationRide.visibility = View.GONE
        val vehicleFragment = VehicleInfoFragment()
        vehicleFragment.updateData(
            this,
            binding.vehiclesSheet,
            data,
            vehiclesAdapter,
            sourceLatLong!!,
            destinationLatLong!!,
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
            mapBottomMargin = binding.bookRideProgressSheet.bottomSheetLayout.height
            bookProgressSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            if (sourceLatLong != null && destinationLatLong != null) {
                bookedFragment = BookRideProgressFragment()
                bookedFragment?.updateData(binding.bookRideProgressSheet)
                viewModel.bookRideCall(request)
            }
        }
    }
    private fun changeHeader(title: String) {
        binding.layoutSearch.toolbar.tvHeader.text = title
    }

    private fun handleBookRideCall() {
        viewModel.bookRideResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.response.isNotEmpty()) {
                           // updateAfterBookData(data.response[0])
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

}