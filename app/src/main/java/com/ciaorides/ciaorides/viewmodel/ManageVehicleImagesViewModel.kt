package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.AddVehicleDetailsRequest
import com.ciaorides.ciaorides.model.request.AddVehicleDetailsStage2Request
import com.ciaorides.ciaorides.model.request.BrandsRequest
import com.ciaorides.ciaorides.model.request.VehicleModelRequest
import com.ciaorides.ciaorides.model.response.*
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ManageVehicleImagesViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _imageUploadResponse = MutableLiveData<DataHandler<ImageUploadResponse>>()
    val imageUploadResponse: LiveData<DataHandler<ImageUploadResponse>> = _imageUploadResponse

    private val _vehicleBrandsResponse = MutableLiveData<DataHandler<VehicleBrandsResponse>>()
    val vehicleBrandsResponse: LiveData<DataHandler<VehicleBrandsResponse>> = _vehicleBrandsResponse

    private val _vehicleModelsResponse = MutableLiveData<DataHandler<VehicleModelsResponse>>()
    val vehicleModelsResponse: LiveData<DataHandler<VehicleModelsResponse>> = _vehicleModelsResponse

    private val _addVehiclesStage1Response =
        MutableLiveData<DataHandler<AddVehiclesStage1Response>>()
    val addVehiclesStageResponse: LiveData<DataHandler<AddVehiclesStage1Response>> =
        _addVehiclesStage1Response

    private val _addVehiclesStage2Response =
        MutableLiveData<DataHandler<AddVehiclesStage2Response>>()
    val addVehiclesStage2Response: LiveData<DataHandler<AddVehiclesStage2Response>> =
        _addVehiclesStage2Response


    fun imageUpload(request: ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            val response = networkRepository.uploadImage(request)
            _imageUploadResponse.postValue(handleResponse(response))
        }
    }

    fun getVehicleBrands(request: BrandsRequest) {
        viewModelScope.launch {
            val response = networkRepository.getVehicleBrands(request)
            _vehicleBrandsResponse.postValue(handleVehicleBrandsResponse(response))
        }
    }

    fun getVehicleModels(request: VehicleModelRequest) {
        viewModelScope.launch {
            val response = networkRepository.getVehicleModels(request)
            _vehicleModelsResponse.postValue(handleVehicleModelResponse(response))
        }
    }

    fun addVehiclesStage1(request: AddVehicleDetailsRequest) {
        viewModelScope.launch {
            val response = networkRepository.addVehiclesStage1(request)
            _addVehiclesStage1Response.postValue(handleAddVehiclesStage1Response(response))
        }
    }

    fun addVehiclesStage2(request: AddVehicleDetailsStage2Request) {
        viewModelScope.launch {
            val response = networkRepository.addVehiclesStage2(request)
            _addVehiclesStage2Response.postValue(handleAddVehiclesStage2Response(response))
        }
    }

    private fun handleResponse(response: Response<ImageUploadResponse>?): DataHandler<ImageUploadResponse> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }

    private fun handleVehicleBrandsResponse(response: Response<VehicleBrandsResponse>?): DataHandler<VehicleBrandsResponse> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }

    private fun handleVehicleModelResponse(response: Response<VehicleModelsResponse>?): DataHandler<VehicleModelsResponse> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }

    private fun handleAddVehiclesStage2Response(response: Response<AddVehiclesStage2Response>?): DataHandler<AddVehiclesStage2Response> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }

    private fun handleAddVehiclesStage1Response(response: Response<AddVehiclesStage1Response>?): DataHandler<AddVehiclesStage1Response> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }
}