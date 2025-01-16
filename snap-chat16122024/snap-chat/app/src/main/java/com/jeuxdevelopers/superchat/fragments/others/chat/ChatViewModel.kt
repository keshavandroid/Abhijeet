package com.jeuxdevelopers.superchat.fragments.others.chat

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.jeuxdevelopers.superchat.adapters.ChatAdapter
import com.jeuxdevelopers.superchat.enums.MessageType
import com.jeuxdevelopers.superchat.models.ChatModel
import com.jeuxdevelopers.superchat.repositories.BuyRepository
import com.jeuxdevelopers.superchat.repositories.ChatRepository
import com.jeuxdevelopers.superchat.repositories.UsersRepository
import com.jeuxdevelopers.superchat.utils.MyPreferences
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {
    companion object {
        const val TAG = "ChatViewModel"
    }

    // here are the fields of other user costs
    private var _characterCost: Double = 0.0
    private var _imageCost: Double = 0.0

    private var _isPayingUser = false


    // here we will create instance of chat repo
    private lateinit var _chatRepo: ChatRepository

    fun setChatRepository(otherUserId: String, preferences: MyPreferences) {
        _chatRepo = ChatRepository(preferences.getCurrentUser().userId, otherUserId, preferences)
    }

    fun setChatRates(otherUserId: String) {
        viewModelScope.launch {
            try {
                val otherUser = UsersRepository().getUserFromDatabaseById(otherUserId)
                _characterCost = otherUser.pricePerCharacter
                _imageCost = otherUser.pricePerImage
                Log.d(
                    TAG,
                    "setChatRates: set chat Rates successfully, char ->$_characterCost image -> $_imageCost"
                )
            } catch (e: Exception) {
                Log.d(TAG, "setChatRates: error -> ${e.message}")
            }

        }
    }

    // dialog visibility handler
    class WaitingState(var status: Boolean, var message: String)

    private val _waiteState = MutableLiveData(WaitingState(false, ""))
    val isWaiting: LiveData<WaitingState> get() = _waiteState


    // validation of inputs
    class Validation(var status: Boolean, var inputView: TextInputLayout?, var message: String)

    private val _validated = MutableLiveData(Validation(false, null, "Empty"))
    val isValidated: LiveData<Validation> get() = _validated

    fun validate(
        message: String
    ) {
        when {
            message.isEmpty() -> {
                _validated.postValue(
                    Validation(
                        false,
                        null,
                        "Please type some message"
                    )
                )
            }
            else -> {
                _validated.postValue(
                    Validation(
                        true,
                        null,
                        message
                    )
                )
            }
        }
    }


    // send chat message
    private val _messageSent = MutableLiveData(false)
    val isMessageSent: LiveData<Boolean> get() = _messageSent
    fun sendMessage(
        message: String,
        messageType: MessageType,
        otherUserId: String,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                if (_isPayingUser) {
                    val able = checkMessageAbleToSend(message, messageType, context)
                    if (able) {
                        // perform debit credit
                        val result =
                            detectCreditAndAddBalance(message, messageType, otherUserId, context)
                        // if the result will be success
                        if (result) {
                            val sent = _chatRepo.sendChatMessage(message, messageType)
                            _messageSent.postValue(sent)
                        } else {
                            // otherwise no message send in case of debit credit failure
                            _messageSent.postValue(false)
                        }
                    } else {
                        // not enough credit to communicate
                        _messageSent.postValue(false)
                    }
                } else {
                    // not a paying user no need for debit credit
                    val sent = _chatRepo.sendChatMessage(message, messageType)
                    _messageSent.postValue(sent)
                }
            } catch (e: Exception) {
                _messageSent.postValue(false)
            }

        }
    }


    // upload chat image
    // add user profile to bucket
    private val _addImage = MutableLiveData("dum")
    val addImage: LiveData<String> get() = _addImage
    fun addChatImage(localUri: Uri, messageType: MessageType) {
        viewModelScope.launch {
            try {
                _waiteState.postValue(WaitingState(true, "Uploading image"))
                val imageUrl = _chatRepo.addImageMessageToBucket(localUri, messageType)
                _addImage.postValue(imageUrl)
                _waiteState.postValue(WaitingState(false, ""))
            } catch (e: Exception) {
                _addImage.postValue("")
                _waiteState.postValue(WaitingState(false, ""))
            }
        }
    }

    // implement chat listener
    fun setChatRecyclerListener(
        adapter: ChatAdapter,
        list: MutableList<ChatModel>,
        chatRecycler: RecyclerView,
        requireContext: Context,
        requireActivity: FragmentActivity,
        otherUserId: String
    ) {
        _chatRepo.setOnPayingListener(object : ChatRepository.PayingUserListener {
            override fun isPaying(boolean: Boolean) {
                _isPayingUser = boolean
                Log.d(TAG, "isPaying: $boolean")
            }

        })
        _chatRepo.setChatListener(adapter, list, chatRecycler, requireContext, requireActivity,otherUserId)
    }


    // check and detect from other user
    private val _messageAbleSend = MutableLiveData(true)
    val isAbleToSend: LiveData<Boolean> get() = _messageAbleSend
    private fun checkMessageAbleToSend(
        message: String,
        messageType: MessageType,
        context: Context
    ): Boolean {
        val preferences = MyPreferences(context)
        val cost = getCalculatedCost(message, messageType)
        return if (preferences.getCurrentUser().credit < cost) {
            _messageAbleSend.postValue(false)
            false
        } else {
            _messageAbleSend.postValue(true)
            true
        }
    }

    private fun getCalculatedCost(message: String, messageType: MessageType): Double {
        return when (messageType) {
            MessageType.IMAGE -> {
                _imageCost
            }
            MessageType.TEXT -> {
                (_characterCost * message.length)
            }
            else -> 0.0
        }
    }

    private suspend fun detectCreditAndAddBalance(
        message: String,
        messageType: MessageType,
        otherUserId: String,
        context: Context
    ): Boolean {
        val cost = getCalculatedCost(message, messageType)
        Log.d(TAG, "detectCreditAndAddBalance: Cost -> $cost")
        return if (cost > 0.0) {
            val result = BuyRepository(context).deductFromCreditCurrentUser(cost)
            if (result) {
                BuyRepository(context).addBalanceToUser(cost, otherUserId)
            } else false
        } else {
            Log.d(TAG, "detectCreditAndAddBalance: Cost is 0")
            false
        }
    }
}