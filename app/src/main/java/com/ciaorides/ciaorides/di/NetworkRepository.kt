package com.ciaorides.ciaorides.di

import android.content.Context
import android.util.Log
import com.ciaorides.ciaorides.api.UsersDataApi
import com.ciaorides.ciaorides.model.AddVehicleImageUpload
import com.ciaorides.ciaorides.model.ImageUpload
import com.ciaorides.ciaorides.fcm.BookingFcmData
import com.ciaorides.ciaorides.model.UserDetailsItem
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.request.RecentFevRequest
import com.ciaorides.ciaorides.model.response.*
import com.ciaorides.ciaorides.view.fragments.AddVehiclesStep2Fragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val usersDataApi: UsersDataApi
) {

    var imageUploadResponse: ImageUploadResponse? = null
    suspend fun getUsersList(): Response<List<UserDetailsItem>> {
        return usersDataApi.getUsersDetails()
    }

    suspend fun doLogin(loginRequest: LoginRequest): Response<UserResponse> {
        return usersDataApi.doLogin(loginRequest)
    }

    suspend fun resendOtp(otpRequest: OtpRequest): Response<UserResponse> {
        return usersDataApi.resendOtp(otpRequest)
    }

    suspend fun getHomeBanners(homeBannersRequest: HomeBannersRequest): Response<HomeBannersResponse>? {

        return try {
            return usersDataApi.getHomeBanners(homeBannersRequest)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

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

    suspend fun bookRide(request: BookRideRequest): Response<BookingFcmData.BookResp>? {
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

    suspend fun updateUserData(request: UpdateProfileRequest): Response<UserDetailsResponse> {
        return usersDataApi.updateProfile(request)
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

    suspend fun addVehiclesStage3(request: AddVehicleStage3Request): Response<AddVehicleStage3Response> {
        return usersDataApi.addVehiclesStep3(request)
    }



    companion object {
        private var imageUpload: ImageUpload? = null
        private var imageUploadVehicle: AddVehicleImageUpload? = null
        fun setInterfaceInstance(context: Context) {

            imageUpload = context as ImageUpload?
        }
        fun setInterfaceInstanceAddVehicle(context: Context) {
            imageUploadVehicle = context as AddVehicleImageUpload?
        }
    }
    fun vehicleUploadImage(
        request: ArrayList<MultipartBody.Part>,
        value: RequestBody
    ) {
        val call: Call<JsonObject>? = usersDataApi.uploadImage1(request, value)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    Log.d("Upload Image", "Upload successful")
                    //     if (imageUpload == null) return@OnClickListener
                    imageUploadVehicle?.imageUploadResponseHanding(response)
//                    Gson().fromJson(
//                        Gson().toJson(response),
//                        ImageUploadResponse::class.java)
                    Log.d("Upload Image", imageUploadVehicle.toString() + "Upload successful")

                } else {
                    Log.e("Upload Image", "Upload failed: " + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("Upload Image", "Upload FAIL")
                //TODO("Not yet implemented")
            }
        })

        /*  return try {
              usersDataApi.uploadImage1(request,value)
          } catch (e: Exception) {
              e.printStackTrace()
              null
          }*/
    }




    fun uploadImage(
        request: ArrayList<MultipartBody.Part>,
        value: RequestBody
    ) {


        var imageUploadResponse: ImageUploadResponse? = null

        val call: Call<JsonObject>? = usersDataApi.uploadImage1(request, value)
        call?.enqueue(object : Callback<JsonObject> {
            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                if (response.isSuccessful) {
                    Log.d("Upload Image", "Upload successful")
                    //     if (imageUpload == null) return@OnClickListener
                    imageUpload?.imageUploadResponseHanding(response)
//                    Gson().fromJson(
//                        Gson().toJson(response),
//                        ImageUploadResponse::class.java)
                    Log.d("Upload Image", imageUpload.toString() + "Upload successful")

                } else {
                    Log.e("Upload Image", "Upload failed: " + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("Upload Image", "Upload FAIL")
                //TODO("Not yet implemented")
            }
        })

        /*  return try {
              usersDataApi.uploadImage1(request,value)
          } catch (e: Exception) {
              e.printStackTrace()
              null
          }*/
    }



    suspend fun offerRide(request: OfferARideRequest): Response<OfferARideResponse>? {
        return try {
            usersDataApi.offerARide(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun acceptOrder(request: AcceptOrderRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.acceptOrder(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun startOfferRide(request: StartOfferRideRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.startOfferRide(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun riderCancelOrder(request: RiderCancelOrderRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.riderCancelOrder(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun declineOrder(request: DeclineOrderRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.declineOrder(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun rideRequestsFromUser(request: RideRequestsFromUserRequest): Response<RideRequestsFromUserResponse>? {
        return try {
            usersDataApi.rideRequestsFromUser(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun pickupUser(request: PickupUserRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.pickupUser(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun completeRide(request: CompleteOfferRideRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.completeOfferRide(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getOnlineMapVehicles(request: GetOnlineMapVehiclesRequest): Response<GetVehicleMapLocationsResponse>? {
        return try {
            usersDataApi.getOnlineMapVehicles(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun completeTaxiRide(request: CompleteOfferRideRequest): Response<GlobalResponse>? {
        return try {
            usersDataApi.completeTaxiRide(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun cancelRideRequest(request: CancelRideRequest): Response<CancelRideResponse>? {
        return try {
            usersDataApi.cancelTaxiRide(request)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getRazorpayDetails() : Response<RazorPayResponse>{
        return usersDataApi.getRazorpayDetails()
    }
}