package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.response.*
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _recentSearchesResponse = MutableLiveData<DataHandler<RecentSearchesResponse>>()
    val recentSearchesResponse: LiveData<DataHandler<RecentSearchesResponse>> =
        _recentSearchesResponse

    private val _recentFevResponse = MutableLiveData<DataHandler<RecentFevResponse>>()
    val recentFevResponse: LiveData<DataHandler<RecentFevResponse>> =
        _recentFevResponse

    private val _vehicleInfoResponse = MutableLiveData<DataHandler<VehicleInfoResponse>>()
    val vehicleInfoResponse: LiveData<DataHandler<VehicleInfoResponse>> =
        _vehicleInfoResponse

    private val _bookRideResponse = MutableLiveData<DataHandler<BookRideResponse>>()
    val bookRideResponse: LiveData<DataHandler<BookRideResponse>> =
        _bookRideResponse

    private val _cancelRideResponse = MutableLiveData<DataHandler<CancelRideResponse>>()
    val cancelRideResponse: LiveData<DataHandler<CancelRideResponse>> =
        _cancelRideResponse

    private val _locationInfo = MutableLiveData<AddressInfoResponse>()
    val locationInfo: LiveData<AddressInfoResponse> =
        _locationInfo


    private val _availabilityResponse = MutableLiveData<DataHandler<SharingAvailabilityResponse>>()
    val availabilityResponse: LiveData<DataHandler<SharingAvailabilityResponse>> =
        _availabilityResponse




    fun makeRecentRequest(request: RecentSearchRequest) {
        viewModelScope.launch {
            val response = networkRepository.recentSearch(request)
            _recentSearchesResponse.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<RecentSearchesResponse>): DataHandler<RecentSearchesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    private fun handleRecentFevResponse(response: Response<RecentFevResponse>): DataHandler<RecentFevResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    fun addToRecent(request: RecentFevRequest) {
        viewModelScope.launch {
            val response = networkRepository.addRecentFev(request)
            if ("favorite" == request.type) {
                _recentFevResponse.postValue(handleRecentFevResponse(response))
            }
        }
    }

    fun getVehicleInfo(request: VehicleInfoRequest) {
        viewModelScope.launch {
            val response = networkRepository.getVehicleInfo(request)
            _vehicleInfoResponse.postValue(handleVehicleInfoResp(response))
        }
    }

    fun getSharingVehicles(request: RidesSharingRequest) {
        viewModelScope.launch {
            val response = networkRepository.getSharingVehiclesRequest(request)
            _vehicleInfoResponse.postValue(handleVehicleInfoResp(response))
        }
    }

    fun checkAvailability(request: CheckAvailabilityRequest) {
        viewModelScope.launch {
            val response = networkRepository.checkAvailability(request)
            _availabilityResponse.postValue(handleSharingAvailabilityResponse(response))
        }
    }

    private fun handleSharingAvailabilityResponse(response: Response<SharingAvailabilityResponse>): DataHandler<SharingAvailabilityResponse> {
        if (response.isSuccessful && response.body() != null && response.body()?.response != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    private fun handleVehicleInfoResp(response: Response<VehicleInfoResponse>): DataHandler<VehicleInfoResponse> {
        if (response.isSuccessful && response.body() != null && response.body()?.response != null) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return if (response.body()?.message != null) {
            DataHandler.ERROR(message = response.body()?.message!!)
        } else {
            DataHandler.ERROR(message = Constants.SOME_THING_WENT_WRONG)
        }
    }

    fun bookRideCall(request: BookRideRequest) {
        viewModelScope.launch {
            val response = networkRepository.bookRide(request)
            _bookRideResponse.postValue(handleBookRideResponse(response))
        }
    }

    private fun handleBookRideResponse(response: Response<BookRideResponse>?): DataHandler<BookRideResponse> {
        if (response!=null && response.isSuccessful && response.body() != null && response.body()?.response != null) {
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

    fun getPlaceDetails(key: String, latLong: String) {
        viewModelScope.launch {
            val response = networkRepository.getAddress(key, latLong)
            if (response.isSuccessful) {
                _locationInfo.postValue(response.body())
            }
        }
    }

    fun cancelRide(request: CancelRideRequest) {
        viewModelScope.launch {
            val response = networkRepository.cancelRide(request)
            _cancelRideResponse.postValue(handleCancelRideResponse(response))
        }
    }

    private fun handleCancelRideResponse(response: Response<CancelRideResponse>?): DataHandler<CancelRideResponse> {
        if (response!=null && response.isSuccessful && response.body() != null && response.body()?.response != null) {
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