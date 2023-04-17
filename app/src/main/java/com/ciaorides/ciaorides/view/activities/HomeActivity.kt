package com.ciaorides.ciaorides.view.activities

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
import android.view.Gravity.LEFT
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityHomeBinding
import com.ciaorides.ciaorides.databinding.BottomSheetSearchingBinding
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.ciaorides.ciaorides.model.request.DriverCheckInRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.FcmBookingModel
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.*
import com.ciaorides.ciaorides.view.activities.chat.ChatViewActivity
import com.ciaorides.ciaorides.view.activities.menu.*
import com.ciaorides.ciaorides.view.activities.user.EditProfileActivity
import com.ciaorides.ciaorides.view.adapter.MenuListAdapter
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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private var profileData: UserDetailsResponse.Response? = null
    var googleMap: GoogleMap? = null
    private val viewModel: HomeViewModel by viewModels()
    lateinit var context: Context
    private var driverId = ""

    var selectedVehicleId = ""
    var currentLatLng: LatLng? = null
    private var currentSheetBehavior: BottomSheetBehavior<*>? = null
    private var mapBottomMargin = 0

    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var fcmViewModel: FcmBookingModel? = null
    var broadCastReceiver: BroadcastReceiver? = null
    lateinit var homeBinding: BottomSheetSearchingBinding
    private var onlineSheetBehavior: BottomSheetBehavior<*>? = null
    private var vehicleSheetBehavior: BottomSheetBehavior<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        homeBinding = binding.appBarHome.layoutHome.searchingSheet
        setContentView(binding.root)
        context = this@HomeActivity

        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_my_rides, R.id.nav_my_wallet, R.id.nav_my_vehicles
            ), drawerLayout
        )
        binding.appBarHome.ivMenu.setOnClickListener {
            drawerLayout.openDrawer(LEFT)
        }
        /* val headerBinding = NavHeaderHomeBinding.bind(navView.getHeaderView(0)) // 0-index header
        headerBinding.imageView.setOnClickListener {

        }*/
        setupMenu()

        binding.userDetails.tvEditProfile.setOnClickListener {
            binding.drawerLayout.closeDrawers()
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra(Constants.DATA_VALUE, profileData)
            startActivity(intent)
        }
        initData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupMenu() {
        val adapter = MenuListAdapter()
        binding.llMenu.rvMenu.adapter = adapter
        adapter.MenuItemClicked { title ->
            binding.drawerLayout.closeDrawers()
            when (title) {
                Constants.MENU_MY_RIDES -> {
                    val intent = Intent(this, RidesActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_MY_EARNINGS -> {

                }
                Constants.MENU_MY_VEHICLES -> {
                    val intent = Intent(this, MyVehiclesActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_MY_FAVOURITES -> {
                    val intent = Intent(this, FavActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_BANK_DETAILS -> {
                    val intent = Intent(this, BankDetailsActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_INBOX -> {

                }
                Constants.MENU_REFER_FRIEND -> {

                }
                Constants.MENU_PAYMENTS -> {

                }
                Constants.MENU_ABOUT_US -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.ABOUT)
                    intent.putExtra(Constants.TITLE, Constants.MENU_ABOUT_US)
                    startActivity(intent)
                }
                Constants.MENU_TERMS_N_CONDITIONS -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.TERMS_AND_CONDITIONS)
                    intent.putExtra(Constants.TITLE, Constants.MENU_TERMS_N_CONDITIONS)
                    startActivity(intent)
                }
                Constants.MENU_PRIVACY_POLICY -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.PRIVACY_POLICY)
                    intent.putExtra(Constants.TITLE, Constants.MENU_PRIVACY_POLICY)
                    startActivity(intent)
                }
                Constants.MENU_HELP -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.HELP)
                    intent.putExtra(Constants.TITLE, Constants.MENU_HELP)
                    startActivity(intent)
                }
            }
        }
    }

    private fun initData() {

        setupMap()
        handleBottomSheets()
        driverId = Constants.getValue(this@HomeActivity, Constants.USER_ID)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@HomeActivity)
        checkPermissions()
        handleMyVehicle()
        handleCheckIn()
        handleCheckInStatus()
        handleRejectRideResponse()
        handleBookingInfoResponse()
        handleAcceptBookingResponse()
        handleBookingClicks()
        getHomePageRidesData()
        binding.appBarHome.layoutHome.progressLayout.root.visibility = View.VISIBLE
        viewModel.getHomePageRidesData(GlobalUserIdRequest(driver_id = driverId))
        viewModel.checkInStatus(
            GlobalUserIdRequest(
                driver_id = driverId
            )
        )
        binding.appBarHome.layoutHome.btnStart.setOnClickListener {
            vehiclesCall()
        }
        binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.visibility = View.GONE
        binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.setOnClickListener {
            makeCheckInCall(Constants.ONLINE)
        }
        homeBinding.btnCancel.setOnClickListener {
            makeCheckInCall(Constants.OFFLINE)
        }
        homeBinding.btnPauseSearch.setOnClickListener {
            makeCheckInCall(Constants.OFFLINE)
        }

        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                updateSearchState(Constants.ONLINE)
            }
        }

        LocalBroadcastManager.getInstance(this@HomeActivity)
            .registerReceiver(broadCastReceiver!!, IntentFilter(Constants.FCM_TOKEN))

        binding.appBarHome.layoutHome.localRideSheet.tvChat.setOnClickListener {
            val intent = Intent(this@HomeActivity, ChatViewActivity::class.java)
            intent.putExtra(Constants.DATA_VALUE, fcmViewModel)
            startActivity(intent)
        }

    }

    private fun makeCheckInCall(state: String) {
        viewModel.checkIn(
            DriverCheckInRequest(
                check_in_status = state,
                vehicle_id = selectedVehicleId,
                from_lng = currentLatLng?.longitude.toString(),
                from_lat = currentLatLng?.latitude.toString(),
                driver_id = driverId
            )
        )
    }

    private fun setupMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    private fun vehiclesCall() {
        if (!TextUtils.isEmpty(driverId)) {
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.VISIBLE
            viewModel.getMyVehicles(
                GlobalUserIdRequest(
                    user_id = driverId
                )
            )
        }
    }

    private fun handleMyVehicle() {
        viewModel.myVehicleResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    if (dataHandler.data?.response != null) {
                        dataHandler.data.let { data ->
                            if (data.status) {
                                updateVehicles(data)
                            }
                        }
                    } else {
                        globalAlert(
                            this@HomeActivity,
                            "No vehicles found",
                            "Add vehicle",
                            "Cancel"
                        ) {

                        }
                        /* Toast.makeText(applicationContext, "No vehicles found", Toast.LENGTH_SHORT)
                             .show()*/
                    }

                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleCheckIn() {
        viewModel.checkInResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            updateSearchState(data.otherValue)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleCheckInStatus() {
        viewModel.checkInStatusResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            updateSearchState(data.response.status)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateSearchState(otherValue: String?) {
        if (otherValue == Constants.ONLINE) {
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            mapBottomMargin = binding.appBarHome.layoutHome.searchingSheet.bottomSheetLayout.height
            currentSheetBehavior = onlineSheetBehavior
            onlineSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            checkStateOfBookApi()
        } else if (otherValue == Constants.OFFLINE) {
            mapBottomMargin = 0

            currentSheetBehavior = null
            onlineSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

        } else if (otherValue == Constants.BUSY) {
            mapBottomMargin = 0
            currentSheetBehavior = null
        }
    }

    private fun updateVehicles(vehicleData: MyVehicleResponse) {

        mapBottomMargin = binding.appBarHome.layoutHome.vehiclesSheet.bottomSheetLayout.height
        currentSheetBehavior = vehicleSheetBehavior
        vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED

        binding.appBarHome.layoutHome.vehiclesSheet.rvCars.apply {
            adapter = vehiclesAdapter
        }
        vehiclesAdapter.selectedVehicle { car ->
            selectedVehicleId = car.id
            binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.visibility = View.VISIBLE
        }
        val cars = vehicleData.response.filter {
            it.vehicle_type == "car"
        }
        if (cars.isNotEmpty()) {
            binding.appBarHome.layoutHome.vehiclesSheet.cardCars.visibility = View.VISIBLE
            vehiclesAdapter.differ.submitList(cars)
            vehiclesAdapter.selectedPosition = -1
            vehiclesAdapter.notifyDataSetChanged()
        } else {
            binding.appBarHome.layoutHome.vehiclesSheet.cardCars.visibility = View.GONE
        }

        val bikes = vehicleData.response.filter {
            it.vehicle_type == "bike"
        }
        if (bikes.isNotEmpty()) {
            binding.appBarHome.layoutHome.vehiclesSheet.cardBike.visibility = View.VISIBLE
        } else {
            binding.appBarHome.layoutHome.vehiclesSheet.cardBike.visibility = View.GONE
        }

        val auto = vehicleData.response.filter {
            it.vehicle_type == "auto"
        }
        if (auto.isNotEmpty()) {
            binding.appBarHome.layoutHome.vehiclesSheet.cardAuto.visibility = View.VISIBLE
        } else {
            binding.appBarHome.layoutHome.vehiclesSheet.cardAuto.visibility = View.GONE
        }
        binding.appBarHome.layoutHome.vehiclesSheet.rvCarsMain.setOnClickListener {
            if (binding.appBarHome.layoutHome.vehiclesSheet.rvCars.visibility == View.VISIBLE) {
                manageCar()
            } else {
                selectedVehicleId = ""
                binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.visibility = View.GONE
                hideAllCards()
                binding.appBarHome.layoutHome.vehiclesSheet.rvCars.visibility = View.VISIBLE
                binding.appBarHome.layoutHome.vehiclesSheet.ivDrop.rotation = 180f
                binding.appBarHome.layoutHome.vehiclesSheet.cardCars.strokeColor =
                    ContextCompat.getColor(this@HomeActivity, R.color.appBlue)
            }
        }
        binding.appBarHome.layoutHome.vehiclesSheet.cardAuto.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.appBarHome.layoutHome.vehiclesSheet.cardAuto.strokeColor =
                ContextCompat.getColor(this@HomeActivity, R.color.appBlue)
            selectedVehicleId = auto[0].id
            binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.visibility = View.VISIBLE
        }
        binding.appBarHome.layoutHome.vehiclesSheet.cardBike.setOnClickListener {
            manageCar()
            hideAllCards()
            binding.appBarHome.layoutHome.vehiclesSheet.cardBike.strokeColor =
                ContextCompat.getColor(this@HomeActivity, R.color.appBlue)
            selectedVehicleId = bikes[0].id
            binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.visibility = View.VISIBLE

        }

    }

    private fun hideAllCards() {
        binding.appBarHome.layoutHome.vehiclesSheet.cardAuto.strokeColor =
            ContextCompat.getColor(this@HomeActivity, R.color.grayLight)
        binding.appBarHome.layoutHome.vehiclesSheet.cardBike.strokeColor =
            ContextCompat.getColor(this@HomeActivity, R.color.grayLight)
    }

    private fun manageCar() {
        binding.appBarHome.layoutHome.vehiclesSheet.rvCars.visibility = View.GONE
        binding.appBarHome.layoutHome.vehiclesSheet.ivDrop.rotation = 0f
        // selectedCar = null
        if (vehiclesAdapter.selectedPosition != -1) {
            val temp = vehiclesAdapter.selectedPosition
            vehiclesAdapter.selectedPosition = -1
            vehiclesAdapter.notifyItemChanged(temp)
        }
        binding.appBarHome.layoutHome.vehiclesSheet.cardCars.strokeColor =
            ContextCompat.getColor(this@HomeActivity, R.color.grayLight)
    }


    val callback = OnMapReadyCallback { mapReturn ->
        googleMap = mapReturn
        try {
            googleMap?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    applicationContext,
                    R.raw.map_style
                )
            )
        } catch (e: Resources.NotFoundException) {
        }

        googleMap?.setOnMapClickListener {
            //TODO Get location address and set to destination
        };
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@HomeActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
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
            this@HomeActivity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
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
                            this@HomeActivity,
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
                    Toast.makeText(this@HomeActivity, "Permission Denied", Toast.LENGTH_SHORT)
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
            locationResult.addOnCompleteListener(this@HomeActivity) { task ->
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
                        applicationContext,
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


    override fun onStop() {
        super.onStop()
        broadCastReceiver?.let {
            LocalBroadcastManager.getInstance(this@HomeActivity).unregisterReceiver(
                it
            )
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun checkStateOfBookApi() {
        val messagesRef = Firebase.database.reference.child(FcmBookUtils.BOOKING)
            .child(FcmBookUtils.ACTIVE_BOOKINGS).child(FcmBookUtils.DRIVERS).child(driverId)
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String()::class.java)?.let { getBookingInfo(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getBookingInfo(bookingId: String) {
        val messagesRef =
            Firebase.database.reference.child(FcmBookUtils.BOOKING).child(FcmBookUtils.RIDES)
                .child(bookingId)
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = (snapshot.value as? HashMap<*, *>)

                if (map != null) {
                    if (map.size == 1) {
                        fcmViewModel =
                            snapshot.child(driverId).getValue(FcmBookingModel::class.java)
                    } else {
                        for ((key, value) in map) {
                            if (driverId == key) {
                                fcmViewModel =
                                    snapshot.child(key.toString())
                                        .getValue(FcmBookingModel::class.java)
                            }
                        }
                    }
                    displayStateUi()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun handleBookingClicks() {
        binding.appBarHome.layoutHome.localRideSheet.btnPickup.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.PICKED
            )
        }
        binding.appBarHome.layoutHome.localRideSheet.btnReached.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.REACHED
            )
        }
        binding.appBarHome.layoutHome.localRideSheet.btnComplete.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.RIDE_COMPLETED
            )
        }
        binding.appBarHome.layoutHome.layoutOtp.btnVerify.setOnClickListener {
            val otp = binding.appBarHome.layoutHome.layoutOtp.firstPinView.text.toString()
            fcmViewModel?.let { data ->
                if (otp?.length == 4 && otp.equals(data.otp.toString())) {
                    FcmBookUtils.updateApprovedStatus(
                        fcmViewModel?.bookingNumber.toString(),
                        driverId,
                        Constants.OTP_VALIDATED
                    )
                } else {
                    Toast.makeText(this@HomeActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.appBarHome.layoutHome.localRideSheet.btnAccept.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.APPROVED
            )
            removeOtherDrivers()
            /*val vehicleInfo = bookingFcmResponse.bookingData.response.find {
                it.driver_id == driverId
            }
            vehicleInfo?.let {
                binding.progressLayout.root.visibility = View.VISIBLE
                viewModel.acceptRideRequest(
                    AcceptRideRequest(
                        booking_id = bookingFcmResponse.bookingData.booking_id.toString(),
                        driver_id = it.driver_id,
                        order_id = bookingFcmResponse.bookingData.order_id.toString(),
                        user_id = bookingFcmResponse.bookingData.user_id,
                        vehicle_id = it.vehicle_id
                    ),
                    bookingFcmResponse.bookingData.booking_id.toString()

                )
            }*/
        }
        binding.appBarHome.layoutHome.localRideSheet.btnReject.setOnClickListener {
            // FcmBookUtils.updateApprovedStatus(driverId, Constants.APPROVED)
            /*showRejectReasonsAlert(this@HomeActivity) {
                binding.progressLayout.root.visibility = View.VISIBLE
                viewModel.rejectRide(
                    RejectRideRequest(
                        order_id = bookingFcmResponse.bookingData.order_id.toString(),
                        driver_id = driverId,
                        user_id = bookingFcmResponse.bookingData.user_id
                    )
                )
            }*/
        }
    }

    private fun updateRideDetails(bookingFcmResponse: FcmBookingModel) {
        homeBinding.bottomSheetLayout.visibility = View.GONE
        binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility = View.VISIBLE
        viewModel.getRideDetails(
            GlobalUserIdRequest(
                booking_id = bookingFcmResponse.bookingNumber.toString()
            )
        )
    }

    private fun handleRejectRideResponse() {
        viewModel.rejectRideResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                            binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                View.GONE
                            FcmBookUtils.removeFcmBooingForReject(
                                fcmViewModel?.bookingNumber.toString(),
                                driverId
                            )
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleAcceptBookingResponse() {
        viewModel.acceptRideResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            removeOtherDrivers()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun removeOtherDrivers() {
        FcmBookUtils.getBookingSendersFcmRef()
            .child(fcmViewModel?.bookingNumber.toString())
            .get().addOnSuccessListener {
                val senderIds = it.getValue(String::class.java)
                if (senderIds != null) {
                    val data = senderIds.split(",")
                    for (item in data) {
                        if (item != driverId) {
                            FcmBookUtils.removeFcmBooingForReject(
                                fcmViewModel?.bookingNumber.toString(),
                                item
                            )
                        }
                    }
                }
                FcmBookUtils.updateApprovedStatus(
                    fcmViewModel?.bookingNumber.toString(),
                    driverId,
                    Constants.APPROVED
                )
                FcmBookUtils.getBookingSendersFcmRef()
                    .child(fcmViewModel?.bookingNumber.toString())
                    .setValue(driverId)
            }
    }

    private fun handleBookingInfoResponse() {
        viewModel.bookingInfoResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            with(binding.appBarHome.layoutHome.localRideSheet) {
                                tvSource.text = data.response.from_address
                                tvDestination.text = data.response.to_address
                                tvName.text = data.response.user_details.first_name
                                tvPayment.text = "Rs " + data.response.total_amount
                                if (!android.text.TextUtils.isEmpty(data.response.user_details.profile_pic)) {
                                    com.ciaorides.ciaorides.utils.Constants.showGlide(
                                        this@HomeActivity,
                                        com.ciaorides.ciaorides.BuildConfig.IMAGE_BASE_URL + data.response.user_details.profile_pic,
                                        profileImage
                                    )
                                }
                            }
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun displayStateUi() {
        if (fcmViewModel != null) {
            fcmViewModel?.let { fcmResponse ->
                with(binding.appBarHome.layoutHome.localRideSheet) {
                    when (fcmResponse.rideStatus) {
                        com.ciaorides.ciaorides.utils.Constants.PENDING -> {
                            updateRideDetails(fcmResponse)
                            btnAccept.visible(true)
                            btnReached.visible(false)
                            btnPickup.visible(false)
                        }
                        com.ciaorides.ciaorides.utils.Constants.APPROVED -> {
                            tvCongratsMsg.text = "Enjoy your ride!"
                            btnAccept.visible(false)
                            btnReached.visible(true)
                            btnPickup.visible(false)
                            tvHeader.visible(false)
                        }
                        com.ciaorides.ciaorides.utils.Constants.PICKED -> {
                            tvCongratsMsg.text = "Enjoy your ride!"
                            btnAccept.visible(false)
                            btnReached.visible(false)
                            btnPickup.visible(true)
                            btnReject.visible(false)
                            tvHeader.visible(false)
                        }
                        com.ciaorides.ciaorides.utils.Constants.REACHED -> {
                            tvCongratsMsg.text = "Enjoy your ride!"
                            binding.appBarHome.layoutHome.layoutOtp.layoutOtpScreen.visible(true)
                            binding.appBarHome.layoutHome.layoutOtp.tvSource.text =
                                fcmResponse.sourceAddress
                            binding.appBarHome.layoutHome.layoutOtp.tvDestination.text =
                                fcmResponse.destinationAddress
                            tvHeader.visible(false)
                        }
                        com.ciaorides.ciaorides.utils.Constants.OTP_VALIDATED -> {
                            tvCongratsMsg.text = "Enjoy your ride!"
                            btnAccept.visible(false)
                            tvHeader.visible(false)
                            btnReached.visible(false)
                            btnPickup.visible(false)
                            btnReject.visible(false)
                            binding.appBarHome.layoutHome.layoutOtp.layoutOtpScreen.visible(false)
                            bottomSheetLayout.visible(true)
                            btnComplete.visible(true)

                            tvSource.text =
                                fcmResponse.sourceAddress
                            tvDestination.text =
                                fcmResponse.destinationAddress
                            tvPayment.text =
                                fcmResponse.time
                        }
                        com.ciaorides.ciaorides.utils.Constants.RIDE_COMPLETED -> {
                            updateSearchState(com.ciaorides.ciaorides.utils.Constants.ONLINE)
                        }
                    }
                }
            }

        } else {
            homeBinding.bottomSheetLayout.visibility = View.VISIBLE
            binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility = View.GONE
        }
    }

    private fun getHomePageRidesData() {
        viewModel.homePageRidesResponse.observe(this@HomeActivity) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            with(binding) {
                                appBarHome.layoutHome.tvTotalBookings.text =
                                    getPrice(data.response.total_bookings)
                                appBarHome.layoutHome.tvTotalEarnings.text =
                                    getPrice(data.response.total_earnings)
                                if (data.response.previous_booking_data.isNotEmpty()
                                ) {
                                    appBarHome.layoutHome.llPrevRidesLayout.visibility =
                                        View.VISIBLE
                                    appBarHome.layoutHome.tvPreviousRides.text =
                                        getPrice(data.response.previous_booking_data[0].total_amount)
                                } else {
                                    appBarHome.layoutHome.llPrevRidesLayout.visibility = View.GONE
                                }

                            }

                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun handleBottomSheets() {
        onlineSheetBehavior = BottomSheetBehavior.from(homeBinding.bottomSheetLayout)
        onlineSheetBehavior?.setBottomSheetCallback(handler)
        onlineSheetBehavior?.peekHeight = 0

        vehicleSheetBehavior =
            BottomSheetBehavior.from(binding.appBarHome.layoutHome.vehiclesSheet.bottomSheetLayout)
        vehicleSheetBehavior?.setBottomSheetCallback(handler)
        vehicleSheetBehavior?.peekHeight = 0

    }

    private val handler = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.appBarHome.layoutHome.mapFrame.setPadding(
                    0,
                    0,
                    0,
                    mapBottomMargin - 30
                )

            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.appBarHome.layoutHome.mapFrame.setPadding(0, 0, 0, 0)
            } else if (newState == BottomSheetBehavior.STATE_DRAGGING) bottomSheet.post {
                currentSheetBehavior?.setState(
                    BottomSheetBehavior.STATE_EXPANDED
                )
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }


}