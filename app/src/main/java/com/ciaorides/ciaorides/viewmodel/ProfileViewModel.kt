package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.request.ImageUploadRequest
import com.ciaorides.ciaorides.model.response.ImageUploadResponse
import com.ciaorides.ciaorides.model.response.UpdateProfileRequest
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _imageUploadResponse = MutableLiveData<ImageUploadResponse>()
    val imageUploadResponse: LiveData<ImageUploadResponse> = _imageUploadResponse


    private val _userDetailsResponse = MutableLiveData<DataHandler<UserDetailsResponse>>()
    val userDetailsResponse: LiveData<DataHandler<UserDetailsResponse>> = _userDetailsResponse
    fun imageUpload(request: ArrayList<MultipartBody.Part>, value: RequestBody) {
        viewModelScope.launch {
            val imageUploadResponse = networkRepository.uploadImage(request, value)
            //   Log.d("response", imageUploadResponse?.toString()!!)
            //val test = Gson().fromJson(Gson().toJson(imageUploadResponse), ImageUploadResponse::class.java)
        }
    }
    fun profileImageUpload(request: ArrayList<MultipartBody.Part>, value: RequestBody) {
        viewModelScope.launch {
            val imageUploadResponse = networkRepository.profileImageUpload(request, value)
            //   Log.d("response", imageUploadResponse?.toString()!!)
            //val test = Gson().fromJson(Gson().toJson(imageUploadResponse), ImageUploadResponse::class.java)
        }
    }
    fun updateUserProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            val response = networkRepository.updateUserData(request)
            _userDetailsResponse.postValue(handleUserDetails(response))
        }
    }
    fun getUserDetails(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getUserDetails(request)
            _userDetailsResponse.postValue(handleUserDetails(response))
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

//    override fun imageUploadResponseHanding(imageUploadResponse: ImageUploadResponse) {
//        Log.d("callback image", imageUploadResponse.result_arr.totalFiles[0].file_path);
//        _imageUploadResponse.postValue(imageUploadResponse)
//
//        //  handleResponse(imageUploadResponse)
//    }

//    private fun handleResponse(response: ImageUploadResponse?): DataHandler<ImageUploadResponse> {
//        if (response? == true) {
//            response?.let { data ->
//                return DataHandler.SUCCESS(data)
//            }
//        }
//        return DataHandler.ERROR(message = response?.toString())
//    }
}