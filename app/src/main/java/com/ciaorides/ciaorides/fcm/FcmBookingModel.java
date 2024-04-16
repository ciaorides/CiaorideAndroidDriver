package com.ciaorides.ciaorides.fcm;

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
    public String amount;

    public String userFromLat;

    public String userName;

    public String userMobile;

    public String userFromLng;

    public String userToLat;

    public String userToLng;

    public String userPic;
    public String userId;

    public String otp;

    public String rideType;
    public BookingFcmData.BookResp.Response driverInfo;
    public String rideStatus;
}
