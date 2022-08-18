package com.penpab.firebasepushnotification.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)
