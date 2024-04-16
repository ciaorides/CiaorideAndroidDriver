package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.fcm.BookingFcmData
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

    private val _bookRideResponse = MutableLiveData<DataHandler<BookingFcmData.BookResp>>()
    val bookRideResponse: LiveData<DataHandler<BookingFcmData.BookResp>> =
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

    private val _offerRideResponse = MutableLiveData<DataHandler<OfferARideResponse>>()
    val offerRideResponse: LiveData<DataHandler<OfferARideResponse>> =
        _offerRideResponse


    fun makeRecentRequest(request: RecentSearchRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.recentSearch(request)
                _recentSearchesResponse.postValue(handleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun offerRide(request: OfferARideRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.offerRide(request)
                _offerRideResponse.postValue(handleOfferRideResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    private fun handleOfferRideResponse(response: Response<OfferARideResponse>?): DataHandler<OfferARideResponse> {
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
            try {
                val response = networkRepository.addRecentFev(request)
                if ("favorite" == request.type) {
                    _recentFevResponse.postValue(handleRecentFevResponse(response))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getVehicleInfo(request: VehicleInfoRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getVehicleInfo(request)
                _vehicleInfoResponse.postValue(handleVehicleInfoResp(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getSharingVehicles(request: RidesSharingRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getSharingVehiclesRequest(request)
                _vehicleInfoResponse.postValue(handleVehicleInfoResp(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkAvailability(request: CheckAvailabilityRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.checkAvailability(request)
                _availabilityResponse.postValue(handleSharingAvailabilityResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            try {
                val response = networkRepository.bookRide(request)
                _bookRideResponse.postValue(handleBookRideResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleBookRideResponse(response: Response<BookingFcmData.BookResp>?): DataHandler<BookingFcmData.BookResp> {
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

    fun getPlaceDetails(key: String, latLong: String) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getAddress(key, latLong)
                if (response.isSuccessful) {
                    _locationInfo.postValue(response.body())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cancelRide(request: CancelRideRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.cancelRide(request)
                _cancelRideResponse.postValue(handleCancelRideResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleCancelRideResponse(response: Response<CancelRideResponse>?): DataHandler<CancelRideResponse> {
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

    private val _myVehicleResponse = MutableLiveData<DataHandler<MyVehicleResponse>>()
    val myVehicleResponse: LiveData<DataHandler<MyVehicleResponse>> =
        _myVehicleResponse

    private val _mapLocationsResponse =
        MutableLiveData<DataHandler<GetVehicleMapLocationsResponse>>()
    val mapLocationsResponse: LiveData<DataHandler<GetVehicleMapLocationsResponse>> =
        _mapLocationsResponse

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

    /// NEW integrations v1
    fun getOnlineMapVehicles(request: GetOnlineMapVehiclesRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getOnlineMapVehicles(request)
                _mapLocationsResponse.postValue(handleMapLocationsResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun handleMapLocationsResponse(response: Response<GetVehicleMapLocationsResponse>?): DataHandler<GetVehicleMapLocationsResponse> {
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


    private val _cancelTaxiRideResponse =
        MutableLiveData<DataHandler<CancelRideResponse>>()
    val cancelTaxiRideResponse: LiveData<DataHandler<CancelRideResponse>> =
        _cancelTaxiRideResponse

    fun cancelTaxiTrip(request: CancelRideRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.cancelRideRequest(request)
                _cancelTaxiRideResponse.postValue(handleCancelTaxiRideResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleCancelTaxiRideResponse(response: Response<CancelRideResponse>?): DataHandler<CancelRideResponse>? {
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