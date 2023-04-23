package com.ciaorides.ciaorides.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.TextUtils
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
import com.ciaorides.ciaorides.BuildConfig
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.*
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object Constants {
    const val YES: String = "Yes"
    const val FCM_TOKEN = "fcm_token"
    const val REQUEST_TYPE = "request_type"
    const val RIDE_TYPE = "ride_type"
    const val SOURCE = "source"
    const val IS_RIDE_OFFER = "is_ride_offer"
    const val DESTINATION = "destination"

    const val ONLINE = "online"
    const val OFFLINE = "offline"
    const val BUSY = "busy"
    const val PENDING = "pending"
    const val APPROVED = "approved"
    const val PICKED = "picked"
    const val REACHED = "reached"
    const val OTP_VALIDATED = "otp_validated"
    const val RIDE_COMPLETED = "ride_completed"


    const val STAGE_STATUS = "STAGE_STATUS"
    const val VEHICLE_ID = "VEHICLE_ID"

    //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
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
    const val IMG_TYPE = "IMG_TYPE"
    const val USER_ID = "user_id"
    const val USER_NAME = "user_name"
    const val MOBILE_NUMBER = "mobile_number"
    const val EMAIL_ID = "email_id"
    const val FONT_INTER_REG = "inter_regular.ttf"

    const val TERMS_AND_CONDITIONS =
        "https://www.ciaorides.com/new/Menuitem/termsandcoditions"
    const val PRIVACY_POLICY = "https://www.ciaorides.com/new/Menuitem/privacy_policy"
    const val ABOUT = "https://www.ciaorides.com/new/Menuitem/about_us"
    const val HELP = "https://www.ciaorides.com/terms_conditions"


    const val MENU_MY_RIDES = "My Rides"
    const val MENU_MY_EARNINGS = "My Earnings"
    const val MENU_MY_VEHICLES = "My Vehicles"
    const val MENU_MY_FAVOURITES = "My Favourites"
    const val MENU_BANK_DETAILS = "Bank Details"
    const val MENU_INBOX = "Inbox"
    const val MENU_REFER_FRIEND = "Refer a friend"
    const val MENU_PAYMENTS = "Payments"
    const val MENU_SETTINGS = "Settings"
    const val MENU_ABOUT_US = "About Us"
    const val MENU_TERMS_N_CONDITIONS = "Terms & Conditions"
    const val MENU_PRIVACY_POLICY = "Privacy Policy"
    const val MENU_HELP = "Help"


    const val KEY_RIDES_TAKEN = "rides_taken"
    const val KEY_BANK_DETAILS = "bank_details"
    const val KEY_EMERGENCY_CONTACT = "emergency_contact"


    fun saveValue(context: Context, key: String, value: String) {
        val sharedPreferences = (context as Activity).getSharedPreferences(MAIN_PREF, MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).commit()
        getValue(context, key)
    }

    fun getValue(context: Context, key: String): String {
        return (context as Activity).getSharedPreferences(MAIN_PREF, MODE_PRIVATE)
            .getString(key, "").toString()
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
    fun showGlide(context: Context, url: String, imageView: ImageView, progress: View? = null) {
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

    fun showScheduleAlert(
        context: Activity,
        okCallBack: ((Boolean) -> Unit?)? = null
    ) {

        var selectedDate = ""
        val builder = AlertDialog.Builder(context)
            .create()
        val scheduleBinding =
            AlertScheduleBinding.inflate(LayoutInflater.from(context), null, false)
        scheduleBinding.calendarView.minDate = System.currentTimeMillis()
        builder.setView(scheduleBinding.root)
        scheduleBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
        }
        scheduleBinding.btnVerify.setOnClickListener {
            okCallBack?.invoke(true)
            builder.dismiss()
        }

        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }
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

enum class FevType {
    HOME,
    OFFICE,
    OTHER
}

fun showRejectReasonsAlert(
    context: Activity,
    okCallBack: ((Boolean) -> Unit?)? = null
) {

    val builder = AlertDialog.Builder(context)
        .create()
    val binding = LayoutRejectResonsBinding.inflate(LayoutInflater.from(context), null, false)
    builder.setView(binding.root)
    binding.btnReject.setOnClickListener {
        builder.dismiss()
        okCallBack?.invoke(true)
    }
    builder.show()
}

fun getPrice(price: String) = "₹ $price"
fun View.visible(state: Boolean) {
    visibility = if (state) View.VISIBLE else View.GONE
}

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

fun globalAlert(
    context: Activity,
    message: String,
    yesText: String,
    noText: String?="",
    isCancel: Boolean = true,
    listener: ((Boolean) -> Unit?)? = null

) {

    val builder = AlertDialog.Builder(context)
        .create()

    val binding =
        GlobalAlertBinding.inflate(LayoutInflater.from(context), null, false)
    binding.btnYes.text = yesText
    binding.tvMessage.text = message
    binding.btnCancel.text = noText
    builder.setCanceledOnTouchOutside(isCancel)
    if (TextUtils.isEmpty(noText)) {
        binding.btnCancel.visibility = View.INVISIBLE
    }
    binding.btnYes.setOnClickListener {
        builder.dismiss()
        listener?.invoke(true)
    }
    binding.btnCancel.setOnClickListener {
        builder.dismiss()
    }
    builder.setView(binding.root)
    builder.show()
}
