package com.ciaorides.ciaorides.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

     fun checkPermissions(
        activity: Activity,
        permissionType: String,
        callBack: (Boolean) -> Unit?
    ) {
        if (ContextCompat.checkSelfPermission(
                activity,
                permissionType
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permissionType
                )
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permissionType), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permissionType), 1
                )
            }
        } else {
            callBack.invoke(true)
        }
    }
}