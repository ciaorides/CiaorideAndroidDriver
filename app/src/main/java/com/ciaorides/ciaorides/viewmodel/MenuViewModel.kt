package com.ciaorides.ciaorides.viewmodel

import android.text.TextUtils
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

    var nameOfBank = MutableLiveData<String>()
    var location = MutableLiveData<String>()
    var accountHolderName = MutableLiveData<String>()
    var accountNumber = MutableLiveData<String>()
    var ifscCode = MutableLiveData<String>()
    var isEditBankDetails: Boolean = false
    lateinit var bankId: String

    var _showErrorMessage = MutableLiveData<Any>()
    val showErrorMessage: LiveData<Any> = _showErrorMessage

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
    private val _saveBankResponse = MutableLiveData<DataHandler<SaveBankResponse>>()
    val saveBankResponse: LiveData<DataHandler<SaveBankResponse>> =
        _saveBankResponse
    private val _changePasswordResponse = MutableLiveData<DataHandler<ChangePasswordResponse>>()
    val changePasswordResponse: LiveData<DataHandler<ChangePasswordResponse>> =
        _changePasswordResponse

    private val _contactResponse = MutableLiveData<DataHandler<EmergencyContactResponse>>()

    val contactResponse: LiveData<DataHandler<EmergencyContactResponse>> =
        _contactResponse

    private val _earningsResponse = MutableLiveData<DataHandler<EarningsResponse>>()

    val earningsResponse: LiveData<DataHandler<EarningsResponse>> =
        _earningsResponse

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

    private fun saveBankDetails(request: SaveBankDetailsRequest) {
        viewModelScope.launch {
            if (isEditBankDetails) {
                val response = networkRepository.editBankDetails(request)
                _saveBankResponse.postValue(handleSaveBankResponse(response))
            } else {
                val response = networkRepository.saveBankDetails(request)
                _saveBankResponse.postValue(handleSaveBankResponse(response))
            }
        }
    }

    private fun handleSaveBankResponse(response: Response<SaveBankResponse>): DataHandler<SaveBankResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    fun changePassword(request: ChangePassword) {
        viewModelScope.launch {
            val response = networkRepository.changePassword(request)
            _changePasswordResponse.postValue(handleChangePasswordResponse(response))
        }
    }

    private fun handleChangePasswordResponse(response: Response<ChangePasswordResponse>): DataHandler<ChangePasswordResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    fun getEmergencyContactList(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getEmergencyContactList(request)
            _contactResponse.postValue(handleGetContacts(response))
        }
    }

    private fun handleGetContacts(response: Response<EmergencyContactResponse>): DataHandler<EmergencyContactResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    fun getMyEarnings(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            val response = networkRepository.getMyEarnings(request)
            _earningsResponse.postValue(handleEarningsResponse(response))
        }
    }

    private fun handleEarningsResponse(response: Response<EarningsResponse>): DataHandler<EarningsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }


    fun validateBankForm(userId:String) {
        if (TextUtils.isEmpty(nameOfBank.value)) {
            _showErrorMessage.value = "Please enter Bank Name"
        } else if (TextUtils.isEmpty(location.value)) {
            _showErrorMessage.value = "Please enter Location Name"
        } else if (TextUtils.isEmpty(accountHolderName.value)) {
            _showErrorMessage.value = "Please enter Account Holder name"
        } else if (TextUtils.isEmpty(accountNumber.value)) {
            _showErrorMessage.value = "Please enter Account Number"
        } else if (TextUtils.isEmpty(ifscCode.value)) {
            _showErrorMessage.value = "Please enter IFSC Code"
        } else {
            val getBankDetails = SaveBankDetailsRequest(
                id = if (isEditBankDetails) bankId else null,
                user_id = userId,
                country_id = "101",
                bank_name = nameOfBank.value.toString(),
                account_holder_name = accountHolderName.value.toString(),
                account_number = accountNumber.value.toString(),
                ifsc_code = ifscCode.value.toString()
            )
            saveBankDetails(getBankDetails)
        }
    }

}