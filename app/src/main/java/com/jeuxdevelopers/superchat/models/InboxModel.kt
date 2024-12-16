package com.jeuxdevelopers.superchat.models

import com.jeuxdevelopers.superchat.enums.MessageType
import java.io.Serializable

data class InboxModel(
    var senderId: String = "",
    var userImage: String = "",
    var messageType: MessageType = MessageType.TEXT,
    var message: String = "",
    var senderName: String = "",
    var receiverId: String = "",
    var isDeleted: Boolean = false,
    var isRead: Boolean = false,
    var unReadCount: Int = 0,
    var time: Long = 0
) : Serializable