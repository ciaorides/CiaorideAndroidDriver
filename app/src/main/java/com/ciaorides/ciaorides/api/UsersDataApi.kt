package com.ciaorides.ciaorides.api

import com.ciaorides.ciaorides.model.UserDetailsItem
import com.ciaorides.ciaorides.model.request.*
import com.ciaorides.ciaorides.model.response.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UsersDataApi {
    @GET("v2/users")
    suspend fun getUsersDetails(): Response<List<UserDetailsItem>>

    @POST("driver1/driver_register")
    suspend fun doLogin(@Body loginRequest: LoginRequest): Response<UserResponse>

    @POST("taxi_flow/resend_otp")
    suspend fun resendOtp(@Body otpRequest: OtpRequest): Response<UserResponse>

    @POST("taxi_flow/banners")
    suspend fun getHomeBanners(@Body homeBannersRequest: HomeBannersRequest): Response<HomeBannersResponse>

    @POST("taxi_flow/get_locations_taxi")
    suspend fun recentSearch(@Body recentSearchRequest: RecentSearchRequest): Response<RecentSearchesResponse>

    @POST("taxi_flow/add_location_taxi")
    suspend fun addRecentFevSearch(@Body request: RecentFevRequest): Response<RecentFevResponse>


    @POST("taxi_flow/get_distance")
    suspend fun getVehicleInfo(@Body request: VehicleInfoRequest): Response<VehicleInfoResponse>

    @POST("taxi_flow/book_now")
    suspend fun bookRide(@Body request: BookRideRequest): Response<BookRideResponse>


    /* https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyAr29XeWWAeWZcrOgjjfs3iSnqkWtAz4No&latlng=2.1812,102.4266&sensor=true*/
    @GET("https://maps.googleapis.com/maps/api/geocode/json")
    suspend fun getAddress(
        @Query("key") key: String,
        @Query("latlng") latlng: String,
        @Query("sensor") sensor: Boolean,
    ): Response<AddressInfoResponse>

    @POST("taxi_flow/get_distance")
    suspend fun cancelRide(@Body request: CancelRideRequest): Response<CancelRideResponse>

    @POST("menuitems/get_my_rides")
    suspend fun getMyRides(@Body request: GlobalUserIdRequest): Response<MyRidesResponse>

    @POST("menuitems/get_my_vehicle_details")
    suspend fun getMyVehicles(@Body request: GlobalUserIdRequest): Response<MyVehicleResponse>

    @POST("menuitems/my_vehicle_details_delete")
    suspend fun deleteVehicle(@Body request: DeleteVehicleRequest): Response<GlobalResponse>

    @POST("menuitems/driver_bank_details")
    suspend fun getBankDetails(@Body request: GlobalUserIdRequest): Response<BankDetailsResponse>

    @POST("menuitems/driver_bank_details_delete")
    suspend fun deleteBankDetails(@Body request: DeleteBankDetailsRequest): Response<GlobalResponse>

    @POST("menuitems/get_favourites_list")
    suspend fun getFav(@Body request: GlobalUserIdRequest): Response<FavResponse>


    @POST("menuitems/driver_favourite_location_delete")
    suspend fun deleteFav(@Body request: DeleteFavRequest): Response<GlobalResponse>

    @POST("menuitems/user_profile")
    suspend fun userDetails(@Body request: GlobalUserIdRequest): Response<UserDetailsResponse>

    @POST("menuitems/add_driver_vehicles_step1")
    suspend fun addVehiclesStep1(@Body request: AddVehicleDetailsRequest): Response<AddVehiclesStage1Response>

    @POST("menuitems/add_driver_vehicles_step2")
    suspend fun addVehiclesStep2(@Body request: AddVehicleDetailsStage2Request): Response<AddVehiclesStage2Response>

    @POST("menuitems/add_driver_vehicles_step3")
    suspend fun addVehiclesStep3(@Body request: AddVehicleStage3Request): Response<AddVehicleStage3Response>


    @POST("taxi_flow/get_vehicle_brands")
    suspend fun getVehicleBrands(@Body request: BrandsRequest): Response<VehicleBrandsResponse>

    @POST("taxi_flow/get_vehicle_models")
    suspend fun getVehicleModels(@Body request: VehicleModelRequest): Response<VehicleModelsResponse>

    @POST("sharing/get_distance")
    suspend fun getSharingVehicleRequest(@Body request: RidesSharingRequest): Response<VehicleInfoResponse>

    @POST("sharing/check_availability")
    suspend fun checkAvailability(@Body request: CheckAvailabilityRequest): Response<SharingAvailabilityResponse>

    //    @Multipart
//    @POST("https://www.ciaorides.com/new/app/taxi_flow/upload")
//    suspend fun uploadImage(@Part files:List<MultipartBody.Part>): Response<ImageUploadResponse>
    @Multipart
    @POST("https://ciaorides.com/new/app/taxi_flow/upload")
    fun uploadImage1(
        @Part image: List<MultipartBody.Part>,
        @Part("upload_type") description: RequestBody
    ): Call<JsonObject>?

    @POST("driver1/check_in")
    suspend fun driverCheckIn(@Body request: DriverCheckInRequest): Response<GlobalResponse>

    @POST("driver1/check_in_validate")
    suspend fun checkInStatus(@Body request: GlobalUserIdRequest): Response<CheckInStatusResponse>

    @POST("driver1/reject_ride")
    suspend fun rejectRide(@Body request: RejectRideRequest): Response<GlobalResponse>

    @POST("taxi_flow/get_booking_info")
    suspend fun getRideDetails(@Body request: GlobalUserIdRequest): Response<BookingInfoResponse>

    @POST("driver1/accept_ride")
    suspend fun acceptRideRequest(@Body request: AcceptRideRequest): Response<GlobalResponse>

    @POST("driver1/home_page_data")
    suspend fun getHomePageRidesData(@Body request: GlobalUserIdRequest): Response<HomePageRidesResponse>

    @POST("menuitems/add_driver_bank_details_step1")
    suspend fun addBankAccount(@Body request: SaveBankDetailsRequest): Response<SaveBankResponse>

    @POST("menuitems/update_driver_bank_details_step1")
    suspend fun editBankDetails(@Body request: SaveBankDetailsRequest): Response<SaveBankResponse>

    @POST("menuitems/update_change_password")
    suspend fun changePassword(@Body request: ChangePassword): Response<ChangePasswordResponse>

    @POST("menuitems/get_emergency_contacts_list")
    suspend fun getEmergencyContactList(@Body request: GlobalUserIdRequest): Response<EmergencyContactResponse>

    @POST("menuitems/my_earnings")
    suspend fun getMyEarnings(@Body request: GlobalUserIdRequest): Response<EarningsResponse>

    @POST("taxi_flow/update_profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserDetailsResponse>

}