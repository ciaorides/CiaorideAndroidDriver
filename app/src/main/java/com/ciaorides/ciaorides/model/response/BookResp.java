package com.ciaorides.ciaorides.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookResp {

    @SerializedName("booking_id")
    public int booking_id;
    @SerializedName("order_id")
    public int order_id;
    @SerializedName("otp")
    public int otp;
    @SerializedName("time")
    public String time;
    @SerializedName("response")
    public List<Response> response;
    @SerializedName("message")
    public String message;
    @SerializedName("status")
    public boolean status;
    @SerializedName("user_id")
    public String user_id;


    public static class Response {
        @SerializedName("distance")
        public String distance;
        @SerializedName("lng")
        public String lng;
        @SerializedName("lat")
        public String lat;
        @SerializedName("vehicle_type")
        public String vehicle_type;
        @SerializedName("vehicle_id")
        public String vehicle_id;
        @SerializedName("driver_id")
        public String driver_id;
        @SerializedName("mobile")
        public String mobile;
    }
}
