package com.ciaorides.ciaorides.view.activities.menu

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityRideDetailsBinding
import com.ciaorides.ciaorides.fcm.FcmBookUtils
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.BookingInfoResponse
import com.ciaorides.ciaorides.model.response.MyRidesResponse
import com.ciaorides.ciaorides.model.response.PaymentsResponse
import com.ciaorides.ciaorides.utils.BookType
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.Constants.KEY_BOOKING_DATA
import com.ciaorides.ciaorides.utils.Constants.KEY_RIDES_TAKEN
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RideDetailsActivity : BaseActivity<ActivityRideDetailsBinding>() {

    override fun getViewBinding(): ActivityRideDetailsBinding =
        ActivityRideDetailsBinding.inflate(layoutInflater)

    lateinit var ridesModel : MyRidesResponse.Response.RidesTaken
    private val viewModel: HomeViewModel by viewModels()

    lateinit var bookingInfoResponse : BookingInfoResponse.Response

    override fun init() {
        handleBookingInfoResponse()
        updateToolBar(binding.toolbar.ivBadge,binding.toolbar.ivProfileImage)
        binding.toolbar.tvHeader.text = getString(R.string.ride_details)
        binding.toolbar.profileView.visibility = View.GONE
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }

        if (intent?.getParcelableExtra<MyRidesResponse.Response.RidesTaken>(KEY_RIDES_TAKEN) != null) {
            ridesModel =
                intent?.getParcelableExtra<MyRidesResponse.Response.RidesTaken>(KEY_RIDES_TAKEN)!!
            setUpUI()
            if (ridesModel.booking_id != null)
                callBookingInfo(ridesModel.booking_id!!)
        } else {
            val bookingData = intent?.getParcelableExtra<PaymentsResponse.BookingData>(KEY_BOOKING_DATA)!!
            callBookingInfo(bookingData.booking_id)
        }

    }

    private fun callBookingInfo(bookingId: String) {
        viewModel.getRideDetails(
            GlobalUserIdRequest(
                booking_id = bookingId
            )
        )
    }

    private fun handleBookingInfoResponse() {
        viewModel.bookingInfoResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            bookingInfoResponse = data.response
                            setUpBookingUI()
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

    private fun setUpBookingUI() {
        bookingInfoResponse.let {
                binding.textViewRating.text = it.user_details.average_rating
                binding.textViewFromAddress.text = it.from_address
                binding.textViewToAddress.text = it.to_address
                binding.tvName.setText("${it.user_details.first_name} ${it.user_details.last_name}")
                binding.tvPrice.text = "Rs ${it.total_amount}"
                binding.tvVehicleNumber.text = it.from_address

                if (!android.text.TextUtils.isEmpty(it.user_details.profile_pic)) {
                    com.ciaorides.ciaorides.utils.Constants.showGlide(
                        this,
                        com.ciaorides.ciaorides.BuildConfig.IMAGE_BASE_URL + it.user_details.profile_pic,
                        binding.profileImage
                    )
                }
                binding.tvVehicleNumber.text =
                    "${it.user_details.address1}, ${it.user_details.address2}"

            // Amount
            var rideAmount = if (it.ride_charges == null) "0" else it.ride_charges
            binding.rideCharges.setText("Rs ${rideAmount}")
            var conCharges = ""
            if (it.convenience_charges == null) {
                conCharges = it.ciao_commission
            } else {
                conCharges = (it.convenience_charges.toDouble() + it.ciao_commission.toDouble()).toString()

            }
            binding.convenienceCharges.setText("Rs ${conCharges}")
            binding.taxAmount.setText("Rs ${it.tax}")
            binding.totalAmount.setText("Rs ${it.total_amount}")

            binding.btnRideStatusUpdate.setOnClickListener {
                Log.d("FCM Started", bookingInfoResponse.booking_id+" "+bookingInfoResponse.driver_details!!.id+" "
                +"Started!")
                FcmBookUtils.updateApprovedStatus(
                    bookingInfoResponse.booking_id,
                    bookingInfoResponse.driver_details!!.id,
                    Constants.STARTED
                )
            }
        }
    }

    private fun setUpUI() {
        ridesModel.let {
                binding.textViewRating.text = it.rating
                binding.textViewToAddress.text = it.to_address
                binding.textViewFromAddress.text = it.from_address
                binding.tvPrice.text = "₹ ${it.total_amount}"
                binding.tvVehicleNumber.text = it.from_address
                binding.tvName.text = it.first_name + " " + it.last_name

            if(it.ride_type != null && it.ride_type == "Taking"){
                if (it.status!!.toLowerCase() == "accepted") {
                    binding.btnRideStatusUpdate.visibility = View.VISIBLE
                    binding.btnRideStatusUpdate.setText("Started")
                    // once started, will not be able to change. Rest of the flow is from Driver home activity.
                } else {
                    binding.btnRideStatusUpdate.visibility = View.GONE
                }
            } else {
                binding.btnRideStatusUpdate.visibility = View.GONE
            }
        }
    }
}