package com.ciaorides.ciaorides.view.activities

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.ciaorides.ciaorides.model.response.UserResponse
import com.ciaorides.ciaorides.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.gun0912.tedpermission.PermissionListener


abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {
    private var mRegistrationBroadcastReceiver: BroadcastReceiver? = null
    protected lateinit var binding: B
    protected abstract fun init()
    var permission: PermissionsCallBack? = null

    val CAMERA_PERMISSION_CODE = 100
    val STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)
        askNotificationPermission()
        init()
        mRegistrationBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                /*val errorMessage =
                    intent.getStringExtra(MyFirebaseInstanceIDService.INTENT_PUSH_REGISTRATION_COMPLETED)
                if (errorMessage != null) {
                    Toast.makeText(
                        this@BaseActivity,
                        "Error push registration:$errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@BaseActivity,
                        "Succeeded push registration",
                        Toast.LENGTH_LONG
                    ).show()
                }*/
            }
        }
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

    /*fun checkPermissionState(permissions: Array<String>): Boolean {
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
    }*/

    fun checkPermissionState(): Boolean {
        /*var isPermissionGranted = true
        for (permission in permissions) {
            Log.d("BaseActivity", "Permission for loop "+permission)
            if (ContextCompat.checkSelfPermission(
                    this@BaseActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionGranted = false
                break
            }
        }
        return isPermissionGranted*/

        val cameraPermission = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.CAMERA
        )
        val readStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val writeStoragePermission = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val mediaImage = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.READ_MEDIA_IMAGES
        )
        val mediaVideo = ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.READ_MEDIA_VIDEO
        )
        Log.d("BaseActivity", "camera Persmission $cameraPermission")
        Log.d("BaseActivity", "read Persmission $readStoragePermission")
        Log.d("BaseActivity", "write Persmission $writeStoragePermission")
        Log.d("BaseActivity", "image Persmission $mediaImage")
        Log.d("BaseActivity", "video Persmission $mediaVideo")
        return ((cameraPermission == PackageManager.PERMISSION_GRANTED && mediaImage == PackageManager.PERMISSION_GRANTED && mediaVideo == PackageManager.PERMISSION_GRANTED )
                || readStoragePermission == PackageManager.PERMISSION_GRANTED && writeStoragePermission == PackageManager.PERMISSION_GRANTED)
    }


    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            permission?.onPermissionGranted()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            finish()
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                val preferences =
                    applicationContext.getSharedPreferences(Constants.MAIN_PREF, MODE_PRIVATE)
                preferences.edit().putString(Constants.FCM_TOKEN, token).apply()
                Log.d("TAG", "Fetching FCM registration token ${token}")
            })
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}