package com.ciaorides.ciaorides.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ciaorides.ciaorides.di.NetworkRepository
import com.ciaorides.ciaorides.model.UserDetailsItem
import com.ciaorides.ciaorides.utils.DataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserDetailsViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _userDetailsList = MutableLiveData<DataHandler<List<UserDetailsItem>>>()
    val userDetailsList: LiveData<DataHandler<List<UserDetailsItem>>> = _userDetailsList

    fun getUserList() {
        _userDetailsList.postValue(DataHandler.LOADING())
        viewModelScope.launch {
            val response = networkRepository.getUsersList()
            _userDetailsList.postValue(handleResponse(response))
        }
    }

    private fun handleResponse(response: Response<List<UserDetailsItem>>): DataHandler<List<UserDetailsItem>> {
        if (response.isSuccessful) {
            response.body()?.let { usersList ->
                return DataHandler.SUCCESS(usersList)
            }
        }
        return DataHandler.ERROR(message = response.errorBody().toString())
    }
}