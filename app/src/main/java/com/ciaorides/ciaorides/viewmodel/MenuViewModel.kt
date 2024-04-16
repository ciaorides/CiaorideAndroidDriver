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

    private val _paymentsResponse = MutableLiveData<DataHandler<PaymentsResponse>>()

    val earningsResponse: LiveData<DataHandler<EarningsResponse>> =
        _earningsResponse

    val paymentResponse: LiveData<DataHandler<PaymentsResponse>> =
        _paymentsResponse

    fun getMyRides(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try{
                val response = networkRepository.getMyRides(request)
                _myRidesResposne.postValue(handleMyRidesResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getMyVehicles(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getMyVehicles(request)
                _myVehicleResponse.postValue(handleMyVehicleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteVehicle(request: DeleteVehicleRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.deleteVehicle(request)
                _deleteVehicleResponse.postValue(handleDeleteVehicleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteBankDetails(request: DeleteBankDetailsRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.deleteBankDetails(request)
                _deleteVehicleResponse.postValue(handleDeleteVehicleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteFav(request: DeleteFavRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.deleteFav(request)
                _deleteVehicleResponse.postValue(handleDeleteVehicleResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun getBankDetails(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getBankDetails(request)
                _bankDetailsResponse.postValue(handleBankDetailsResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            try {
                val response = networkRepository.getFav(request)
                _favResponse.postValue(handleFavResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            try {
                if (isEditBankDetails) {
                    val response = networkRepository.editBankDetails(request)
                    _saveBankResponse.postValue(handleSaveBankResponse(response))
                } else {
                    val response = networkRepository.saveBankDetails(request)
                    _saveBankResponse.postValue(handleSaveBankResponse(response))
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
            try {
                val response = networkRepository.changePassword(request)
                _changePasswordResponse.postValue(handleChangePasswordResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            try {
                val response = networkRepository.getEmergencyContactList(request)
                _contactResponse.postValue(handleGetContacts(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
            try {
                val response = networkRepository.getMyEarnings(request)
                _earningsResponse.postValue(handleEarningsResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPayments(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getPayments(request)
                _paymentsResponse.postValue(handlePaymentsResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPendingPayments(request: GlobalUserIdRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getPendingPayments(request)
                _paymentsResponse.postValue(handlePaymentsResponse(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

    private fun handlePaymentsResponse(response: Response<PaymentsResponse>): DataHandler<PaymentsResponse> {
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

    private val _addTransactionResponse = MutableLiveData<DataHandler<GlobalResponse>>()

    private val _withdrawTransactionResponse = MutableLiveData<DataHandler<GlobalResponse>>()

    private val _getTransactionResponse = MutableLiveData<DataHandler<GetTransactionResposne>>()

    val addTransactionResponse: LiveData<DataHandler<GlobalResponse>> =
        _addTransactionResponse

    val withdrawTransactionResponse: LiveData<DataHandler<GlobalResponse>> =
        _withdrawTransactionResponse

    val getTransactionResponse: LiveData<DataHandler<GetTransactionResposne>> =
        _getTransactionResponse

    fun postAddTransactions(request: AddTransactionRequest) {
        viewModelScope.launch {
            val response = networkRepository.postAddTransactions(request)
            _addTransactionResponse.postValue(handleAddTransactions(response))
        }
    }

    private fun handleAddTransactions(response: Response<GlobalResponse>): DataHandler<GlobalResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    fun postWithdrawTransactions(request: WithdrawTransactionRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.postWithdrawTransactions(request)
                _withdrawTransactionResponse.postValue(handleWithdrawTransactions(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleWithdrawTransactions(response: Response<GlobalResponse>): DataHandler<GlobalResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }

    fun getTransactions(request: GetTransactionRequest) {
        viewModelScope.launch {
            try {
                val response = networkRepository.getTransactions(request)
                _getTransactionResponse.postValue(handleGetTransactions(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleGetTransactions(response: Response<GetTransactionResposne>): DataHandler<GetTransactionResposne> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }


    private val _razorPayResponse = MutableLiveData<DataHandler<RazorPayResponse>>()
    val razorPayResponse: LiveData<DataHandler<RazorPayResponse>> =
        _razorPayResponse

    fun getRazorPayResponse() {
        viewModelScope.launch {
            try {
                val response = networkRepository.getRazorpayDetails()
                _razorPayResponse.postValue(handleRazorPayDetails(response))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handleRazorPayDetails(response: Response<RazorPayResponse>): DataHandler<RazorPayResponse> {
        if (response.isSuccessful) {
            response.body()?.let { data ->
                return DataHandler.SUCCESS(data)
            }
        }
        return DataHandler.ERROR(message = response.message())
    }



}