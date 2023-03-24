package com.ciaorides.ciaorides.view.activities.chat

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ciaorides.ciaorides.R
import com.ciaorides.ciaorides.databinding.ActivityChatViewBinding
import com.ciaorides.ciaorides.model.response.Message
import com.ciaorides.ciaorides.view.activities.BaseActivity
import com.ciaorides.ciaorides.view.adapter.MessageAdapter
import javax.inject.Inject

class ChatViewActivity : BaseActivity<ActivityChatViewBinding>() {

    @Inject
    lateinit var messageAdapter : MessageAdapter

    val messageList = ArrayList<Message>()

    override fun init() {
        binding.toolbar.tvHeader.text = getString(R.string.chat)

        binding.recyclerView.apply {
            messageAdapter = MessageAdapter(messageList)
            adapter= messageAdapter
            layoutManager = LinearLayoutManager(this@ChatViewActivity)
        }
       binding.button.setOnClickListener{
           if (binding.editText.text.toString().isNullOrBlank()) {
               Toast.makeText(this,"Please enter Message",Toast.LENGTH_SHORT).show()
           }else{
               messageList.add(Message(binding.editText.text.toString(),"you"))
               binding.editText.text?.clear()
               messageAdapter.notifyDataSetChanged()
           }
       }
    }

    override fun getViewBinding(): ActivityChatViewBinding = ActivityChatViewBinding.inflate(layoutInflater)
}