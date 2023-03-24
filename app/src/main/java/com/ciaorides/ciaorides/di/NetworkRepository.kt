package com.ciaorides.ciaorides.di

import com.ciaorides.ciaorides.api.UsersDataApi
import com.ciaorides.ciaorides.model.UserDetailsItem
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.request.RecentFevRequest
import com.ciaorides.ciaorides.model.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val usersDataApi: UsersDataApi
) {

    suspend fun getUsersList(): Response<List<UserDetailsItem>> {
        return usersDataApi.getUsersDetails()
    }

    suspend fun doLogin(loginRequest: LoginRequest): Response<UserResponse> {
        return usersDataApi.doLogin(loginRequest)
    }

    suspend fun resendOtp(otpRequest: OtpRequest): Response<UserResponse> {
        return usersDataApi.resendOtp(otpRequest)
    }

    suspend fun getHomeBanners(homeBannersRequest: HomeBannersRequest): Response<HomeBannersResponse> {
        return usersDataApi.getHomeBanners(homeBannersRequest)
    }

    suspend fun recentSearch(recentSearchRequest: RecentSearchRequest): Response<RecentSearchesResponse> {
        return usersDataApi.recentSearch(recentSearchRequest)
    }

    suspend fun addRecentFev(recentSearchRequest: RecentFevRequest): Response<RecentFevResponse> {
        return usersDataApi.addRecentFevSearch(recentSearchRequest)
    }

    suspend fun getVehicleInfo(request: VehicleInfoRequest): Response<VehicleInfoResponse> {
        return usersDataApi.getVehicleInfo(request)
    }

    suspend fun getSharingVehiclesRequest(request: RidesSharingRequest): Response<VehicleInfoResponse> {
        return usersDataApi.getSharingVehicleRequest(request)
    }

    suspend fun checkAvailability(request: CheckAvailabilityRequest): Response<SharingAvailabilityResponse> {
        return usersDataApi.checkAvailability(request)
    }

    suspend fun get(request: RidesSharingRequest): Response<VehicleInfoResponse> {
        return usersDataApi.getSharingVehicleRequest(request)
    }

    suspend fun bookRide(request: BookRideRequest): Response<BookRideResponse>? {
        return try {
            usersDataApi.bookRide(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getAddress(key: String, logLong: String): Response<AddressInfoResponse> {
        return usersDataApi.getAddress(key, logLong, true)
    }

    suspend fun cancelRide(request: CancelRideRequest): Response<CancelRideResponse> {
        return usersDataApi.cancelRide(request)
    }

    suspend fun getMyRides(request: GlobalUserIdRequest): Response<MyRidesResponse> {
        return usersDataApi.getMyRides(request)
    }

    suspend fun getMyVehicles(request: GlobalUserIdRequest): Response<MyVehicleResponse> {
        return usersDataApi.getMyVehicles(request)
    }

    suspend fun getBankDetails(request: GlobalUserIdRequest): Response<BankDetailsResponse> {
        return usersDataApi.getBankDetails(request)
    }

    suspend fun deleteVehicle(request: DeleteVehicleRequest): Response<GlobalResponse> {
        return usersDataApi.deleteVehicle(request)
    }

    suspend fun deleteBankDetails(request: DeleteBankDetailsRequest): Response<GlobalResponse> {
        return usersDataApi.deleteBankDetails(request)
    }

    suspend fun getFav(request: GlobalUserIdRequest): Response<FavResponse> {
        return usersDataApi.getFav(request)
    }

    suspend fun deleteFav(request: DeleteFavRequest): Response<GlobalResponse> {
        return usersDataApi.deleteFav(request)
    }

    suspend fun getUserDetails(request: GlobalUserIdRequest): Response<UserDetailsResponse> {
        return usersDataApi.userDetails(request)
    }

    suspend fun getVehicleBrands(request: BrandsRequest): Response<VehicleBrandsResponse> {
        return usersDataApi.getVehicleBrands(request)
    }

    suspend fun getVehicleModels(request: VehicleModelRequest): Response<VehicleModelsResponse> {
        return usersDataApi.getVehicleModels(request)
    }

    suspend fun addVehiclesStage1(request: AddVehicleDetailsRequest): Response<AddVehiclesStage1Response>? {
        return try {
            usersDataApi.addVehiclesStep1(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addVehiclesStage2(request: AddVehicleDetailsStage2Request): Response<AddVehiclesStage2Response> {
        return usersDataApi.addVehiclesStep2(request)
    }

    suspend fun uploadImage(request: ArrayList<MultipartBody.Part>): Response<ImageUploadResponse>? {
        return try {
            usersDataApi.uploadImage(request, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}