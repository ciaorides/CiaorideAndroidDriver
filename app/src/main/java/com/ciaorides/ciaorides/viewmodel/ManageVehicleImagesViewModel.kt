package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.response.*
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    private val _addVehiclesStage3Response =
        MutableLiveData<DataHandler<AddVehicleStage3Response>>()
    val addVehiclesStage3Response: LiveData<DataHandler<AddVehicleStage3Response>> =
        _addVehiclesStage3Response


    fun imageUpload(request: ArrayList<MultipartBody.Part>, value: RequestBody) {
        viewModelScope.launch {
            val response = networkRepository.uploadImage(request, value)
            // _imageUploadResponse.postValue(handleResponse(response))
        }
    }

    fun vehicleImageUpload(request: ArrayList<MultipartBody.Part>, value: RequestBody) {
        viewModelScope.launch {
            val response = networkRepository.vehicleUploadImage(request, value)
            // _imageUploadResponse.postValue(handleResponse(response))
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

    fun addVehicle3(request: AddVehicleStage3Request) {
        viewModelScope.launch {
            val response = networkRepository.addVehiclesStage3(request)
            _addVehiclesStage3Response.postValue(handleAddVehiclesStage3Response(response))
            //   Log.d("response", imageUploadResponse?.toString()!!)
            //val test = Gson().fromJson(Gson().toJson(imageUploadResponse), ImageUploadResponse::class.java)
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
    private fun handleAddVehiclesStage3Response(response: Response<AddVehicleStage3Response>?): DataHandler<AddVehicleStage3Response> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }
}