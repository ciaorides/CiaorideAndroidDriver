package com.ciaorides.ciaorides.view.activities.ui.home

import android.content.Context.APPWIDGET_SERVICE
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentHomeBinding
import com.ciaorides.ciaorides.model.LocationsData
import com.ciaorides.ciaorides.model.request.HomeBannersRequest
import com.ciaorides.ciaorides.model.response.HomeBannersResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.utils.PromotionTypes
import com.ciaorides.ciaorides.view.activities.SearchActivity
import com.ciaorides.ciaorides.view.activities.rides.TaxiFlowActivity
import com.ciaorides.ciaorides.view.adapter.HomePromotionalAdapter
import com.ciaorides.ciaorides.view.adapter.PromotionsAdapter
import com.ciaorides.ciaorides.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var homePromotionalAdapter: HomePromotionalAdapter

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var currentTopPage = 0
    private var currentMiddlePage = 0
    private val DELAY_MS: Long = 500
    private val PERIOD_MS: Long = 3000

    var rideSelection = RideSelection.TAXI
    var resultReceiver: ResultReceiver? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initData()
        viewModel.validateUser(HomeBannersRequest(Constants.HOME))
        handleBannersCall()

        return root
    }

    private fun handleBannersCall() {
        viewModel.homeBannersResponse.observe(requireActivity()) { dataHandler ->

            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.progressBar.root.visibility = View.GONE
                    dataHandler.data?.let { data ->
                        setupPager(data)
                    }
                }
                is DataHandler.ERROR -> {
                    binding.progressBar.root.visibility = View.GONE
                    Toast.makeText(requireActivity(), dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
                // is DataHandler.LOADING -> binding.progressBar.root.visibility = View.VISIBLE
            }

        }
    }

    private fun initData() {
        activity?.let { activity ->
            binding.takeRide.tvTakeRide.setOnClickListener {
                handleTakeRide()
            }

            binding.offerRide.tvOfferRide.setOnClickListener {
                //handleOfferRide()
            }

            binding.takeRide.cardTaxi.setOnClickListener {
                binding.takeRide.cardTaxi.strokeColor =
                    ContextCompat.getColor(activity, R.color.appBlue)
                binding.takeRide.cardInterCity.strokeColor =
                    ContextCompat.getColor(activity, R.color.white)
                binding.takeRide.cardSharing.strokeColor =
                    ContextCompat.getColor(activity, R.color.white)
                binding.offerRide.etCurrentLocation.visibility = View.VISIBLE
                handleOfferRide()
                rideSelection = RideSelection.TAXI
            }

            binding.takeRide.cardInterCity.setOnClickListener {
                binding.takeRide.cardTaxi.strokeColor =
                    ContextCompat.getColor(activity, R.color.white)
                binding.takeRide.cardInterCity.strokeColor =
                    ContextCompat.getColor(activity, R.color.appBlue)
                binding.takeRide.cardSharing.strokeColor =
                    ContextCompat.getColor(activity, R.color.white)
                binding.offerRide.etCurrentLocation.visibility = View.VISIBLE
                handleOfferRide()
                rideSelection = RideSelection.OUT_STATION
            }

            binding.takeRide.cardSharing.setOnClickListener {
                binding.takeRide.cardTaxi.strokeColor =
                    ContextCompat.getColor(activity, R.color.white)
                binding.takeRide.cardInterCity.strokeColor =
                    ContextCompat.getColor(activity, R.color.white)
                binding.takeRide.cardSharing.strokeColor =
                    ContextCompat.getColor(activity, R.color.appBlue)
                binding.offerRide.etCurrentLocation.visibility = View.GONE
                handleOfferRide()
                rideSelection = RideSelection.RIDE_SHARE
            }
            binding.takeRide.etCurrentLocation.setOnClickListener {
                moveRides()
            }

            binding.takeRide.etDestination.setOnClickListener {
                moveRides()
            }
            binding.offerRide.etCurrentLocation.setOnClickListener {
               moveOfferRides()
            }
            binding.offerRide.etDestination.setOnClickListener {
                moveOfferRides()
            }

            resultReceiver = AddressResultReceiver(Handler(Looper.getMainLooper()))
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


                   binding.offerRide.etCurrentLocation.setText(loc)
                lastKnownLocation?.let { location ->
                    binding.offerRide.etCurrentLocation.setText(loc)
                }

            } else {
                Toast.makeText(
                    activity,
                    resultData.getString(Constants.RESULT_DATA_KEY),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun moveOfferRides() {
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra(Constants.RIDE_TYPE, rideSelection.name)
        intent.putExtra(Constants.IS_RIDE_OFFER, true)
        startActivity(intent)
    }

    private fun moveRides() {
        if(rideSelection == RideSelection.TAXI){
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(activity, SearchActivity::class.java)
            intent.putExtra(Constants.RIDE_TYPE, rideSelection.name)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleOfferRide() {
        if (binding.offerRide.etCurrentLocation.visibility == View.VISIBLE) {
            binding.offerRide.etCurrentLocation.visibility = View.GONE
            binding.offerRide.etDestination.visibility = View.GONE
            binding.offerRide.dropDown.rotation = 180f
        } else {
            binding.offerRide.etCurrentLocation.visibility = View.GONE
            binding.offerRide.etCurrentLocation.visibility = View.VISIBLE
            binding.offerRide.etDestination.visibility = View.VISIBLE
            binding.offerRide.dropDown.rotation = 0f
        }
    }

    private fun handleTakeRide() {
        if (binding.takeRide.llRides.visibility == View.VISIBLE) {
            binding.takeRide.llRides.visibility = View.GONE
            binding.takeRide.dropDown.rotation = 180f
            binding.takeRide.etCurrentLocation.visibility = View.GONE
            binding.takeRide.etDestination.visibility = View.GONE
        } else {
            binding.takeRide.llRides.visibility = View.VISIBLE
            binding.takeRide.etCurrentLocation.visibility = View.VISIBLE
            binding.takeRide.etDestination.visibility = View.VISIBLE
            binding.takeRide.dropDown.rotation = 0f
        }
    }

    private fun setupPager(data: HomeBannersResponse) {
        if (data.response.top.isNotEmpty()) {
            binding.pagerTopPromotions.visibility = View.VISIBLE
            binding.dotsIndicator.visibility = View.VISIBLE
            binding.pagerTopPromotions.adapter = PromotionsAdapter(
                childFragmentManager,
                PromotionTypes.TOP,
                data.response
            )
            binding.dotsIndicator.attachTo(binding.pagerTopPromotions)
            autoScrollTopPager(data.response.top.size + 1)
        }
        if (data.response.middle.isNotEmpty()) {
            binding.cardMiddlePromotions.visibility = View.VISIBLE
            binding.dotsMiddleIndicator.visibility = View.VISIBLE
            binding.pagerMiddlePromotions.adapter = PromotionsAdapter(
                childFragmentManager,
                PromotionTypes.MIDDLE,
                data.response
            )
            binding.dotsMiddleIndicator.attachTo(binding.pagerMiddlePromotions)
            //autoScrollMiddlePager(data.response.bottom.size + 1)
        }
        data.response.bottom.add(HomeBannersResponse.Response.Bottom())
        if (data.response.bottom.isNotEmpty()) {
            homePromotionalAdapter.differ.submitList(data.response.bottom)
            binding.rvOtherPromotions.apply {
                adapter = homePromotionalAdapter
                layoutManager = LinearLayoutManager(activity)
            }
        }

    }

    private fun autoScrollTopPager(totalCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            if (currentTopPage === totalCount - 1) {
                currentTopPage = 0
            }
            binding.pagerTopPromotions.setCurrentItem(currentTopPage++, true)
        }
        val topTimer = Timer() // This will create a new Thread
        topTimer.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }
}

enum class RideSelection {
    TAXI,
    OUT_STATION,
    RIDE_SHARE
}