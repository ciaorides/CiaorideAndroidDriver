package com.ciaorides.ciaorides.view.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.FragmentUsersListBinding
import com.ciaorides.ciaorides.utils.Constants.USER_DATA
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.adapter.UsersListAdapter
import com.ciaorides.ciaorides.viewmodel.UserDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UsersListFragment : Fragment(R.layout.fragment_users_list) {
    private val viewModel: UserDetailsViewModel by viewModels()

    @Inject
    lateinit var usersListAdapter: UsersListAdapter

    private lateinit var binding: FragmentUsersListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUsersListBinding.bind(view)
        init()

        viewModel.userDetailsList.observe(viewLifecycleOwner) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    dataHandler.data?.let { usersList ->
                        if (usersList.isEmpty()) {
                            Toast.makeText(
                                activity,
                                getString(R.string.user_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            usersListAdapter.differ.submitList(usersList)
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(activity, dataHandler.message, Toast.LENGTH_SHORT).show()
                }
                is DataHandler.LOADING -> binding.progressBar.visibility = View.VISIBLE
            }

        }
        viewModel.getUserList()
    }

    private fun init() {
        binding.recyclerView.apply {
            adapter = usersListAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        usersListAdapter.onArticleClicked {
            val bundle = Bundle().apply {
                putParcelable(USER_DATA, it)

            }
            findNavController().navigate(
                R.id.action_userListFragment_to_userDetailsFragment,
                bundle
            )
        }

    }
}