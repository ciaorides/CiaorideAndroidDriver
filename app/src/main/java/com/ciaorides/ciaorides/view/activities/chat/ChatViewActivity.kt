package com.ciaorides.ciaorides.view.activities.chat

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityChatViewBinding
import com.ciaorides.ciaorides.model.response.BookRideResponse
import com.ciaorides.ciaorides.model.response.Message
import com.ciaorides.ciaorides.utils.Constants
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatViewActivity : BaseActivity<ActivityChatViewBinding>() {
    private var chatToken = ""
    private lateinit var chatAdapter: ChatAdapter
    override fun init() {

        val userId = intent.getStringExtra(Constants.USER_ID)
        chatToken = userId + "-" + Constants.getValue(applicationContext, Constants.USER_ID)
        val messagesRef = Firebase.database.reference.child("Chats").child(chatToken)
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()

        chatAdapter = ChatAdapter(options)
        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                with(binding) { recyclerView.scrollToPosition(chatAdapter.itemCount - 1) }
            }
        })
        binding.recyclerView.adapter = chatAdapter

        binding.toolbar.tvHeader.text = getString(R.string.chat)
        val dataResponse =
            intent.getParcelableExtra(Constants.DATA_VALUE) as? BookRideResponse
        dataResponse?.response?.get(0)?.let { response ->
            /* binding.tvVehicleNumber.text = response.number_plate
             binding.tvName.text = response.first_name
             binding.tvVehicleType.text = response.vehicle_make + " " + response.vehicle_model
             binding.tvRating.text = response.r_ratings*/
            binding.cardCall.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + response.mobile)
                startActivity(callIntent)
            }
            /* Constants.showGlide(
                 applicationContext,
                // response.profile_pic,
                 binding.profileImage,
                 binding.progress
             )*/
        }
        binding.toolbar.ivMenu.setOnClickListener {
            onBackPressed()
        }



        binding.button.setOnClickListener {
            if (binding.editText.text.toString().isNullOrBlank()) {
                Toast.makeText(this, "Please enter Message", Toast.LENGTH_SHORT).show()
            } else {
                val database = Firebase.database.reference
                val friendlyMessage = Message(binding.editText.text.toString(), "12345", Date())
                database.child("Chats").child(chatToken).push().setValue(friendlyMessage)
                binding.editText.text?.clear()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chatAdapter.startListening();
    }

    override fun onStop() {
        super.onStop()
        chatAdapter.stopListening();
    }

    override fun getViewBinding(): ActivityChatViewBinding =
        ActivityChatViewBinding.inflate(layoutInflater)

}