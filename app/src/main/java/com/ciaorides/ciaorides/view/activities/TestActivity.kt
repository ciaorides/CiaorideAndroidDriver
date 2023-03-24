package com.ciaorides.ciaorides.view.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityTestBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback


class TestActivity : AppCompatActivity() {
    var behavior: BottomSheetBehavior<*>?=null

    lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBottomSheet()
        binding.layoutsearch.btnVerify.setOnClickListener {
            behavior?.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private fun showBottomSheet(){
        behavior = BottomSheetBehavior.from(binding.sheet.bottomSheetLayout)
        behavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // React to state change
                Log.e("onStateChanged", "onStateChanged:$newState")
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.layoutsearch.login.setPadding(0, 0, 0, binding.sheet.bottomSheetLayout.height+100)
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.layoutsearch.login.setPadding(0, 0, 0, 0)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })

          behavior?.peekHeight = 0
    }
}