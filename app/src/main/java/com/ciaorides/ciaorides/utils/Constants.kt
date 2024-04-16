package com.ciaorides.ciaorides.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.*
import com.ciaorides.ciaorides.model.request.BookRideRequest
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object Constants {


    const val PENDING = "pending"
    const val FCM_TOKEN = "fcm_token"
    const val DEVICE_ID = "device_id"
    const val BOTH = "both"
    const val MALE = "male"
    const val FE_MALE = "female"
    const val TEMP_USER_ID = "2209"
    const val REQUEST_TYPE = "request_type"
    const val RIDE_TYPE = "ride_type"
    const val SOURCE = "source"
    const val IS_RIDE_OFFER = "is_ride_offer"
    const val DESTINATION = "destination"

    const val APPROVED = "approved"
    const val PICKED = "picked"
    const val REACHED = "reached"
    const val OTP_VALIDATED = "otp_validated"
    const val RIDE_COMPLETED = "ride_completed"
    const val PAYMENT_COMPLETED = "payment_completed"
    const val RIDE_CANCELLED = "ride_cancelled"
    const val MENU_REFER_FRIEND = "Refer a friend"

    //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

    /*adb kill-server
    sudo cp ~/Android/Sdk/platform-tools/adb /usr/bin/adb
    sudo chmod +x /usr/bin/adb
    adb start-server*/
    const val SOME_THING_WENT_WRONG = "Something went wrong.."
    const val MAIN_PREF = "main_pref"
    const val HOME = "home"
    const val HOME1 = "home"
    const val USER_DATA = "user_data"
    const val PHONE_NUMBER = "phone_number"
    const val TITLE = "title"
    const val INDEX = "index"
    const val BANNER_RESP = "banner_resp"

    const val USER_INFO = "user_info"
    const val PACKAGE_NAME = "com.example.currentaddress"
    const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
    const val RECEVIER = "$PACKAGE_NAME.RECEVIER"
    const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"

    const val ADDRESS = "$PACKAGE_NAME.ADDRESS"
    const val LOCAITY = "$PACKAGE_NAME.LOCAITY"
    const val COUNTRY = "$PACKAGE_NAME.COUNTRY"
    const val DISTRICT = "$PACKAGE_NAME.DISTRICT"
    const val POST_CODE = "$PACKAGE_NAME.POST_CODE"
    const val STATE = "$PACKAGE_NAME.STATE"

    const val SUCCESS_RESULT = 1
    const val FAILURE_RESULT = 0
    const val DATA_VALUE = "DATA_VALUE"
    const val STAGE_STATUS = "STAGE_STATUS"
    const val VEHICLE_ID = "VEHICLE_ID"
    const val IMG_TYPE = "IMG_TYPE"
    const val USER_ID = "user_id"
    const val USER_NAME = "user_name"
    const val MOBILE_NUMBER = "mobile_number"
    const val EMAIL_ID = "email_id"
    const val FONT_INTER_REG = "inter_regular.ttf"

    const val TERMS_AND_CONDITIONS = "https://ciaorides.com/new/Menuitem/termsandcoditions"
    const val PRIVACY_POLICY = "https://ciaorides.com/new/Menuitem/privacy_policy"
    const val ABOUT = "https://ciaorides.com/new/Menuitem/about_us"
    const val HELP = "https://www.ciaorides.com/contact"


    const val MENU_MY_RIDES = "My Rides"
    const val RIDE_REQUESTS = "Ride Requests"
    const val MENU_MY_WALLET = "My Wallet"
    const val MENU_MY_VEHICLES = "My Vehicles"
    const val MENU_MY_FAVOURITES = "My Favourites"
    const val MENU_BANK_DETAILS = "Bank Details"
    const val MENU_SETTINGS = "Settings"
    const val MENU_ABOUT_US = "About Us"
    const val MENU_TERMS_N_CONDITIONS = "Terms & Conditions"
    const val MENU_PRIVACY_POLICY = "Privacy Policy"
    const val MENU_HELP = "Help"

    const val LAST_KNOWN_LOCATION_LATITUDE = "LAST_KNOWN_LOCATION_LATITUDE"
    const val LAST_KNOWN_LOCATION_LONGITUDE = "LAST_KNOWN_LOCATION_LONGITUDE"

    fun saveValue(context: Context, key: String, value: String) {
        val sharedPreferences = (context as Activity).getSharedPreferences(MAIN_PREF, MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).commit()
        getValue(context, key)
    }

    fun getValue(context: Context, key: String): String {
        return context.getSharedPreferences(MAIN_PREF, MODE_PRIVATE)
            .getString(key, "").toString()
    }

    fun getPrice(price: String) = "₹ $price"


    fun showGlide(context: Context, url: String?, imageView: ImageView, progress: View? = null) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        Glide
            .with(context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress?.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress?.visibility = View.GONE
                    return false
                }

            })
            .into(imageView)
    }

    fun showAlert(
        context: Activity, message: String, title: String? = "", isCancelRequired: Boolean = false,
        listener: ((Boolean) -> Unit?)? = null
    ) {
        val builder = AlertDialog.Builder(context)
        with(builder)
        {
            if (title?.isNotEmpty() == true) {
                builder.setTitle(title)
            }
            builder.setMessage(message)
            builder.setPositiveButton(
                "Ok"
            ) { dialog, p1 ->
                dialog?.dismiss()
                listener?.invoke(true)
            }

            if (isCancelRequired) {
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, p1 ->
                    dialog?.dismiss()
                }
            }
            /* builder.setPositiveButton("Ok") {
                     dialog, p1 -> dialog?.dismiss()
             }*/
            show()
        }
    }


    fun showDeleteVehicleAlert(
        context: Activity,
        listener: ((Boolean) -> Unit?)? = null
    ) {

        val builder = AlertDialog.Builder(context)
            .create()

        val fevBinding =
            AlertDeleteVehicleBinding.inflate(LayoutInflater.from(context), null, false)
        fevBinding.btnDelete.setOnClickListener {
            listener?.let {
                builder.dismiss()
                it.invoke(true)
            }
        }
        fevBinding.btnCancel.setOnClickListener {
            builder.dismiss()
        }
        builder.setView(fevBinding.root)
        builder.show()
    }

    fun showFevAlert(
        context: Activity,
        message: String,
        okCallBack: ((FevType) -> Unit?)? = null,
        otherMessage: ((String) -> Unit?)? = null
    ) {

        val builder = AlertDialog.Builder(context)
            .create()

        val fevBinding = AlertFevBinding.inflate(LayoutInflater.from(context), null, false)

        builder.setView(fevBinding.root)
        fevBinding.tvLocation.text = message
        fevBinding.radioFevGroup.setOnCheckedChangeListener { group, id ->
            if (fevBinding.radioHome.id == id) {
                fevBinding.btnSubmit.visibility = View.GONE
                fevBinding.etOther.visibility = View.GONE
                builder.dismiss()
                okCallBack?.invoke(FevType.HOME)
            } else if (fevBinding.radioOffice.id == id) {
                fevBinding.btnSubmit.visibility = View.GONE
                fevBinding.etOther.visibility = View.GONE
                builder.dismiss()
                okCallBack?.invoke(FevType.OFFICE)

            } else {
                fevBinding.btnSubmit.visibility = View.VISIBLE
                fevBinding.etOther.visibility = View.VISIBLE
                // okCallBack?.invoke(FevType.OTHER)
            }
        }
        fevBinding.btnSubmit.setOnClickListener {
            if (TextUtils.isEmpty(fevBinding.tvLocation.text.toString())) {
                fevBinding.etOther.error = "Field should be not empty."
                fevBinding.etOther.requestFocus()
            } else {
                fevBinding.etOther.error = null
                otherMessage?.invoke(fevBinding.tvLocation.text.toString())
                builder.dismiss()
            }
        }
        builder.show()


    }


    fun showBookAlert(
        context: Activity,
        message: String,
        okCallBack: ((Boolean) -> Unit?)? = null
    ) {


        val builder = AlertDialog.Builder(context)
            .create()
        val messageBoxView = LayoutInflater.from(context).inflate(R.layout.alert_success, null)

        val body = messageBoxView.findViewById(R.id.message) as TextView
        body.text = message
        val doneBtn = messageBoxView.findViewById(R.id.btnDone) as MaterialButton
        builder.setView(messageBoxView)
        doneBtn.setOnClickListener {
            builder.dismiss()
            okCallBack?.let {
                it.invoke(true)
            }
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    fun showDialog(
        context: Activity,
        okCallBack: ((Int) -> Unit?)? = null
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.image_pick_layout)
        val gallery = dialog.findViewById(R.id.ll_gallery) as LinearLayout
        val camera = dialog.findViewById(R.id.ll_Camera) as LinearLayout
        (dialog.findViewById(R.id.ivClose) as ImageView).setOnClickListener {
            dialog.dismiss()
        }
        gallery.setOnClickListener {
            okCallBack?.invoke(2)
            dialog.dismiss()
        }
        camera.setOnClickListener {
            okCallBack?.invoke(1)
            dialog.dismiss()
        }
        dialog.show()

    }

    fun showRideSharingAlert(
        context: Context,
        okCallBack: ((String) -> Unit?)
    ) {

        val builder = AlertDialog.Builder(context)
            .create()
        val binding =
            SharingLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        binding.btnVerify.setOnClickListener {
            val cal = Calendar.getInstance()

            val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            if (binding.radioToday.isChecked) {
                var selectedDate: String = df.format(cal.time)
                //TODO validation of time with current time
                selectedDate = selectedDate + " " + getTime(binding.timePicker)
                builder.dismiss()
                Log.d("", "Selected Date Today " + selectedDate)
                okCallBack.invoke(selectedDate)
            } else {
                cal.add(Calendar.DATE, 1)
                var selectedDate: String = df.format(cal.time)
                selectedDate = selectedDate + " " + getTime(binding.timePicker)
                builder.dismiss()
                Log.d("", "Selected Date Tomorow " + selectedDate)
                okCallBack.invoke(selectedDate)
                //TODO validation of time with END time
            }
        }
        builder.setView(binding.root)
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    fun showScheduleAlert(
        context: Activity,
        okCallBack: ((String) -> Unit?)
    ) {
        val cal = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var selectedDate: String = df.format(cal)

        val builder = AlertDialog.Builder(context)
            .create()
        val scheduleBinding =
            AlertScheduleBinding.inflate(LayoutInflater.from(context), null, false)
        scheduleBinding.calendarView.minDate = System.currentTimeMillis()
        builder.setView(scheduleBinding.root)
        scheduleBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
            selectedDate = getDate(dayOfMonth, month, year)
            //2022-09-06 20:40:00
        }
        scheduleBinding.btnVerify.setOnClickListener {
            var time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                "" + scheduleBinding.timePicker.hour
            } else {
                "" + scheduleBinding.timePicker.currentHour
            }
            time += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ":" + scheduleBinding.timePicker.minute
            } else {
                ":" + scheduleBinding.timePicker.currentMinute
            }
            time += ":00"

            selectedDate = selectedDate + " " + time
            builder.dismiss()
            okCallBack.invoke(selectedDate)
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
}

