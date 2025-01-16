package com.jeuxdevelopers.superchat.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.jeuxdevelopers.superchat.adapters.ChatAdapter
import com.jeuxdevelopers.superchat.dialogs.RateBottomDialog
import com.jeuxdevelopers.superchat.enums.MessageType
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.models.ChatModel
import com.jeuxdevelopers.superchat.models.RateModel
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ChatRepository
    (
    private val currentUserId: String,
    private val otherUserId: String,
    private val preferences: MyPreferences
) {
    companion object {
        const val TAG = "ChatRepository"
    }

    private var listener: PayingUserListener? = null

    private val _chatRef: CollectionReference =
        Firebase.firestore.collection(Constants.CHAT_COLLECTION)
            .document(getChannelID()).collection(Constants.MESSAGES_COLLECTION)


    private val _chatStoreRef: StorageReference =
        Firebase.storage.reference.child(Constants.CHAT_STORAGE)


    suspend fun sendChatMessage(message: String, messageType: MessageType): Boolean {
        val id = _chatRef.document().id
        val seen = HashMap<String, Long>()
        seen[currentUserId] = Utils.localTimeToUtc(System.currentTimeMillis())
        val chatModel = ChatModel(
            message, id, messageType, seen, currentUserId,
            otherUserId, false, Utils.localTimeToUtc(System.currentTimeMillis()), 0
        )
        return try {
            _chatRef.document(id).set(chatModel).await()
            val otherUserModel = UsersRepository().getUserFromDatabaseById(otherUserId)
            InboxRepository(currentUserId, otherUserId)
                .sendToMyInbox(
                    message,
                    messageType,
                    preferences.getCurrentUser().userId,
                    otherUserModel.name,
                    otherUserModel.profileUrl,
                    chatModel.time,
                    otherUserModel.userId
                )
            InboxRepository(currentUserId, otherUserId)
                .sendToOtherInbox(
                    message,
                    messageType,
                    preferences.getCurrentUser().userId,
                    preferences.getCurrentUser().name,
                    preferences.getCurrentUser().profileUrl,
                    chatModel.time,
                    otherUserModel.userId
                )
            _chatRef.document(id).update(Constants.MESSAGE_SENT, true).await()
            true
        } catch (e: Exception) {
            false
        }


    }


    private fun setMessageAsSeen(id: String) {
        _chatRef.document(id)
            .update("seen.$otherUserId", Utils.localTimeToUtc(System.currentTimeMillis()))
    }

    private fun getChannelID(): String {
        val channelId = StringBuilder()
        val mCurrentId: String = preferences.getCurrentUser().userId
        val list: List<String> = listOf(otherUserId, mCurrentId)
        Collections.sort(list)
        //Arrays.sort(list,String.CASE_INSENSITIVE_ORDER)
        for (id in list) {
            channelId.append(id)
        }
        Log.d(TAG, "getChannelID: =>  $channelId")
        return channelId.toString()
    }


    // add images to storage
    suspend fun addImageMessageToBucket(localUri: Uri, messageType: MessageType): String {
        val dateTimeFormat = SimpleDateFormat("dd-mm-yy-hh-mm-ss", Locale.getDefault())
        val storage = _chatStoreRef.child(getChannelID()).child(
            preferences.getCurrentUser()
                .name + "/" + dateTimeFormat.format(System.currentTimeMillis()) + "/" + messageType.name
        )
        return try {
            storage.putFile(localUri).await()
            val downloadUri = storage.downloadUrl.await()
            downloadUri.toString()
        } catch (e: java.lang.Exception) {
            ""
        }

    }


    //TODO: here we implement chat listener

    fun setChatListener(
        adapter: ChatAdapter,
        chatList: MutableList<ChatModel>,
        recyclerChat: RecyclerView,
        requireContext: Context,
        requireActivity: FragmentActivity,
        otherUserId: String,
    ) {
        Log.d(TAG, "setChatListener: ")
        _chatRef
            .orderBy(Constants.MESSAGE_SERVER_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.d(TAG, "setChatListener: Error -> ${error.message}")
                } else {
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        //  Log.d(TAG, "setChatListener: ${querySnapshot.documents.toString()}")
                        // check with first message either user is paying or not
                        val chatModel = querySnapshot.documents[0].toObject(ChatModel::class.java)
                        if (chatModel!!.senderId == preferences.getCurrentUser().userId) {
                            if (listener != null) {
                                listener!!.isPaying(true)
                            }
                        } else {
                            if (listener != null) {
                                listener!!.isPaying(false)
                            }
                        }
                        for (documentChange in querySnapshot.documentChanges) {
                            val oldIndex: Int = documentChange.oldIndex
                            val newIndex: Int = documentChange.newIndex
                            when (documentChange.type) {
                                DocumentChange.Type.ADDED -> {
                                    val model: ChatModel =
                                        documentChange.document.toObject(ChatModel::class.java)
                                    chatList.add(newIndex, model)
                                    adapter.notifyItemInserted(newIndex)
                                    if (newIndex - 1 > 0) {
                                        adapter.notifyItemChanged(newIndex - 1)
                                    }
                                    recyclerChat.scrollToPosition(chatList.size - 1)
                                    setMessageAsSeen(model.messageId)
                                    InboxRepository(
                                        currentUserId,
                                        this.otherUserId
                                    ).setReadInInbox()
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val modifiedChat: ChatModel =
                                        documentChange.document.toObject(ChatModel::class.java)
                                    chatList[oldIndex] = modifiedChat
                                    adapter.notifyItemChanged(newIndex)
                                }
                                else -> {
                                    if (listener != null) {
                                        listener!!.isPaying(true)
                                    }
                                }
                            }
                        }
                    } else {
                        val bottomDialog = RateBottomDialog(requireContext, otherUserId)
                        bottomDialog.setOnRatesListener(object :
                            RateBottomDialog.RatesListener {
                            override fun onAgreeClick() {
                                Utils.showShortToast(
                                    requireContext,
                                    "You are agree, All charges will apply"
                                )
                                if (listener != null) {
                                    listener!!.isPaying(true)
                                }
                            }

                            override fun onCloseClick() {
                                requireActivity.onBackPressed()
                            }

                            override fun onBuyBalanceClick() {
                                val listener = requireContext as MainNavigationListener
                                listener.fromChatFragmentToBuyCreditFragment()
                            }

                        })
                        bottomDialog.show()
                        Log.d(TAG, "setChatListener: snapshot is empty")
                    }
                }
            }
    }


    fun setOnPayingListener(listener: PayingUserListener) {
        this.listener = listener
    }

    interface PayingUserListener {
        fun isPaying(boolean: Boolean)
    }

}