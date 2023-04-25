package com.ciaorides.ciaorides.view.activities

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.viewbinding.ViewBinding
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.model.response.UserResponse
import com.ciaorides.ciaorides.model.response.UserSingleton
import com.ciaorides.ciaorides.utils.Constants

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B
    protected abstract fun init()
    var permission: PermissionsCallBack? = null

    val CAMERA_PERMISSION_CODE = 100
    val STORAGE_PERMISSION_CODE = 101
    protected fun updateToolBar(ivUserBadge: ImageView, ivUserImage: ImageView) {
        when (Constants.getValue(this@BaseActivity, Constants.BADGE)) {
            "red" -> {
                ivUserBadge.setImageResource(R.drawable.ic_red_badge)
            }
            "orange" -> {
                ivUserBadge.setImageResource(R.drawable.ic_orange_bagde)
            }
            "green" -> {
                ivUserBadge.setImageResource(R.drawable.ic_green_badge)
            }
            else -> {
                ivUserBadge.isVisible = false
            }
        }

        Constants.showGlide(
            ivUserImage.context,
            Constants.getValue(this@BaseActivity, Constants.USER_IMAGE), ivUserImage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        init()
    }

    abstract fun getViewBinding(): B

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun checkPermissionState(permissions: Array<String>): Boolean {
        var isPermissionGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this@BaseActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = false
                break
            }
        }
        return isPermissionGranted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (checkPermissionState(permissions)) {
                permission?.let {
                    it.onPermissionGranted();
                }
            }
        }
    }


    fun saveUserData(response: UserResponse.Response) {
        response.id?.let { userId ->
            Constants.saveValue(this@BaseActivity, Constants.USER_ID, userId)
        }
        /*response.first_name?.let { firstName ->
            Constants.saveValue(applicationContext, Constants.USER_ID, firstName)
        }

        response.last_name?.let { lastName ->
            Constants.saveValue(applicationContext, Constants.USER_ID, lastName)
        }
        response.email_id?.let { email ->
            Constants.saveValue(applicationContext, Constants.USER_ID, email)
        }*/
    }

    interface PermissionsCallBack {
        fun onPermissionGranted()
    }

}