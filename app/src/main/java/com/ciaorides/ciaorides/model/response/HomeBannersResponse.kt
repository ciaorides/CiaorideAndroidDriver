package com.ciaorides.ciaorides.model.response


import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class HomeBannersResponse(
    val message: String,
    val response: Response,
    val status: Boolean
) : Parcelable {
    @Parcelize
    data class Response(
        val bottom: ArrayList<Bottom> = ArrayList(),
        val middle: List<Middle>,
        val top: List<Top>
    ) : Parcelable {
        @Parcelize
        data class Bottom(
            var banner_image: String = "",
            val banner_url: String = "",
            val created_date: String = "",
            val id: String = "",
            val status: String = "",
            val sub_type: String = "",
            val title: String = "",
            val type: String = "",
            val updated_date: String = ""
        ) : Parcelable

        @Parcelize
        data class Middle(
            val banner_image: String,
            val banner_url: String,
            val created_date: String,
            val id: String,
            val status: String,
            val sub_type: String,
            val title: String,
            val type: String,
            val updated_date: String
        ) : Parcelable

        @Parcelize
        data class Top(
            val banner_image: String,
            val banner_url: String,
            val created_date: String,
            val id: String,
            val status: String,
            val sub_type: String,
            val title: String,
            val type: String,
            val updated_date: String
        ) : Parcelable
    }
}