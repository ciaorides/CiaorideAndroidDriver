package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.response.*
import com.ciaorides.ciaorides.model.response.GlobalResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {

    private val _myRidesResposne = MutableLiveData<DataHandler<MyRidesResponse>>()
    val myRidesResponse: LiveData<DataHandler<MyRidesResponse>> =
        _myRidesResposne

    private val _myVehicleResponse = MutableLiveData<DataHandler<MyVehicleResponse>>()
    val myVehicleResponse: LiveData<DataHandler<MyVehicleResponse>> =
        _myVehicleResponse

    private val _favResponse = MutableLiveData<DataHandler<FavResponse>>()
    val favResponse: LiveData<DataHandler<FavResponse>> =
        _favResponse

    private val _deleteVehicleResponse = MutableLiveData<DataHandler<GlobalResponse>>()
    val deleteVehicleResponse: LiveData<DataHandler<GlobalResponse>> =
        _deleteVehicleResponse

    private val _bankDetailsResponse = MutableLiveData<DataHandler<BankDetailsResponse>>()
    val bankDetailsResponse: LiveData<DataHandler<BankDetailsResponse>> =
        _bankDetailsResponse

    fun getMyRides(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getMyRides(request)
            _myRidesResposne.postValue(handleMyRidesResponse(response))
        }
    }

    fun getMyVehicles(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getMyVehicles(request)
            _myVehicleResponse.postValue(handleMyVehicleResponse(response))
        }
    }

    fun deleteVehicle(request: DeleteVehicleRequest) {
        viewModelScope.launch {
            val response = networkRepository.deleteVehicle(request)
            _deleteVehicleResponse.postValue(handleDeleteVehicleResponse(response))
        }
    }

    fun deleteBankDetails(request: DeleteBankDetailsRequest) {
        viewModelScope.launch {
            val response = networkRepository.deleteBankDetails(request)
            _deleteVehicleResponse.postValue(handleDeleteVehicleResponse(response))
        }
    }

    fun deleteFav(request: DeleteFavRequest) {
        viewModelScope.launch {
            val response = networkRepository.deleteFav(request)
            _deleteVehicleResponse.postValue(handleDeleteVehicleResponse(response))
        }
    }


    fun getBankDetails(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getBankDetails(request)
            _bankDetailsResponse.postValue(handleBankDetailsResponse(response))
        }
    }

    private fun handleBankDetailsResponse(response: Response<BankDetailsResponse>?): DataHandler<BankDetailsResponse> {
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

    private fun handleDeleteVehicleResponse(response: Response<GlobalResponse>?): DataHandler<GlobalResponse> {
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

    private fun handleMyRidesResponse(response: Response<MyRidesResponse>?): DataHandler<MyRidesResponse> {
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

    fun getFav(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getFav(request)
            _favResponse.postValue(handleFavResponse(response))
        }
    }

    private fun handleFavResponse(response: Response<FavResponse>): DataHandler<FavResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

}