package com.penpab.firebasepushnotification.utils

import android.app.*
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.penpab.firebasepushnotification.R

object NotificationUtils {

    private const val GROUP_ID = "group"
    private const val GROUP_NAME = "Channels"

    private const val GENERAL_CHANNEL_ID = "com.penpab.firebasepushnotification"
    const val GENERAL_CHANNEL_NAME = "General Notifications"


    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannels(context: Context) {

        // Notification manager instance
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create group
        NotificationChannelGroup(GROUP_ID, GROUP_NAME)
        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(GROUP_ID, GROUP_NAME))

        // Create channels
        val generalChannel = NotificationChannel(
            GENERAL_CHANNEL_ID.plus(GENERAL_CHANNEL_NAME),
            GENERAL_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        generalChannel.group = GROUP_ID
        generalChannel.enableLights(true)
        generalChannel.enableVibration(true)
        generalChannel.lightColor = Color.RED
        generalChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

        // Add channels to notification manager
        notificationManager.createNotificationChannel(generalChannel)
    }

    fun createNotification(
        context: Context,
        channelName: String,
        title: String,
        message: String,
        pendingIntent: PendingIntent? = null
    ): Notification {
        return NotificationCompat.Builder(context, GENERAL_CHANNEL_ID.plus(channelName))
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
    }

}