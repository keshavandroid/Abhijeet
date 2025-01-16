package com.jeuxdevelopers.superchat.models

import com.jeuxdevelopers.superchat.enums.MessageType
import java.io.Serializable


data class ChatModel(
     var message: String = "",
     var messageId: String = "",
     var messageType: MessageType = MessageType.TEXT,
     var seen: Map<String, Long> = HashMap(),
     var senderId: String = "",
     var receiverId: String = "",
     var sent: Boolean = false,
     var time: Long = 0,
     var duration: Long = 0
) : Serializable
