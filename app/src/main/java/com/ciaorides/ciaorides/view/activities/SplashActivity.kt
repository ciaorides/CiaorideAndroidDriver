package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.menu.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        val updateHandler = Handler(Looper.getMainLooper())
        val runnable = Runnable {

//            if (Constants.getValue(this@SplashActivity, Constants.USER_ID).isEmpty()) {
//                startActivity(Intent(this@SplashActivity, LandingActivity::class.java))
//            } else {
//                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
//            }
            //startActivity(Intent(this@SplashActivity, PaymentsActivity::class.java))
            //startActivity(Intent(this@SplashActivity, RidesActivity::class.java))
            //startActivity(Intent(this@SplashActivity, RideDetailsActivity::class.java))
            //startActivity(Intent(this@SplashActivity, EarningsActivity::class.java))
            // startActivity(Intent(this@SplashActivity, PendingPaymentsActivity::class.java))
            // startActivity(Intent(this@SplashActivity, CompletedPaymentsActivity::class.java))
            //startActivity(Intent(this@SplashActivity, SettingsActivity::class.java))
            // startActivity(Intent(this@SplashActivity, ChangePasswordActivity::class.java))
            //  startActivity(Intent(this@SplashActivity, EmergencyContact::class.java))
            //startActivity(Intent(this@SplashActivity, InboxActivity::class.java))
            startActivity(Intent(this@SplashActivity, ReferActivity::class.java))

            finish()
        }
        updateHandler.postDelayed(runnable, 2000)

    }
}