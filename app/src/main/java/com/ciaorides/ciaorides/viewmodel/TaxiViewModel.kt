package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.BookRideRequest
import com.ciaorides.ciaorides.model.request.RecentSearchRequest
import com.ciaorides.ciaorides.model.request.VehicleInfoRequest
import com.ciaorides.ciaorides.model.response.AddressInfoResponse
import com.ciaorides.ciaorides.model.response.BookRideResponse
import com.ciaorides.ciaorides.model.response.RecentSearchesResponse
import com.ciaorides.ciaorides.model.response.VehicleInfoResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TaxiViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {

    private val _locationInfo = MutableLiveData<AddressInfoResponse>()
    val locationInfo: LiveData<AddressInfoResponse> =
        _locationInfo

    private val _recentSearchesResponse = MutableLiveData<DataHandler<RecentSearchesResponse>>()
    val recentSearchesResponse: LiveData<DataHandler<RecentSearchesResponse>> =
        _recentSearchesResponse



    fun getPlaceDetails(key: String, latLong: String) {
        viewModelScope.launch {
            val response = networkRepository.getAddress(key, latLong)
            if (response.isSuccessful) {
                _locationInfo.postValue(response.body())
            }
        }
    }


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

    private val _vehicleInfoResponse = MutableLiveData<DataHandler<VehicleInfoResponse>>()
    val vehicleInfoResponse: LiveData<DataHandler<VehicleInfoResponse>> =
        _vehicleInfoResponse

    fun getVehicleInfo(request: VehicleInfoRequest) {
        viewModelScope.launch {
            val response = networkRepository.getVehicleInfo(request)
            _vehicleInfoResponse.postValue(handleVehicleInfoResp(response))
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

    private val _bookRideResponse = MutableLiveData<DataHandler<BookRideResponse>>()
    val bookRideResponse: LiveData<DataHandler<BookRideResponse>> =
        _bookRideResponse

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
}