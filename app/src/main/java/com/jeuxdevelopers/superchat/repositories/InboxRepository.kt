package com.jeuxdevelopers.superchat.repositories

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jeuxdevelopers.superchat.enums.MessageType
import com.jeuxdevelopers.superchat.utils.Constants
import kotlinx.coroutines.tasks.await
import com.jeuxdevelopers.superchat.adapters.InboxAdapter
import com.jeuxdevelopers.superchat.models.InboxModel


class InboxRepository(currentUserId: String, otherUserId: String) {

    companion object{
        const val TAG = "InboxRepository"
    }

    private val _inboxCollectionRef: CollectionReference = Firebase.firestore.collection(Constants.CHAT_INBOX)
        .document(currentUserId).collection(Constants.INBOX_USERS)

    private val _inboxRef: DocumentReference = Firebase.firestore.collection(Constants.CHAT_INBOX)
        .document(currentUserId).collection(Constants.INBOX_USERS).document(otherUserId)

    private val _otherInboxRef: DocumentReference =
        Firebase.firestore.collection(Constants.CHAT_INBOX)
            .document(otherUserId).collection(Constants.INBOX_USERS).document(currentUserId)


     suspend fun sendToMyInbox(
        message: String,
        messageContentType: MessageType,
        senderId: String,
        senderName: String,
        userImage: String,
        time: Long,
        receiverId: String
    ): Boolean {
        val inboxModel = InboxModel(
            senderId, userImage, messageContentType, message, senderName, receiverId, false, false,
            0, time
        )
        return try {
            _inboxRef.set(inboxModel).await()
            Log.d(TAG, "sendToMyInbox: Done")
            true
        } catch (e: Exception) {
            false
        }

    }

     suspend fun sendToOtherInbox(
        message: String,
        messageContentType: MessageType,
        senderId: String,
        senderName: String,
        userImage: String,
        time: Long,
        receiverId: String
    ): Boolean {
        val inboxModel = InboxModel(
            senderId, userImage, messageContentType, message, senderName, receiverId, false, false,
            0, time
        )
        return try {
            val documentSnapshot = _otherInboxRef.get().await()
            val model: InboxModel? = documentSnapshot.toObject(InboxModel::class.java)
            if (documentSnapshot != null && model != null) {

                try {
                    inboxModel.unReadCount = model.unReadCount + 1
                    _otherInboxRef.set(inboxModel).await()
                    Log.d(TAG, "sendToOtherInbox: Done")
                    true
                } catch (e: Exception) {
                    inboxModel.unReadCount = 1
                    _otherInboxRef.set(inboxModel).await()
                    Log.d(TAG, "sendToOtherInbox: Done")
                    false
                }
            } else {
                inboxModel.unReadCount = 1
                _otherInboxRef.set(inboxModel).await()
                Log.d(TAG, "sendToOtherInbox: Done")
                return false
            }

        } catch (e: Exception) {
            Log.d(TAG, "sendToOtherInbox: Fail")
            false
        }
    }

      fun setReadInInbox() {
            _inboxRef.update("unReadCount", 0)
    }

    // here we implement inbox listener
     fun setInboxRecycler(inboxAdapter:InboxAdapter, inboxList:MutableList<InboxModel>) {
        Log.d(TAG, "setInboxRecycler: ")
        _inboxCollectionRef
            .orderBy(Constants.MESSAGE_TIME, Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Log.d(TAG, "setInboxRecycler: error -> ${error.message}")
                } else {
                    if (querySnapshot!=null && !querySnapshot.isEmpty) {
                        Log.d(
                            TAG,
                            "setInboxRecycler: data -> ${querySnapshot.documents.toString()}"
                        )
                        for (documentChange in querySnapshot.documentChanges) {
                            val oldIndex: Int = documentChange.oldIndex
                            val newIndex: Int = documentChange.newIndex
                            when (documentChange.type) {
                                DocumentChange.Type.ADDED -> {
                                    val newInbox: InboxModel =
                                        documentChange.document
                                            .toObject(InboxModel::class.java)
                                    inboxList.add(newIndex, newInbox)
                                    inboxAdapter.notifyItemInserted(newIndex)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val modifiedInbox: InboxModel =
                                        documentChange.document.toObject(
                                            InboxModel::class.java
                                        )
                                    inboxList[oldIndex] = modifiedInbox
                                    inboxAdapter.notifyItemChanged(newIndex)
                                }
                                else -> {

                                }
                            }
                        }
                    }else{
                        Log.d(TAG, "setInboxRecycler: snapshots are empty")
                    }
                }
            }
    }

}