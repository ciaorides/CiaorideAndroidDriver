package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.DriverCheckInRequest
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.GlobalResponse
import com.ciaorides.ciaorides.model.response.MyVehicleResponse
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


    fun getMyVehicles(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getMyVehicles(request)
            _myVehicleResponse.postValue(handleMyVehicleResponse(response))
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

    fun checkIn(request: DriverCheckInRequest) {
        viewModelScope.launch {
            val response = networkRepository.driverCheck(request)
            _checkInResponse.postValue(handleCheckInResponse(response,request.check_in_status))
        }
    }
    private fun handleCheckInResponse(response: Response<GlobalResponse>?, checkInStatus: String): DataHandler<GlobalResponse> {
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




}
