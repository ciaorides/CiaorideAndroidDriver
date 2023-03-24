package com.ciaorides.ciaorides.view.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity.LEFT
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityHomeBinding
import com.ciaorides.ciaorides.model.request.GlobalUserIdRequest
import com.ciaorides.ciaorides.model.response.UserDetailsResponse
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.utils.DataHandler
import com.ciaorides.ciaorides.view.activities.menu.*
import com.ciaorides.ciaorides.view.activities.user.EditProfileActivity
import com.ciaorides.ciaorides.view.adapter.MenuListAdapter
import com.ciaorides.ciaorides.viewmodel.HomeViewModel
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private var profileData: UserDetailsResponse.Response? = null

    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarHome.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_my_rides, R.id.nav_my_wallet, R.id.nav_my_vehicles
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        binding.appBarHome.ivMenu.setOnClickListener {
            drawerLayout.openDrawer(LEFT)
        }
        /* val headerBinding = NavHeaderHomeBinding.bind(navView.getHeaderView(0)) // 0-index header
        headerBinding.imageView.setOnClickListener {

        }*/
        setupMenu()
        handleUserResponse()
        if (!TextUtils.isEmpty(Constants.getValue(this@HomeActivity, Constants.USER_ID))) {
            viewModel.getUserDetails(
                GlobalUserIdRequest(
                    user_id = Constants.getValue(this@HomeActivity, Constants.USER_ID)
                )
            )
        }

        binding.userDetails.tvEditProfile.setOnClickListener {
            binding.drawerLayout.closeDrawers()
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra(Constants.DATA_VALUE, profileData)
            startActivity(intent)
        }
        

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setupMenu() {
        val adapter = MenuListAdapter()
        binding.llMenu.rvMenu.adapter = adapter
        adapter.MenuItemClicked { title ->
            binding.drawerLayout.closeDrawers()
            when (title) {
                Constants.MENU_MY_RIDES -> {
                    val intent = Intent(this, RidesActivity::class.java)
                    startActivity(intent)
                }  Constants.MENU_MY_EARNINGS -> {

                }
                Constants.MENU_MY_VEHICLES -> {
                    val intent = Intent(this, MyVehiclesActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_MY_FAVOURITES -> {
                    val intent = Intent(this, FavActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_BANK_DETAILS -> {
                    val intent = Intent(this, BankDetailsActivity::class.java)
                    startActivity(intent)
                }
                Constants.MENU_INBOX -> {

                }
                Constants.MENU_REFER_FRIEND -> {

                }
                Constants.MENU_PAYMENTS -> {

                }
                Constants.MENU_ABOUT_US -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.ABOUT)
                    intent.putExtra(Constants.TITLE, Constants.MENU_ABOUT_US)
                    startActivity(intent)
                }
                Constants.MENU_TERMS_N_CONDITIONS -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.TERMS_AND_CONDITIONS)
                    intent.putExtra(Constants.TITLE, Constants.MENU_TERMS_N_CONDITIONS)
                    startActivity(intent)
                }
                Constants.MENU_PRIVACY_POLICY -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.PRIVACY_POLICY)
                    intent.putExtra(Constants.TITLE, Constants.MENU_PRIVACY_POLICY)
                    startActivity(intent)
                }
                Constants.MENU_HELP -> {
                    val intent = Intent(this, StaticPagesActivity::class.java)
                    intent.putExtra(Constants.DATA_VALUE, Constants.HELP)
                    intent.putExtra(Constants.TITLE, Constants.MENU_HELP)
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleUserResponse() {
        viewModel.userDetailsResponse.observe(this) { dataHandler ->
            when (dataHandler) {
                is DataHandler.SUCCESS -> {
                    dataHandler.data?.let { data ->
                        if (data.status) {
                            profileData = data.response
                            binding.userDetails.tvName.text = data.response.first_name
                            binding.userDetails.tvNumber.text = data.response.mobile
                        }
                    }
                }
                is DataHandler.ERROR -> {
                    Toast.makeText(applicationContext, dataHandler.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}