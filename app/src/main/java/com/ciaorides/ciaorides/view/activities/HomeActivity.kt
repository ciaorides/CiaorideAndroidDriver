package com.ciaorides.ciaorides.view.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Rational
import android.view.Gravity.LEFT
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityHomeBinding
import com.ciaorides.ciaorides.databinding.BottomSheetSearchingBinding
import com.ciaorides.ciaorides.databinding.SupportAlertBinding
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.ciaorides.ciaorides.fcm.OreoNotification
import com.ciaorides.ciaorides.model.request.AcceptRideRequest
import com.ciaorides.ciaorides.model.request.CompleteOfferRideRequest
import com.ciaorides.ciaorides.model.request.DriverCheckInRequest
import com.ciaorides.ciaorides.model.request.EndRideRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.request.PickUpRideRequest
import com.ciaorides.ciaorides.model.request.RejectRideRequest
import com.ciaorides.ciaorides.model.response.FcmBookingModel
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.services.LocationService
import com.ciaorides.ciaorides.utils.BookType
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.utils.SweetAlertDialog
import com.ciaorides.ciaorides.utils.getPrice
import com.ciaorides.ciaorides.utils.globalAlert
import com.ciaorides.ciaorides.utils.showRejectReasonsAlert
import com.ciaorides.ciaorides.utils.visible
import com.ciaorides.ciaorides.view.activities.chat.ChatViewActivity
import com.ciaorides.ciaorides.view.activities.menu.BankDetailsActivity
import com.ciaorides.ciaorides.view.activities.menu.EarningsActivity
import com.ciaorides.ciaorides.view.activities.menu.FavActivity
import com.ciaorides.ciaorides.view.activities.menu.InboxActivity
import com.ciaorides.ciaorides.view.activities.menu.MyVehiclesActivity
import com.ciaorides.ciaorides.view.activities.menu.PaymentsActivity
import com.ciaorides.ciaorides.view.activities.menu.RidesActivity
import com.ciaorides.ciaorides.view.activities.menu.SettingsActivity
import com.ciaorides.ciaorides.view.activities.menu.StaticPagesActivity
import com.ciaorides.ciaorides.view.activities.menu.WalletActivity
import com.ciaorides.ciaorides.view.activities.ui.vehicleDetails.VehicleDetailsActivity
import com.ciaorides.ciaorides.view.activities.user.EditProfileActivity
import com.ciaorides.ciaorides.view.adapter.MenuListAdapter
import com.ciaorides.ciaorides.view.adapter.VehiclesAdapter
import com.ciaorides.ciaorides.viewmodel.HomeViewModel
import com.github.angads25.toggle.widget.LabeledSwitch
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    @Inject
    lateinit var vehiclesAdapter: VehiclesAdapter
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var profileData: UserDetailsResponse.Response? = null
    var googleMap: GoogleMap? = null
    private val viewModel: HomeViewModel by viewModels()
    lateinit var context: Context

    var selectedVehicleId = ""
    var currentLatLng: LatLng? = null
    private var currentSheetBehavior: BottomSheetBehavior<*>? = null
    private var mapBottomMargin = 0

    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    companion object {
        var fcmViewModel: FcmBookingModel? = null
        var driverId = ""
    }
    var broadCastReceiver: BroadcastReceiver? = null
    lateinit var homeBinding: BottomSheetSearchingBinding
    private var onlineSheetBehavior: BottomSheetBehavior<*>? = null
    private var vehicleSheetBehavior: BottomSheetBehavior<*>? = null
    var checked_in_state = Constants.OFFLINE

    lateinit var sourceLatLng : LatLng
    lateinit var destinationLatLng : LatLng
    lateinit var mobileNumber : String
    var isOtpValidated : Boolean = false

    var bookingId = ""
    var order_id = ""
    var rider_id = ""
    var user_id = ""
    var ride_start_time = ""
    var ride_end_time = ""
    var total_ride_amount = ""
    var ride_status = ""

    val CHANNEL_ID: String = "CIAORides"
    val CHANNEL_NAME: String = "CIAORides"

    override fun init() {
        homeBinding = binding.appBarHome.layoutHome.searchingSheet
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
        Constants.showImage(
            binding.appBarHome.ivProfileImage.context,
            Constants.getValue(
                this,
                Constants.USER_IMAGE
            ), binding.appBarHome.ivProfileImage
        )
        Constants.showImage(
            binding.userDetails.imageView.context,
            Constants.getValue(
                this,
                Constants.USER_IMAGE
            ), binding.userDetails.imageView
        )
    }

    override fun getViewBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(layoutInflater)

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
                Constants.MENU_WALLET -> {
                    val intent = Intent(this, WalletActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_MY_EARNINGS -> {
                    val intent = Intent(this, EarningsActivity::class.java)
                    startActivity(intent)

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
                    val intent = Intent(this, InboxActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_REFER_FRIEND -> {
                    // mDrawerLayout.openDrawer(Gravity.START);
                    ShareCompat.IntentBuilder.from(this@HomeActivity)
                        .setType("text/plain")
                        .setChooserTitle("Share CIAO Rides App")
                        .setText("https://www.ciaorides.com/")
                        .startChooser()
                }
                Constants.MENU_PAYMENTS -> {
                    val intent = Intent(this, PaymentsActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_SETTINGS -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
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

    fun openWhatsApp(phoneNumber: String, message: String) {
        val packageManager = packageManager
        val i = Intent(Intent.ACTION_VIEW)

        try {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${
                URLEncoder.encode(
                    message,
                    "UTF-8"
                )
            }"
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
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
        handleCompleteTaxiRideCall()
        handleEndTaxiRideCall()
        handleCurrentRidesCall()
        binding.appBarHome.layoutHome.progressLayout.root.visibility = View.VISIBLE
        viewModel.getUserDetails(GlobalUserIdRequest(user_id = driverId))

        handleUserResponse()
        binding.appBarHome.layoutHome.btnStart.setOnClickListener {
            vehiclesCall()
        }
        binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.visibility = View.GONE
        binding.appBarHome.layoutHome.vehiclesSheet.btnStartRide.setOnClickListener {
            checked_in_state = Constants.ONLINE
            makeCheckInCall(Constants.ONLINE)
        }
        binding.appBarHome.layoutHome.driverStatus.setOnToggledListener { labeledSwitch, isOn ->
            // Implement your switching logic here
            if (isOn){
                checked_in_state = Constants.ONLINE
                makeCheckInCall(Constants.ONLINE)
            } else {
                checked_in_state = Constants.OFFLINE
                makeCheckInCall(Constants.OFFLINE)
            }
        }
        binding.appBarHome.layoutHome.vehiclesSheet.btnCancelRide.setOnClickListener {
            checked_in_state = Constants.OFFLINE
            makeCheckInCall(Constants.OFFLINE)
            binding.appBarHome.layoutHome.vehiclesSheet.bottomSheetLayout.visibility = View.GONE
            vehicleSheetBehavior = BottomSheetBehavior.from(binding.appBarHome.layoutHome.vehiclesSheet.bottomSheetLayout)
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        homeBinding.btnCancel.setOnClickListener {
            checked_in_state = Constants.OFFLINE
            makeCheckInCall(Constants.OFFLINE)
        }
        homeBinding.btnPauseSearch.setOnClickListener {
            checked_in_state = Constants.OFFLINE
            makeCheckInCall(Constants.OFFLINE)
        }

        broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                Log.d("Driver", "FCM Broadcast Received")
                Toast.makeText(contxt, "Reciev", Toast.LENGTH_SHORT).show()
                checked_in_state = Constants.ONLINE
//                updateSearchState(Constants.ONLINE)
            }
        }

        LocalBroadcastManager.getInstance(this@HomeActivity)
            .registerReceiver(broadCastReceiver!!, IntentFilter(Constants.FCM_TOKEN))

        binding.appBarHome.layoutHome.localRideSheet.tvChat.setOnClickListener {
            val intent = Intent(this@HomeActivity, ChatViewActivity::class.java)
            intent.putExtra(Constants.DATA_VALUE, fcmViewModel)
            startActivity(intent)
        }

        binding.appBarHome.layoutHome.localRideSheet.cardCall.setOnClickListener {
            fcmViewModel?.let {
                Toast.makeText(this, "Call${it.userMobile}", Toast.LENGTH_SHORT).show()
                it.userMobile?.let {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:$it")
                        }
                        startActivity(intent)
                       /* if (checkPermissionState()) {
                            Log.d("Call", "Call dialed")

                        } else {
                            TedPermission.create()
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.CALL_PHONE)
                                .check();
                        }*/
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

        binding.appBarHome.layoutHome.localRideSheet.cardLocation.setOnClickListener {
            if(isOtpValidated){
                fcmViewModel?.let {
                    val mapUri = Uri.parse("geo:0,0?q=loc:${it.userToLat},${it.userToLng}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            } else {
                fcmViewModel?.let {
                    val mapUri = Uri.parse("geo:0,0?q=loc:${it.userFromLat},${it.userFromLng}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                    /*try {
                        val mapUri = Uri.parse("geo:0,0?q=loc:${it.latitude},${it.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                    } catch (ane: ActivityNotFoundException){
                        Toast.makeText(this, "Please Install Google Maps ", Toast.LENGTH_LONG).show();
                    }catch (ex:Exception){
                        Log.d("Error", ex.stackTrace.toString())
                    }*/
                }
            }
        }
        checkRides()
    }

    private fun checkRides() {
        try {
            val globalUserIdRequest = GlobalUserIdRequest(
                user_id = Constants.getValue(this, Constants.USER_ID),
                user_type = "DRIVER"
            )
            viewModel.checkRides(globalUserIdRequest)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleCurrentRidesCall() {
        viewModel.checkRides.observe(this){ dataHandler ->
            when(dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status){
                            checkStateOfBookApi()
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }
        }
    }

    private fun handleEndTaxiRideCall() {
        viewModel.endUpRideResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            if (data.response.total_amount != null) {
                                FcmBookUtils.updateAmount(
                                    bookingId,
                                    rider_id,
                                    data.response.total_amount.toString()
                                )
                                binding.appBarHome.layoutHome.localRideSheet.tvPayment.text = data.response.total_amount.toString()
                            }
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

    private fun makeCheckInCall(state: String) {
        if (state == Constants.OFFLINE){
            // Cancel notification
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.cancel(1)

            val serviceIntent = Intent(applicationContext, LocationService::class.java)
            stopService(serviceIntent)
            binding.appBarHome.layoutHome.driverStatus.isOn = false
        } else {
            binding.appBarHome.layoutHome.driverStatus.isOn = true
        }
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
                    user_id = driverId,
                    driver_id = driverId
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
                                val verifiedVehicleList = ArrayList<MyVehicleResponse.Response>()
                                for (vehicle in data.response) {
                                    if (vehicle.vehicle_step1 == Constants.YES && vehicle.vehicle_step2 == Constants.YES && vehicle.vehicle_step3 == Constants.YES && vehicle.vehicle_verified == Constants.YES) {
                                        verifiedVehicleList.add(vehicle)
                                    }
                                }
                                if (verifiedVehicleList.isNotEmpty()) {
                                    updateVehicles(data)
                                } else if (data.response.isNotEmpty()) {
                                    globalAlert(
                                        this@HomeActivity,
                                        "You vehicle(s) not verified or updated. Please update",
                                        "Update",
                                        "Dismiss"
                                    ) {
                                        if (it) {
                                            val intent =
                                                Intent(this, MyVehiclesActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                } else {
                                    globalAlert(
                                        this@HomeActivity,
                                        "Vehicles not added. Please add",
                                        "Add",
                                        "Dismiss"
                                    ) {
                                        if (it) {
                                            val intent =
                                                Intent(this, VehicleDetailsActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }
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
                is DataHandler.LOADING -> {

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
                            updateSearchState(data.otherValue.toString())
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

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
                is DataHandler.LOADING -> {

                }
            }
        }
    }

    private fun updateSearchState(otherValue: String?) {
        if (checked_in_state == Constants.ONLINE || otherValue.toString().toLowerCase() == Constants.ONLINE.toLowerCase()) {
            vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            mapBottomMargin = binding.appBarHome.layoutHome.searchingSheet.bottomSheetLayout.height
            currentSheetBehavior = onlineSheetBehavior
            homeBinding.bottomSheetLayout.visibility = View.VISIBLE
            onlineSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
            binding.appBarHome.layoutHome.driverStatus.isOn = true
            checkStateOfBookApi()
        } else if (checked_in_state == Constants.OFFLINE || otherValue.toString().toLowerCase() == Constants.OFFLINE.toLowerCase()) {
            mapBottomMargin = 0

            currentSheetBehavior = null
            onlineSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.appBarHome.layoutHome.driverStatus.isOn = false
            homeBinding.bottomSheetLayout.visibility = View.GONE
        } else if (checked_in_state == Constants.BUSY) {
            mapBottomMargin = 0
            currentSheetBehavior = null
            binding.appBarHome.layoutHome.driverStatus.isOn = false
            homeBinding.bottomSheetLayout.visibility = View.GONE
        }
    }

    private fun updateVehicles(vehicleData: MyVehicleResponse) {

        mapBottomMargin = binding.appBarHome.layoutHome.vehiclesSheet.bottomSheetLayout.height
        currentSheetBehavior = vehicleSheetBehavior
        vehicleSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        binding.appBarHome.layoutHome.vehiclesSheet.bottomSheetLayout.visibility = View.VISIBLE

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
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.uiSettings?.isZoomGesturesEnabled = false
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@HomeActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
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
             val latitute = location.latitude
             val longitute = location.longitude
              Log.i("test", "Latitute: $latitute ; Longitute: $longitute")
            val serviceIntent = Intent(applicationContext, LocationService::class.java)
            startService(serviceIntent)
            /*if(fcmViewModel!= null && fcmViewModel?.bookingNumber!=null && latitute!=null && longitute!=null){
                if (fcmViewModel!!.rideStatus == Constants.APPROVED || fcmViewModel!!.rideStatus == Constants.OTP_VALIDATED) {
                    FcmBookUtils.updateDriverLocation(
                        fcmViewModel?.bookingNumber.toString(),
                        driverId,
                        latitute,
                        longitute
                    )
                }
            }*/
            this.googleMap?.clear()
            currentLatLng = LatLng(
                latitute,
                longitute
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
//            Log.d("##LastLocation", " lat : ${fusedLocationProviderClient.lastLocation.result.latitude}" )
//            Log.d("##LastLocation", " long : ${fusedLocationProviderClient.lastLocation.result.longitude}")
            locationResult.addOnCompleteListener(this@HomeActivity) { task ->
                if (task.isSuccessful) {
                    Log.d("##Location", "task.isSuccessful")
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        Log.d("##LocationResult", "lastKnownLocation not null ${lastKnownLocation.toString()}")
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

    private fun checkStateOfBookApi() {
        val messagesRef = Firebase.database.reference.child(FcmBookUtils.BOOKING)
            .child(FcmBookUtils.ACTIVE_BOOKINGS).child(FcmBookUtils.DRIVERS).child(driverId)
        /*messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.key!!)

                // A new comment has been added, add it to the displayed list
                val comment = dataSnapshot.getValue<Comment>()

                dataSnapshot.getValue(String()::class.java)?.let {
                    Log.d("Driver", "check state of book api called")
                    getBookingInfo(it)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                dataSnapshot.getValue(String()::class.java)?.let {
                    Log.d("Driver", "check state of book api called")
                    getBookingInfo(it)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataSnapshot.getValue<Comment>()
                val commentKey = dataSnapshot.key

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                Toast.makeText(
                    context,
                    "Failed to load comments.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        })*/

        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(String()::class.java)?.let {
                    Log.d("Driver", "check state of book api called")
                    getBookingInfo(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun data(){
        val messagesRef =
            Firebase.database.reference.child(FcmBookUtils.BOOKING).child(FcmBookUtils.RIDES)
                .child(bookingId)
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the entire JSON string from the snapshot
                val json = dataSnapshot.value.toString()
                Log.d("Snapshot Data (JSON)", json)
           }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getBookingInfo(bookingId: String) {
        val messagesRef =
            Firebase.database.reference.child(FcmBookUtils.BOOKING).child(FcmBookUtils.RIDES)
                .child(bookingId)
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FcmViewModel", "Is fcmViewModel bookingNumber value? ${fcmViewModel?.bookingNumber}")
                Log.d("FcmViewModel", "Is fcmViewModel ride status? ${fcmViewModel?.rideStatus}")
                Log.d("FcmViewModel", "ride status? ${ride_status}")

                val map = (snapshot.value as? HashMap<*, *>)
                val model = snapshot.child(driverId).getValue(FcmBookingModel::class.java)

                // For the first time fcm will be null
                if (fcmViewModel == null || (fcmViewModel!= null && fcmViewModel!!.bookingNumber == null && fcmViewModel!!.rideStatus == "payment_completed")){
                    Log.d("FcmViewModel", "FCM Null")
                    getRideData(map, snapshot)
                }
                else {
                    Log.d("FcmViewModel", "FCM Non Null")
                    if (fcmViewModel?.orderId != null) { // To avoid dummy data. Only if order is available, will display the data.
                        // Once the ride data is added, it will not be null
                        // From the second time onwards, for same booking id, unless the status changes, should not load the data again.
                        if (fcmViewModel != null && fcmViewModel?.bookingNumber == bookingId && fcmViewModel?.rideStatus != null && model?.rideStatus != fcmViewModel?.rideStatus) {
                            Log.d("FcmViewModel", "Update Data called")
                            getRideData(map, snapshot)
                        }
                        // For a new request booking id will be changed.
                        else if (fcmViewModel?.bookingNumber != bookingId) {
                            Log.d("FcmViewModel", "FCM new booking")
                            getRideData(map, snapshot)
                        }

                        // Sometimes the data is not loading correctly, so based on condition popup is showing
                        if (fcmViewModel?.bookingNumber != null && fcmViewModel?.rideStatus != Constants.RIDE_CANCELLED && fcmViewModel?.rideStatus != Constants.PAYMENT_COMPLETED &&
                            fcmViewModel?.rideStatus != Constants.REJECTED
                        ) {
                            if(binding.appBarHome.layoutHome.localRideSheet.root.visibility == View.GONE) {
                                Log.d("FcmViewModel", "UI manually rendered")
                                getRideData(map, snapshot)
                            }
                        }
                    } else {
                        if(binding.appBarHome.layoutHome.localRideSheet.root.visibility == View.VISIBLE) {
                            binding.appBarHome.layoutHome.localRideSheet.root.visibility = View.GONE
                            Log.d("FcmViewModel", "FCM Disabled")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    /*// For first time FCM model would be empty
               // Second time onwards, for same booking id, unless the status changes, should not load the data again.
               Log.d("Driver", "Is fcmViewModel null? ${fcmViewModel == null}")
               Log.d("Driver", "Is fcmViewModel bookingNumber? ${fcmViewModel?.bookingNumber == bookingId}")
               Log.d("Driver", "Is fcmViewModel rideStatus? ${fcmViewModel?.rideStatus != ride_status}")
               Log.d("Driver", "Is fcmViewModel bookingNumber value? ${fcmViewModel?.bookingNumber}")
               Log.d("Driver", "Is fcmViewModel bookingId value? ${bookingId}")
               Log.d("Driver", "Is fcmViewModel rideStatus Value? ${ride_status}")
               Log.d("Driver", "Is fcmViewModel rideStatus fcmValue? ${fcmViewModel?.rideStatus}")

               // Second time for a new request booking id will be changed.
               val model = snapshot.child(driverId).getValue(FcmBookingModel::class.java)
               Log.d("Driver", "Is New Request? ${model?.bookingNumber != bookingId}")
               Log.d("Driver", "New Request Booking Number ${model?.bookingNumber }")
               if (fcmViewModel == null || (fcmViewModel?.bookingNumber == bookingId && fcmViewModel?.rideStatus != ride_status)) {
                   // Old data
                   getRideData(map, snapshot)
               }else if (fcmViewModel!= null && model!!.bookingNumber == bookingId && model!!.rideStatus == Constants.PENDING){
                   // Its a new request
                   getRideData(map, snapshot)
               }*/

    private fun getRideData(map: java.util.HashMap<*, *>?, snapshot: DataSnapshot) {
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
            Log.d("FcmViewModel", "FCM Display state method called")
            fcmViewModel?.let {
                if(it.rideStatus == Constants.PENDING)
                    showNotification()
            }
            displayStateUi()
        }
    }

    private fun handleBookingClicks() {
        binding.appBarHome.layoutHome.localRideSheet.btnPickup.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.PICKED
            )
            ride_status = Constants.PICKED
        }
        binding.appBarHome.layoutHome.localRideSheet.btnReached.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.REACHED
            )
            ride_status = Constants.REACHED
        }
        binding.appBarHome.layoutHome.localRideSheet.btnComplete.setOnClickListener {
            Constants.showWarningSweetAlert(this, SweetAlertDialog.WARNING_TYPE, "", "Are you sure to end ride?", "Yes", { confirmed ->
                if (confirmed) {
                    // Action to take when confirmed
                    val addresses: List<Address>

                    val geocoder = Geocoder(this, Locale.getDefault())

                    addresses = geocoder.getFromLocation(
                        currentLatLng?.latitude!!,
                        currentLatLng?.longitude!!,
                        1
                    )!! // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    if(addresses.isNotEmpty()) {
                        viewModel.endTaxiTrip(
                            EndRideRequest(
                                to_address = addresses.get(0).getAddressLine(0),
                                to_lat = currentLatLng?.latitude.toString(),
                                to_lng = currentLatLng?.longitude.toString(),
                                booking_id = bookingId,

                                )
                        )
                        FcmBookUtils.updateApprovedStatus(
                            fcmViewModel?.bookingNumber.toString(),
                            driverId,
                            Constants.RIDE_COMPLETED
                        )
                        ride_status = Constants.RIDE_COMPLETED
                    }
                } else {
                    // Action to take when not confirmed (if applicable)
                }
            } )
        }
        binding.appBarHome.layoutHome.localRideSheet.btnPayment.setOnClickListener {
            callTaxiPaymentCompleteAPI()

            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.PAYMENT_COMPLETED
            )
            ride_status = Constants.PAYMENT_COMPLETED
        }
        binding.appBarHome.layoutHome.layoutOtp.btnVerify.setOnClickListener {
            try {
                val otp = binding.appBarHome.layoutHome.layoutOtp.firstPinView.text.toString()
                fcmViewModel?.let { data ->
    //                if (otp?.length == 4 && otp.equals(data.otp.toString())) {
                    if (otp?.length == 4 && otp.equals(data.otp.toString())) {
                        FcmBookUtils.updateApprovedStatus(
                            fcmViewModel?.bookingNumber.toString(),
                            driverId,
                            Constants.OTP_VALIDATED
                        )

                        ride_status = Constants.OTP_VALIDATED
                        // Call pickup ride api call
                        val pickUpRideRequest = PickUpRideRequest(
                            driverId,
                            selectedVehicleId,
                            order_id
                        )

                        viewModel.pickUpTaxiTrip(pickUpRideRequest)
                    } else {
                        Toast.makeText(this@HomeActivity, "Enter OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                    e.printStackTrace()
            }
        }
        binding.appBarHome.layoutHome.localRideSheet.btnAccept.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(
                fcmViewModel?.bookingNumber.toString(),
                driverId,
                Constants.APPROVED
            )
            ride_status = Constants.APPROVED
            removeOtherDrivers()
            fcmViewModel?.let {
                viewModel.acceptRideRequest(
                    AcceptRideRequest(
                        booking_id = fcmViewModel?.bookingNumber!!,
                        driver_id = fcmViewModel?.driverInfo!!.driver_id,
                        order_id = fcmViewModel?.orderId!!,
                        user_id = fcmViewModel?.userId!!,
                        vehicle_id = fcmViewModel?.driverInfo!!.vehicle_id
                    ),
                    it.bookingNumber
                )
            }
        }
        binding.appBarHome.layoutHome.localRideSheet.btnReject.setOnClickListener {
            FcmBookUtils.updateApprovedStatus(fcmViewModel?.bookingNumber!!.toString(), driverId, Constants.REJECTED)
            ride_status = Constants.REJECTED
            showRejectReasonsAlert(this@HomeActivity) {
                viewModel.rejectRide(
                    RejectRideRequest(
                        order_id = fcmViewModel?.orderId!!,
                        driver_id = driverId,
                        user_id = fcmViewModel?.userId!!
                    )
                )
            }
        }

        binding.appBarHome.layoutHome.localRideSheet.btnSupport.setOnClickListener {
            if (bookingId != null) {
                showSupportDialog("Booking Id : $bookingId \n Write your query!")
            } else {
                showSupportDialog("Write your query!")
            }
        }
    }

    fun showSupportDialog(text : String) {
        val builder = AlertDialog.Builder(this)
            .create()
        val scheduleBinding =
            SupportAlertBinding.inflate(LayoutInflater.from(this), null, false)
        builder.setView(scheduleBinding.root)
        scheduleBinding.btnCall.setOnClickListener {
            builder.dismiss()
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$9441500416")
            }
            startActivity(intent)
        }
        scheduleBinding.btnChat.setOnClickListener {
            builder.dismiss()
            openWhatsApp("+919441500416", text)
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }


    private fun callTaxiPaymentCompleteAPI() {
        if (!TextUtils.isEmpty(Constants.getValue(this@HomeActivity, Constants.USER_ID))) {
            viewModel.completeTaxiTrip(
                CompleteOfferRideRequest(
                    amount = total_ride_amount,
                    order_id = order_id ,
                    rider_id = rider_id,
                    user_id = user_id,
                    booking_id = bookingId,
                    transaction_id = "",
                    payment_gateway_provider = "cash",
                    payment_status = "paid",
                    ride_start_time = ride_start_time,
                    ride_end_time = ride_end_time,
                    payment_type = "cash"
                )
            )
        }
    }

    private fun updateRideDetails(bookingFcmResponse: FcmBookingModel) {
        try {
            Log.d("Driver", "Update Ride Details Called")
            with(binding.appBarHome.layoutHome.localRideSheet) {
            fcmViewModel?.let {
                tvSource.text = it.sourceAddress
                tvDestination.text = it.destinationAddress
                tvName.text = it.userName
                tvPayment.text = "Rs ${it.amount}"

                order_id = it.orderId
                profileImage.setImageDrawable(getDrawable(R.drawable.ic_user))
//                Constants.showImage(this@HomeActivity, it.userPic, profileImage)
            }
        }
        }catch (e: Exception){
            e.printStackTrace()


        }


        homeBinding.bottomSheetLayout.visibility = View.GONE
        binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility = View.VISIBLE
        viewModel.getRideDetails(
            GlobalUserIdRequest(
                driver_id = bookingFcmResponse.driverInfo.driver_id.toString(),
                booking_id = bookingFcmResponse.bookingNumber.toString()
            )
        )
        /*with(binding.appBarHome.layoutHome.localRideSheet) {
            tvSource.text = bookingFcmResponse.sourceAddress
            tvDestination.text = bookingFcmResponse.destinationAddress
            tvName.text = bookingFcmResponse.userId
            tvPayment.text = "Rs 160"
            if (!android.text.TextUtils.isEmpty(bookingFcmResponse.userPic)) {
                com.ciaorides.ciaorides.utils.Constants.showGlide(
                    this@HomeActivity,
                    com.ciaorides.ciaorides.BuildConfig.IMAGE_BASE_URL + bookingFcmResponse.userPic,
                    profileImage
                )
            }
        }*/
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
                is DataHandler.LOADING -> {

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
                is DataHandler.LOADING -> {

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
//                                tvName.text = data.response.user_details.first_name
                                tvPayment.text = "Rs ${data.response.total_amount}"
                                sourceLatLng = LatLng(data.response.from_lat.toDouble(), data.response.from_lng.toDouble())
                                destinationLatLng = LatLng(data.response.to_lat.toDouble(), data.response.to_lng.toDouble())
                                mobileNumber = data.response.user_details.mobile
                               /* if (!android.text.TextUtils.isEmpty(data.response.user_details.profile_pic)) {
                                    com.ciaorides.ciaorides.utils.Constants.showGlide(
                                        this@HomeActivity,
                                        data.response.user_details.profile_pic,
                                        profileImage
                                    )
                                }*/

                                bookingId = data.response.booking_id
                                total_ride_amount = data.response.total_amount
                                rider_id = data.response.rider_id
                                user_id = data.response.user_id

                                fcmViewModel?.let {
                                    FcmBookUtils.updateAmountLatLng(
                                        fcmViewModel?.bookingNumber.toString(),
                                        driverId,
                                        data.response.total_amount,
                                        data.response.from_lat,
                                        data.response.from_lng,
                                        data.response.to_lat,
                                        data.response.to_lng,
                                        data.response.user_details.mobile,
                                        data.response.user_details.first_name+" "+data.response.user_details.last_name
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
                is DataHandler.LOADING -> {

                }
            }
        }
    }


    private fun displayStateUi() {
        try {
            if (fcmViewModel != null) {
                fcmViewModel?.let { fcmResponse ->
                    with(binding.appBarHome.layoutHome.localRideSheet) {
                        when (fcmResponse.rideStatus) {
                            com.ciaorides.ciaorides.utils.Constants.PENDING -> {
                                ride_status = Constants.PENDING
                                Log.d(" FCMDriver", "FCM Pending")
                                updateRideDetails(fcmResponse)
                                tvCongratsMsg.text = "Accept your ride!"
                                btnAccept.visible(true)
                                btnReject.visible(true)
                                btnReached.visible(false)
                                btnPickup.visible(false)
                                btnPayment.visible(false)
                                btnComplete.visible(false)
                            }

                            Constants.STARTED -> {
                                if(fcmResponse.rideType == BookType.LATER.name){
                                    Log.d("FCMDriver", "FCM Approved")
                                    ride_status = Constants.APPROVED
                                    updateRideDetails(fcmResponse)
                                    tvCongratsMsg.text = "Enjoy your ride!"
                                    btnAccept.visible(false)
                                    btnReject.visible(false)
                                    btnReached.visible(true)
                                    btnPickup.visible(false)
                                    btnPayment.visible(false)
                                    btnComplete.visible(false)
                                    tvHeader.visible(false)
                                    homeBinding.bottomSheetLayout.visible(false)
                                    bottomSheetLayout.visible(true)

                                    binding.appBarHome.layoutHome.layoutOtp.tvSource.text =
                                        fcmResponse.sourceAddress
                                    binding.appBarHome.layoutHome.layoutOtp.tvDestination.text =
                                        fcmResponse.destinationAddress
                                }
                            }
                            Constants.APPROVED -> {
                                if(fcmResponse.rideType == BookType.LATER.name){
                                    updateSearchState(Constants.ONLINE)
                                    homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                                    binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                        View.GONE
                                } else {
                                    Log.d("FCMDriver", "FCM Approved")
                                    ride_status = Constants.APPROVED
                                    updateRideDetails(fcmResponse)
                                    tvCongratsMsg.text = "Enjoy your ride!"
                                    btnAccept.visible(false)
                                    btnReject.visible(false)
                                    btnReached.visible(true)
                                    btnPickup.visible(false)
                                    btnPayment.visible(false)
                                    btnComplete.visible(false)
                                    tvHeader.visible(false)
                                    homeBinding.bottomSheetLayout.visible(false)
                                    bottomSheetLayout.visible(true)

                                    binding.appBarHome.layoutHome.layoutOtp.tvSource.text =
                                        fcmResponse.sourceAddress
                                    binding.appBarHome.layoutHome.layoutOtp.tvDestination.text =
                                        fcmResponse.destinationAddress
                                }
                            }
                            // Once the ride is started, the communication is from home screen to driver screen
                            com.ciaorides.ciaorides.utils.Constants.PICKED -> {
                                if(fcmResponse.rideType == BookType.LATER.name){
                                    homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                                    binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                        View.GONE
                                } else {
                                    Log.d("FCMDriver", "FCM PICKED")
                                    ride_status = Constants.PICKED
                                    updateRideDetails(fcmResponse)
                                    tvCongratsMsg.text = "Enjoy your ride!"
                                    btnAccept.visible(false)
                                    btnReached.visible(false)
                                    btnPickup.visible(true)
                                    btnReject.visible(false)
                                    tvHeader.visible(false)
                                }
                            }

                            com.ciaorides.ciaorides.utils.Constants.REACHED -> {
                                    Log.d("FCMDriver", "FCM REACHED")
                                    ride_status = Constants.REACHED
                                    updateRideDetails(fcmResponse)
                                    tvCongratsMsg.text = "Enjoy your ride!"
                                    binding.appBarHome.layoutHome.layoutOtp.firstPinView.setText("")
                                    binding.appBarHome.layoutHome.layoutOtp.layoutOtpScreen.visible(
                                        true
                                    )
                                    binding.appBarHome.layoutHome.layoutOtp.tvSource.text =
                                        fcmResponse.sourceAddress
                                    binding.appBarHome.layoutHome.layoutOtp.tvDestination.text =
                                        fcmResponse.destinationAddress
                                    tvHeader.visible(false)
                            }

                            com.ciaorides.ciaorides.utils.Constants.OTP_VALIDATED -> {
                                    Log.d("FCMDriver", "FCM OTP VALIDATED")
                                    ride_status = Constants.OTP_VALIDATED
                                    isOtpValidated = true
                                    updateRideDetails(fcmResponse)
                                    tvCongratsMsg.text = "Enjoy your ride!"
                                    btnAccept.visible(false)
                                    tvHeader.visible(false)
                                    btnReached.visible(false)
                                    btnPickup.visible(false)
                                    btnReject.visible(false)
                                    binding.appBarHome.layoutHome.layoutOtp.layoutOtpScreen.visible(
                                        false
                                    )
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
                                    Log.d("FCMDriver", "FCM RIDE COMPLETED")
                                    ride_status = Constants.RIDE_COMPLETED
                                    updateRideDetails(fcmResponse)
                                    tvCongratsMsg.text = "Payment Complete!!"
                                    btnAccept.visible(false)
                                    tvHeader.visible(false)
                                    btnReached.visible(false)
                                    btnPickup.visible(false)
                                    btnReject.visible(false)
                                    binding.appBarHome.layoutHome.layoutOtp.layoutOtpScreen.visible(
                                        false
                                    )
                                    bottomSheetLayout.visible(true)
                                    btnComplete.visible(false)
                                    btnPayment.visible(true)

                                    tvSource.text =
                                        fcmResponse.sourceAddress
                                    tvDestination.text =
                                        fcmResponse.destinationAddress
                                    tvPayment.text =
                                        fcmResponse.time
                            }

                            com.ciaorides.ciaorides.utils.Constants.PAYMENT_COMPLETED -> {
                                    ride_status = Constants.PAYMENT_COMPLETED
                                    Log.d("FCMDriver", "FCM PAYMENT COMPLETED")
                                    updateSearchState(com.ciaorides.ciaorides.utils.Constants.ONLINE)
                                    homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                                    binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                        View.GONE

                                /* FcmBookUtils.updateApprovedStatus(
                                    bookingId,
                                    rider_id,
                                    Constants.REMOVE_RIDE
                                )
                                    // Restart activity
                                    if (Build.VERSION.SDK_INT >= 11) {
                                    recreate()
                                } else {
                                    val intent = intent
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    finish()
                                    overridePendingTransition(0, 0)

                                    startActivity(intent)
                                    overridePendingTransition(0, 0)
                                }*/
                            }

                            com.ciaorides.ciaorides.utils.Constants.REJECTED -> {
                                ride_status = Constants.REJECTED
                                Log.d("FCMDriver", "Ride rejected")
                                updateSearchState(com.ciaorides.ciaorides.utils.Constants.ONLINE)
                                homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                                binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                    View.GONE
                            }

                            com.ciaorides.ciaorides.utils.Constants.RIDE_CANCELLED -> {
                                ride_status = Constants.RIDE_CANCELLED
                                Log.d("FCMDriver", "Ride cancelled")
                                updateSearchState(com.ciaorides.ciaorides.utils.Constants.ONLINE)
                                homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                                binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                    View.GONE
                            }
                        }
                    }
                }
            } else {
                homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                    View.GONE
            }
        } catch (e: Exception){
            e.printStackTrace()
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
                                    data.response.total_bookings
                                appBarHome.layoutHome.tvTotalEarnings.text =
                                    getPrice(data.response.total_earnings!!)
                                if (data.response.previous_booking_data!=null && data.response.previous_booking_data!!.isNotEmpty()
                                ) {
                                    appBarHome.layoutHome.llPrevRidesLayout.visibility =
                                        View.VISIBLE

                                    data.response.previous_booking_data?.let{
                                        it[0].total_amount?.let { total_amount ->
                                            appBarHome.layoutHome.tvPreviousRides.text =
                                                getPrice(total_amount)
                                        }
                                    }


                                    try {
                                        val date = Constants.getFormattedDob(
                                            "yyyy-MM-dd",
                                            "dd MMM",
                                            data.response.previous_booking_data!![0].ride_time!!.split(
                                                " "
                                            )[0])
                                        appBarHome.layoutHome.tvTime.text =
                                            "${data.response.previous_booking_data!![0].trip_distance} KM - ${date}"
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else {
                                    appBarHome.layoutHome.llPrevRidesLayout.visibility = View.VISIBLE
                                }

                            }

                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(this@HomeActivity, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

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

    private fun handleUserResponse() {
        viewModel.userDetailsResponse.observe(this) { dataHandler ->
            binding.appBarHome.layoutHome.progressLayout.root.visibility = View.GONE
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        profileData = data.response
                        if (data.status) {
                            binding.userDetails.tvName.setText(data.response.first_name)
                            binding.userDetails.tvNumber.setText(data.response.mobile)

                            if (data.response.profile_pic.isNotEmpty()) {
                                Constants.saveValue(
                                    this,
                                    Constants.USER_IMAGE,
                                    data.response.profile_pic
                                )
                            }

                            var alertValue = ""
                            /*if (data.response.driver_license_verified != Constants.YES) {
                                alertValue = getString(R.string.driving_licence) + ", "
                            }
                            if (data.response.pan_card_verified != Constants.YES) {
                                alertValue = alertValue + " " + getString(R.string.pan_card) + ", "
                            }
                            if (data.response.aadhar_card_verified != Constants.YES) {
                                alertValue =
                                    alertValue + " " + getString(R.string.adhar_card) + ", "
                            }*/
                            if (!TextUtils.isEmpty(alertValue)) {
                                globalAlert(
                                    this@HomeActivity,
                                    alertValue + "is(are) not updated/verified",
                                    "Upload",
                                    isCancel = false

                                ) {
                                    if (it) {
                                        val intent = Intent(this, EditProfileActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            } else {
                                binding.appBarHome.layoutHome.btnStart.visible(true)
//                                binding.appBarHome.layoutHome.bottomStartRide.visible(true)
                                viewModel.getHomePageRidesData(GlobalUserIdRequest(driver_id = driverId))
                                viewModel.checkInStatus(
                                    GlobalUserIdRequest(
                                        driver_id = driverId
                                    )
                                )

                                Constants.saveValue(
                                    this@HomeActivity,
                                    Constants.BADGE,
                                    profileData?.badge_type!!
                                )
                                updateToolBar(
                                    binding.appBarHome.ivBadge,
                                    binding.appBarHome.ivProfileImage
                                )
                            }
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is DataHandler.LOADING -> {

                }
            }
        }
    }

    private fun handleCompleteTaxiRideCall() {
        viewModel.completeTaxiRideResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            // On success form server, payment has to complete
                            updateSearchState(com.ciaorides.ciaorides.utils.Constants.ONLINE)
                            homeBinding.bottomSheetLayout.visibility = View.VISIBLE
                            binding.appBarHome.layoutHome.localRideSheet.bottomSheetLayout.visibility =
                                View.GONE
                            bookingId = ""
                            ride_status = ""
                            /*FcmBookUtils.removeBooking(
                                driverId = driverId,
                                fcmData = fcmViewModel!!
                            )*/
                            fcmViewModel = null
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

    override fun onResume() {
        super.onResume()
//        updateSearchState("")
        updateToolBar(
            binding.appBarHome.ivBadge,
            binding.appBarHome.ivProfileImage
        )
    }

    // PIP Mode Code
    override fun onPause() {
        super.onPause()

        enterPictureInPicture()
    }

    private fun enterPictureInPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(9, 10) // Example aspect ratio
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
//                .setActions(getPipActions()) // Optional: provide actions
                .build()
            enterPictureInPictureMode(pipParams)
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            // Hide header and popup
            Log.d("On Pause", "Enter PIP Mode")
            binding.appBarHome.homeAppIcon.visibility = View.VISIBLE
            binding.appBarHome.layoutHome.rootLayout.visibility = View.GONE
            binding.appBarHome.honeToolbar.visibility =View.GONE
        } else {
            // Show header and popup
            Log.d("On Pause", "Left PIP Mode")
            binding.appBarHome.homeAppIcon.visibility = View.GONE
            binding.appBarHome.layoutHome.rootLayout.visibility = View.VISIBLE
            binding.appBarHome.honeToolbar.visibility =View.VISIBLE
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPictureInPicture()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Handle configuration changes if necessary
    }


    private fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotification1()
        } else {
            sendNotification()
        }
    }

    private fun sendNotification() {
        //foreground app
        val resultIntent = Intent(applicationContext, HomeActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val alarmSound = Uri.parse(
            (ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + packageName + "/raw/ringtone.mp3")
        )
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,  /* Request code */resultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(
            applicationContext, CHANNEL_ID
        )
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.app_icon)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setNumber(10)
            .setAutoCancel(false)
            .setTicker("CIAORides")
            .setContentTitle("Ride!")
            .setContentText("You have new ride request!")
            .setContentInfo("New Notification")
        notificationBuilder.setSound(alarmSound)
        notificationManager.notify(1, notificationBuilder.build())
    }

    @SuppressLint("NewApi")
    private fun sendNotification1() {
        //foreground app
        val resultIntent = Intent(applicationContext, HomeActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,  /* Request code */resultIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmSound = Uri.parse(
            (ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + packageName + "/raw/ringtone.mp3")
        )

        //Uri defaultsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        val oreoNotification = OreoNotification(this)
        val builder = oreoNotification.getOreoNotification(
            "Ride!",
            "You have new ride request!",
            pendingIntent,
            alarmSound,
            java.lang.String.valueOf(R.drawable.ic_launcher_background)
        )

        val i = 0
        oreoNotification.manager.notify(i, builder.build())
    }
}