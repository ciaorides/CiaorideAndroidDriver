package com.ciaorides.ciaorides.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentUsersDetailsBinding

class UserDetailsFragment : Fragment(R.layout.fragment_users_details) {
    private lateinit var binding: FragmentUsersDetailsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersDetailsBinding.bind(view)


    }
}