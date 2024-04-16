package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.request.HomePageRidesResponse
import com.ciaorides.ciaorides.model.response.*
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {


    private val _myVehicleResponse = MutableLiveData<DataHandler<MyVehicleResponse>>()
    val myVehicleResponse: LiveData<DataHandler<MyVehicleResponse>> =
        _myVehicleResponse

    private val _checkInResponse = MutableLiveData<DataHandler<GlobalResponse>>()
    val checkInResponse: LiveData<DataHandler<GlobalResponse>> =
        _checkInResponse

    private val _checkInStatusResponse = MutableLiveData<DataHandler<CheckInStatusResponse>>()
    val checkInStatusResponse: LiveData<DataHandler<CheckInStatusResponse>> =
        _checkInStatusResponse

    private val _rejectRideResponse = MutableLiveData<DataHandler<GlobalResponse>>()
    val rejectRideResponse: LiveData<DataHandler<GlobalResponse>> =
        _rejectRideResponse

    private val _acceptRideResponse = MutableLiveData<DataHandler<GlobalResponse>>()
    val acceptRideResponse: LiveData<DataHandler<GlobalResponse>> =
        _acceptRideResponse

    private val _bookingInfoResponse = MutableLiveData<DataHandler<BookingInfoResponse>>()
    val bookingInfoResponse: LiveData<DataHandler<BookingInfoResponse>> =
        _bookingInfoResponse

    private val _homePageRidesResponse = MutableLiveData<DataHandler<HomePageRidesResponse>>()
    val homePageRidesResponse: LiveData<DataHandler<HomePageRidesResponse>> =
        _homePageRidesResponse

    private val _userDetailsResponse = MutableLiveData<DataHandler<UserDetailsResponse>>()
    val userDetailsResponse: LiveData<DataHandler<UserDetailsResponse>> = _userDetailsResponse



    fun getMyVehicles(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getMyVehicles(request)
                _myVehicleResponse.postValue(handleMyVehicleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleMyVehicleResponse(response: Response<MyVehicleResponse>?): DataHandler<MyVehicleResponse> {
        if (response != null && response.isSuccessful && response.body() != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    fun getUserDetails(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getUserDetails(request)
                _userDetailsResponse.postValue(handleUserDetails(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleUserDetails(response: Response<UserDetailsResponse>): DataHandler<UserDetailsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.errorBody().toString())
    }

    fun checkIn(request: DriverCheckInRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.driverCheck(request)
                _checkInResponse.postValue(handleCheckInResponse(response, request.check_in_status))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleCheckInResponse(
        response: Response<GlobalResponse>?,
        checkInStatus: String
    ): DataHandler<GlobalResponse> {
        if (response != null && response.isSuccessful && response.body() != null) {
            response.body()?.let { data ->
                data.otherValue = checkInStatus;
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }


    fun checkInStatus(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.checkInStatus(request)
                _checkInStatusResponse.postValue(handleCheckInStatusResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleCheckInStatusResponse(response: Response<CheckInStatusResponse>?): DataHandler<CheckInStatusResponse> {
        if (response != null && response.isSuccessful && response.body() != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    fun rejectRide(request: RejectRideRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.rejectRide(request)
                _rejectRideResponse.postValue(handleCheckInResponse(response, ""))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRideDetails(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getRideDetails(request)
                _bookingInfoResponse.postValue(handleMyBookingInfoResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleMyBookingInfoResponse(response: Response<BookingInfoResponse>?): DataHandler<BookingInfoResponse> {
        if (response != null && response.isSuccessful && response.body() != null && response.body()?.response != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    fun acceptRideRequest(request: AcceptRideRequest, bookingId: String) {
        viewModelScope.launch {
            try {
                val response = networkRepository.acceptRideRequest(request)
                _acceptRideResponse.postValue(handleAcceptRideRequest(response, bookingId))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleAcceptRideRequest(
        response: Response<GlobalResponse>?,
        bookingId: String
    ): DataHandler<GlobalResponse> {
        if (response != null && response.isSuccessful && response.body() != null) {
            response.body()?.let { data ->
                data.otherValue = bookingId
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    fun getHomePageRidesData(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getHomePageRidesData(request)
                _homePageRidesResponse.postValue(handleHomePageRidesResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleHomePageRidesResponse(response: Response<HomePageRidesResponse>?): DataHandler<HomePageRidesResponse> {
        if (response != null && response.isSuccessful && response.body() != null && response.body()?.response != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }


    fun completeTaxiTrip(request: CompleteOfferRideRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.completeTaxiRide(request)
                _completeTaxiRideResponse.postValue(handleCompleteTaxiRideResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _completeTaxiRideResponse =
        MutableLiveData<DataHandler<GlobalResponse>>()
    val completeTaxiRideResponse: LiveData<DataHandler<GlobalResponse>> =
        _completeTaxiRideResponse

    private fun handleCompleteTaxiRideResponse(response: Response<GlobalResponse>?): DataHandler<GlobalResponse> {
        if (response != null && response.isSuccessful && response.body() != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    fun pickUpTaxiTrip(request: PickUpRideRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.pickUpTaxiRide(request)
                _pickUpRideResponse.postValue(handlePickUpTaxiRideResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _pickUpRideResponse =
        MutableLiveData<DataHandler<GlobalResponse>>()
    val pickUpRideResponse: LiveData<DataHandler<GlobalResponse>> =
        _pickUpRideResponse

    private fun handlePickUpTaxiRideResponse(response: Response<GlobalResponse>?): DataHandler<GlobalResponse> {
        if (response != null && response.isSuccessful && response.body() != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response?.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }
}
