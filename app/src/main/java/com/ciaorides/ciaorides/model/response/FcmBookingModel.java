package com.ciaorides.ciaorides.model.response;

import java.io.Serializable;

public class FcmBookingModel implements Serializable {
    public String sourceAddress;
    public String destinationAddress;
    public String bookingNumber;
    public String orderId;

    public String driverName;
    public String vehicleNumber;
    public String time;
    public String driverNo;
    public String driverMobileNumber;
    public String driverPic;

    public String userPic;
    public String userId;

    public String otp;
    public BookingFcmData.BookResp.Response driverInfo;
    public String rideStatus;


}
