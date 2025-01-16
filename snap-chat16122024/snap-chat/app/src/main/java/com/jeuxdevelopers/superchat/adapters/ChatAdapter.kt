package com.jeuxdevelopers.superchat.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jeuxdevelopers.superchat.databinding.ItemChatBinding
import com.jeuxdevelopers.superchat.enums.MessageType
import com.jeuxdevelopers.superchat.models.ChatModel
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChatAdapter(
    val data: MutableList<ChatModel>,
    val context: Context,
   val otherUserProfileImage: String
) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    inner class ChatViewHolder(val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val preferences = MyPreferences(context)
        fun bind(model: ChatModel) {
            if (model.senderId == preferences.getCurrentUser().userId) {
                binding.chatLeftItem.visibility = View.GONE
                binding.chatRightItem.visibility = View.VISIBLE
                //right chat
                setRightChatView(model)
            } else if (model.receiverId == preferences.getCurrentUser().userId) {
                binding.chatLeftItem.visibility = View.VISIBLE
                binding.chatRightItem.visibility = View.GONE

                // left chat
                setLeftChatView(model)
            }
        }

        // implement view of left chat item
        private fun setLeftChatView(model: ChatModel) {
            if (model.messageType == MessageType.TEXT) {
                binding.cardImageLeft.visibility = View.GONE
                binding.tvMessageLeft.visibility = View.VISIBLE

                binding.tvMessageLeft.text = model.message

            } else if (model.messageType == MessageType.IMAGE) {
                binding.cardImageLeft.visibility = View.VISIBLE
                binding.tvMessageLeft.visibility = View.GONE

                Glide.with(context).load(model.message).into(binding.ivLeft)
            }

            binding.tvTimeLeft.text = Utils.getTimeFromMillis(Utils.utcTimeToLocal(model.time))
            Glide.with(context).load(otherUserProfileImage).into(binding.cvProfileLeft)
            // use coroutine to fetch user and display its profile image
//            binding.root.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
//                try {
//                    val userModel = UsersRepository().getUserFromDatabaseById(model.senderId)
//                    Log.d("SenderProfile", "setLeftChatView: Url -> ${userModel.profileUrl}")
//                    withContext(Dispatchers.Main) {
//                        Glide.with(context).load(userModel.profileUrl).into(binding.cvProfileLeft)
//                    }
//                } catch (e: Exception) {
//                    Log.d("SenderProfile", "setLeftChatView: Exception -> ${e.message}")
//                }
//            }

        }

        // implement view of right item
        private fun setRightChatView(model: ChatModel) {
            Log.d("ChatView", "setRightChatView: ")
            if (model.messageType == MessageType.TEXT) {
                binding.cardImageRight.visibility = View.GONE
                binding.tvMessageRight.visibility = View.VISIBLE

                binding.tvMessageRight.text = model.message
                Log.d("ChatView", "setRightChatView: message -> ${model.message}")

            } else if (model.messageType == MessageType.IMAGE) {
                binding.cardImageRight.visibility = View.VISIBLE
                binding.tvMessageRight.visibility = View.GONE

                Glide.with(context).load(model.message).into(binding.ivRight)
            }

            binding.tvTimeRight.text = Utils.getTimeFromMillis(Utils.utcTimeToLocal(model.time))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ItemChatBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}