private fun getTime(timePicker: TimePicker): String {
    var time = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        "" + timePicker.hour
    } else {
        "" + timePicker.currentHour
    }
    time += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ":" + timePicker.minute
    } else {
        ":" + timePicker.currentMinute
    }
    time += ":00"
    return time
}

fun getCurrentTime(): String {
    val cal = Calendar.getInstance().time
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return df.format(cal)
}

fun getDateTime(date: String): Pair<String, String> {
    val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date)
    val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(formattedDate)
    val time = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(formattedDate)
    return Pair(date, time.uppercase(Locale.getDefault()))
}

private fun getDate(dayOfMonth: Int, month: Int, year: Int): String {
    var selectedDate = "" + year
    selectedDate += if (month < 10) {
        "-0" + (month + 1)
    } else {
        "" + (month + 1)
    }

    selectedDate += if (dayOfMonth < 10) {
        "-0$dayOfMonth"
    } else {
        "-$dayOfMonth"
    }
    return selectedDate
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0
    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lat += dlat
        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or (b and 0x1f shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
        lng += dlng
        val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
        poly.add(latLng)
    }
    return poly
}

fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
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

fun getDirectionURL(origin: LatLng, dest: LatLng, secret: String): String {
    return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
            "&destination=${dest.latitude},${dest.longitude}" +
            "&sensor=false" +
            "&mode=driving" +
            "&key=$secret"
}

