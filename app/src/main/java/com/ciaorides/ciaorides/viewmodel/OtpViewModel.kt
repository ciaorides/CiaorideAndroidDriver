package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.UserDetailsItem
import com.ciaorides.ciaorides.model.request.LoginRequest
import com.ciaorides.ciaorides.model.request.OtpRequest
import com.ciaorides.ciaorides.model.response.UserResponse
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _userResponse = MutableLiveData<DataHandler<UserResponse>>()
    val userResponse: LiveData<DataHandler<UserResponse>> = _userResponse

    private val _userDetailsList = MutableLiveData<DataHandler<UserResponse>>()
    val userDetailsList: LiveData<DataHandler<UserResponse>> = _userDetailsList

    fun resendOtp(otpRequest: OtpRequest) {
        viewModelScope.launch {
            val response = networkRepository.resendOtp(otpRequest)
            _userResponse.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<UserResponse>): DataHandler<UserResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.errorBody().toString())
    }

    fun validateUser(loginRequest: LoginRequest) {
        viewModelScope.launch {
            val response = networkRepository.doLogin(loginRequest)
            _userDetailsList.postValue(handleResponse(response))
        }
    }

    private fun handleUserResponse(response: Response<UserResponse>): DataHandler<UserResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.errorBody().toString())
    }
}