package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.request.HomeBannersRequest
import com.ciaorides.ciaorides.model.request.UpdateProfileRequest
import com.ciaorides.ciaorides.model.response.HomeBannersResponse
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _homeBannersResponse = MutableLiveData<DataHandler<HomeBannersResponse>>()
    val homeBannersResponse: LiveData<DataHandler<HomeBannersResponse>> = _homeBannersResponse


    private val _userDetailsResponse = MutableLiveData<DataHandler<UserDetailsResponse>>()
    val userDetailsResponse: LiveData<DataHandler<UserDetailsResponse>> = _userDetailsResponse

    fun validateUser(bannersRequest: HomeBannersRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getHomeBanners(bannersRequest)
                _homeBannersResponse.postValue(handleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleResponse(response: Response<HomeBannersResponse>?): DataHandler<HomeBannersResponse> {
        if (response != null && response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = "Something went wrong...")
    }

    fun getUserDetails(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getUserDetails(request)
                _userDetailsResponse.postValue(handleUserDetails(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
 fun updateUserProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.updateUserData(request)
                _userDetailsResponse.postValue(handleUserDetails(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
}