fun showEmergencyCallsAlert(
    context: Activity,
) {

    val builder = AlertDialog.Builder(context)
        .create()
    val binding = LayoutEmergencyContactsBinding.inflate(LayoutInflater.from(context), null, false)
    builder.setView(binding.root)
    binding.btnCancel.setOnClickListener {
        builder.dismiss()
    }
    binding.cardCall.setOnClickListener {
        makeCall(context, "9441500416")
    }
    binding.cardCall2.setOnClickListener {
        makeCall(context, "9441500416")
    }
    binding.cardCall3.setOnClickListener {
        makeCall(context, "9441500416")
    }

    builder.show()
}

private fun makeCall(context: Activity, phone: String) {
    val callIntent = Intent(Intent.ACTION_DIAL)
    callIntent.data = Uri.parse("tel:" + phone)
    context.startActivity(callIntent)
}


fun showCustomerSupport(
    context: Activity,
) {

    val builder = AlertDialog.Builder(context)
        .create()
    val scheduleBinding =
        SupportAlertBinding.inflate(LayoutInflater.from(context), null, false)
    builder.setView(scheduleBinding.root)
    scheduleBinding.btnCall.setOnClickListener {
        builder.dismiss()
        makeCall(context, "9441500416")
    }
    scheduleBinding.btnChat.setOnClickListener {
        builder.dismiss()
        Toast.makeText(context, "Navigates to support chat", Toast.LENGTH_SHORT).show()
    }
    builder.setCanceledOnTouchOutside(false)
    builder.show()
}

