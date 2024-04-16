package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.utils.Constants

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
            if (Constants.getValue(this@SplashActivity, Constants.USER_ID).isEmpty()) {
                startActivity(Intent(this@SplashActivity, LandingActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
            }
            finish()
        }
        updateHandler.postDelayed(runnable, 2000)

    }
}