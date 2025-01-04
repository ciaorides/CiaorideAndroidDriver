package com.ciaorides.ciaorides.services

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.HomeActivity
import com.ciaorides.ciaorides.view.activities.HomeActivity.Companion.driverId
import com.ciaorides.ciaorides.view.activities.HomeActivity.Companion.fcmViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    lateinit var locationClient: LocationClient

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        start()
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    fun start(){
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking Location for Ciao...")
            .setContentText("")
            .setSmallIcon(R.drawable.app_icon)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        locationClient.getLocationUpdates(1000000)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                updateToFirebase(location)
                val updsteNotification = notification.setContentText(
                    "Location $lat & $long}"
                )

                notificationManager.notify(1, updsteNotification.build())
            }
            .launchIn(serviceScope)
        startForeground(1, notification.build())
    }

    private fun updateToFirebase(location: Location) {
        if(fcmViewModel!= null && fcmViewModel?.bookingNumber!=null && location!=null){
            if ((fcmViewModel!!.rideStatus == Constants.APPROVED || fcmViewModel!!.rideStatus == Constants.OTP_VALIDATED) && fcmViewModel!!.orderId != null) {
                Log.e(TAG, "Booking number: ${fcmViewModel!!.bookingNumber}")
                FcmBookUtils.updateDriverLocation(
                    fcmViewModel?.bookingNumber.toString(),
                    driverId,
                    location.latitude,
                    location.longitude
                )
            }
        }
    }

    companion object {
        private const val TAG = "MyLocationService"
        private const val LOCATION_INTERVAL = 10
        private const val LOCATION_DISTANCE = 1f
    }
}