fun getMultipartData(realPath: String): ArrayList<MultipartBody.Part> {
    val file = File(realPath)
    var imagePartFile: MultipartBody.Part? = null
    val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
    imagePartFile = MultipartBody.Part.createFormData("image[]", file.name, requestBody)
    val descriptionList: ArrayList<MultipartBody.Part> = ArrayList()
    descriptionList.add(imagePartFile)
    return descriptionList
}

fun getTempRequest() =
    BookRideRequest(
        user_id = "2244",
        from_lat = "17.495218",
        from_lng = "78.398628",
        from_address = "Kukatpally Housing Board Colony  Kukatpally Hyderabad, Telangana",
        to_lat = "17.438360",
        to_lng = "78.454247",
        to_address = "Ameerpet, Hyderabad, Telangana",
        mode = "city",
        vehicle_type = "car",
        sub_vehicle_type = "mini",
        gender = "men",
        seats_required = "3",
        ride_type = "now",
        ride_time = "2022-09-06 20:40:00",
        user_type = "driver"
    )

@SuppressLint("SimpleDateFormat")
fun getCurrentTimeStamp(): String? {
    return try {
        val dateFormat =
            SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        dateFormat.format(Date())
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

enum class FevType {
    HOME,
    OFFICE,
    OTHER
}

fun paymentSuccessAlert(
    context: Activity,
) {

    val builder = AlertDialog.Builder(context)
        .create()
    val scheduleBinding =
        AlertPaymentSuccessfullBinding.inflate(LayoutInflater.from(context), null, false)
    builder.setView(scheduleBinding.root)
    scheduleBinding.btnRate.setOnClickListener {
        builder.dismiss()
    }
    scheduleBinding.btnGoToHome.setOnClickListener {
        builder.dismiss()
        context.finish()
    }
    builder.setCanceledOnTouchOutside(false)
    builder.show()
}

fun editAlert(
    context: Activity,
    listener: ((Boolean) -> Unit?)? = null
) {

    val builder = AlertDialog.Builder(context)
        .create()

    val binding =
        LayoutExitBinding.inflate(LayoutInflater.from(context), null, false)
    binding.btnYes.setOnClickListener {
        listener?.let {
            builder.dismiss()
            it.invoke(true)
        }
    }
    binding.btnCancel.setOnClickListener {
        builder.dismiss()
    }
    builder.setView(binding.root)
    builder.show()
}

fun globalAlert(
    context: Activity,
    message: String,
    yesText: String,
    noText: String,
    listener: ((Boolean) -> Unit?)? = null

) {

    val builder = AlertDialog.Builder(context)
        .create()

    val binding =
        GlobalAlertBinding.inflate(LayoutInflater.from(context), null, false)
    binding.btnYes.text = yesText
    binding.tvMessage.text = message
    if (TextUtils.isEmpty(noText)) {
        binding.btnCancel.visibility = View.INVISIBLE
    }
    binding.btnCancel.text = noText

    binding.btnYes.setOnClickListener {
        builder.dismiss()
        listener?.invoke(true)
    }
    binding.btnCancel.setOnClickListener {
        builder.dismiss()
        listener?.invoke(false)
    }
    builder.setView(binding.root)
    builder.show()
}

fun writeLogToFile(){
    if (isExternalStorageWritable()) {
        val appDirectory =
            File(Environment.getExternalStorageDirectory().toString() + "/MyPersonalAppFolder")
        val path = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            "CiaoRides"
        )
        val logDirectory = File("$path/logs")
        val logFile = File(logDirectory, "logcat_current" + ".txt")

        // create app folder
        if (!appDirectory.exists()) {
            appDirectory.mkdir()
        }

        // create log folder
        if (!logDirectory.exists()) {
            logDirectory.mkdir()
        }

        // clear the previous logcat and then write the new one to the file
        try {
            var process = Runtime.getRuntime().exec("logcat -c")
            process = Runtime.getRuntime().exec("logcat -f $logFile")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else if (isExternalStorageReadable()) {
        // only readable
    } else {
        // not accessible
    }
}

/* Checks if external storage is available for read and write */
fun isExternalStorageWritable(): Boolean {
    val state = Environment.getExternalStorageState()
    return if (Environment.MEDIA_MOUNTED == state) {
        true
    } else false
}

/* Checks if external storage is available to at least read */
fun isExternalStorageReadable(): Boolean {
    val state = Environment.getExternalStorageState()
    return if (Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state) {
        true
    } else false
}