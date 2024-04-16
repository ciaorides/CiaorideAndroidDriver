package com.ciaorides.ciaorides.api

import com.ciaorides.ciaorides.fcm.BookingFcmData
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

    @POST("taxi_flow/register_user")
    suspend fun doLogin(@Body loginRequest: LoginRequest): Response<UserResponse>

    @POST("taxi_flow/resend_otp")
    suspend fun resendOtp(@Body otpRequest: OtpRequest): Response<UserResponse>

    @Headers("Content-Type: application/json")
    @POST("taxi_flow/banners")
    suspend fun getHomeBanners(@Body homeBannersRequest: HomeBannersRequest): Response<HomeBannersResponse>

    @Headers("Content-Type: application/json")
    @POST("taxi_flow/get_locations_taxi")
    suspend fun recentSearch(@Body recentSearchRequest: RecentSearchRequest): Response<RecentSearchesResponse>

    @POST("taxi_flow/add_location_taxi")
    suspend fun addRecentFevSearch(@Body request: RecentFevRequest): Response<RecentFevResponse>

    @POST("taxi_flow/get_distance")
    suspend fun getVehicleInfo(@Body request: VehicleInfoRequest): Response<VehicleInfoResponse>

    @POST("taxi_flow/book_now")
    suspend fun bookRide(@Body request: BookRideRequest): Response<BookingFcmData.BookResp>


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

    @POST("ws/update_profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserDetailsResponse>

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
//    @POST("https://ciaorides.com/new/app/taxi_flow/upload")
//    suspend fun uploadImage(@Part files:List<MultipartBody.Part>): Response<ImageUploadResponse>
    @Multipart
    @POST("https://ciaorides.com/new/app/taxi_flow/upload")
    suspend fun uploadImage(
        @Part image: List<MultipartBody.Part>,
        @Part upload_type: Int
    ): Response<ImageUploadResponse>

    @POST("sharing/offer_a_ride")
    suspend fun offerARide(@Body request: OfferARideRequest): Response<OfferARideResponse>

    @POST("sharing/accept_order")
    suspend fun acceptOrder(@Body request: AcceptOrderRequest): Response<GlobalResponse>

    @POST("sharing/decline_order")
    suspend fun declineOrder(@Body request: DeclineOrderRequest): Response<GlobalResponse>

    @POST("sharing/offer_a_ride_requests_from_user")
    suspend fun rideRequestsFromUser(@Body request: RideRequestsFromUserRequest): Response<RideRequestsFromUserResponse>

    @POST("sharing/pickup_user")
    suspend fun pickupUser(@Body request: PickupUserRequest): Response<GlobalResponse>

    @POST("sharing/complete_ride")
    suspend fun completeOfferRide(@Body request: CompleteOfferRideRequest): Response<GlobalResponse>

    @POST("sharing/start_ride")
    suspend fun startOfferRide(@Body request: StartOfferRideRequest): Response<GlobalResponse>

    @POST("sharing/rider_cancel_order")
    suspend fun riderCancelOrder(@Body request: RiderCancelOrderRequest): Response<GlobalResponse>

    @POST("taxi_flow/get_nearby_rider_lat_longs")
    suspend fun getOnlineMapVehicles(@Body request: GetOnlineMapVehiclesRequest): Response<GetVehicleMapLocationsResponse>

    @Multipart
    @POST("https://ciaorides.com/new/app/taxi_flow/upload")
    fun uploadImage1(
        @Part image: List<MultipartBody.Part>,
        @Part("upload_type") description: RequestBody
    ): Call<JsonObject>?

    @POST("driver1/complete_ride")
    suspend fun completeTaxiRide(@Body request: CompleteOfferRideRequest): Response<GlobalResponse>

    @POST("menuitems/get_razorpay_details")
    suspend fun getRazorpayDetails(): Response<RazorPayResponse>

    @POST("driver1/user_cancel_ride")
    suspend fun cancelTaxiRide(@Body request: CancelRideRequest): Response<CancelRideResponse>
}