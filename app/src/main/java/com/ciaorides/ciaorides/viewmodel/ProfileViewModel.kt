package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.ImageUploadRequest
import com.ciaorides.ciaorides.model.response.ImageUploadResponse
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {

    private val _imageUploadResponse = MutableLiveData<DataHandler<ImageUploadResponse>>()
    val imageUploadResponse: LiveData<DataHandler<ImageUploadResponse>> = _imageUploadResponse

    fun imageUpload(request: ArrayList<MultipartBody.Part>) {
        viewModelScope.launch {
            val response = networkRepository.uploadImage(request)
            _imageUploadResponse.postValue(handleResponse(response))
        }
    }
//    fun imageUpload(request: ImageUploadRequest) {
//        viewModelScope.launch {
//            val response = networkRepository.uploadImage(request)
//            _imageUploadResponse.postValue(handleResponse(response))
//        }
//    }

    private fun handleResponse(response: Response<ImageUploadResponse>?): DataHandler<ImageUploadResponse> {
        if (response?.isSuccessful == true) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response?.errorBody().toString())
    }
}