package com.jeuxdevelopers.superchat.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jeuxdevelopers.superchat.adapters.UsersAdapter
import com.jeuxdevelopers.superchat.enums.UserState
import com.jeuxdevelopers.superchat.models.PaymentMethod
import com.jeuxdevelopers.superchat.models.UserModel
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.MyPreferences
import kotlinx.coroutines.tasks.await
import kotlin.Exception

class UsersRepository() {
    companion object {
        const val TAG = "UsersRepository"
    }

    private val _usersCollection = Firebase.firestore.collection(Constants.USERS_COLLECTION)
    private val _usersImagesStore = Firebase.storage.reference.child(Constants.PROFILE_IMAGES)

    private val _connectedRef = Firebase.database.getReference(".info/connected")
    private var connectionListener: ValueEventListener? = null

    suspend fun addNewUserToDatabase(userModel: UserModel): Boolean {
        return try {
            _usersCollection.document(userModel.userId).set(userModel).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserFromDatabaseById(userId: String): UserModel {
        return try {
            val documentSnapshot = _usersCollection.document(userId).get().await()
            Log.e("ServerResponse=", documentSnapshot.toString())
            documentSnapshot.toObject<UserModel>()!!


        } catch (e: Exception) {
            UserModel()
        }
    }

    suspend fun addUserProfileImage(localUri: Uri): String {
        val storage = _usersImagesStore.child(localUri.lastPathSegment.toString())
        return try {
            storage.putFile(localUri).await()
            val downloadUri = storage.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            ""
        }

    }


    suspend fun addPaymentMethodToUser(currentUserId: String, paymentMethod: PaymentMethod)
            : Boolean {
        return try {
            _usersCollection.document(currentUserId)
                .update(Constants.USER_PAYMENT_METHOD, paymentMethod).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    fun setFirebaseUsersListener(adapter: UsersAdapter, list: MutableList<UserModel>) {
        _usersCollection
            .whereNotEqualTo(Constants.USER_ID, Firebase.auth.currentUser?.uid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    // Log.d(TAG, "setPostsRecyclerListener: Error => ${e.localizedMessage}")
                    // Utils.showShortToast(requireContext(), "Error => ${e.localizedMessage}")
                } else {
                    if (snapshots != null) {
                        for (documentChange: DocumentChange in snapshots.documentChanges) {
                            val oldIndex = documentChange.oldIndex
                            val newIndex = documentChange.newIndex
                            when (documentChange.type) {
                                DocumentChange.Type.ADDED -> {
                                    Log.e("agevalue=",documentChange.document.toString())
                                    val model: UserModel = documentChange.document.toObject(UserModel::class.java)
                                    list.add(newIndex, model)
                                    adapter.notifyItemInserted(newIndex)
                                }
                                DocumentChange.Type.MODIFIED -> {
                                    val model: UserModel =
                                        documentChange.document.toObject(UserModel::class.java)
                                    list[oldIndex] = model
                                    adapter.notifyItemChanged(oldIndex)
                                }
                                DocumentChange.Type.REMOVED -> {
                                    //val model: UserModel =
                                    //     documentChange.document.toObject(UserModel::class.java)
                                    // list[oldIndex] = model
                                    list.removeAt(oldIndex)
                                    adapter.notifyItemRemoved(oldIndex)
                                }
                                else -> {
                                    // Log.d(TAG, "Document Change => ${documentChange.type.name}")
                                }
                            }
                        }
                    }
                }
            }

    }


    fun setConnectionListener(currentUserId: String, context: Context) {
        connectionListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                Log.d(TAG, "onDataChange: connection ->$connected")
                updateUserConnectionState(connected, context, currentUserId)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: error -> ${error.message}")
            }

        }
        _connectedRef.addValueEventListener(connectionListener!!)
    }

    fun removeConnectionListener() {
        if (connectionListener != null)
            _connectedRef.removeEventListener(connectionListener!!)
    }


    fun updateUserConnectionState(connected: Boolean, context: Context, userId: String) {
        val state: UserState = if (connected) {
            UserState.ONLINE
        } else {
            UserState.OFFLINE
        }
        _usersCollection.document(userId).update(Constants.USER_CONNECTION_STATE, state)
            .addOnSuccessListener {
                val userModel = MyPreferences(context).getCurrentUser()
                userModel.userState = state
                MyPreferences(context).saveCurrentUser(userModel)
            }
    }

}