package com.ciaorides.ciaorides.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ciaorides.ciaorides.databinding.ActivityTestBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class TestActivity : AppCompatActivity() {
    var behavior: BottomSheetBehavior<*>?=null

    lateinit var binding: ActivityTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

}