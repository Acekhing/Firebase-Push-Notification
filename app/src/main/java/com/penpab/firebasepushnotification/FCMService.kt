package com.penpab.firebasepushnotification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.penpab.firebasepushnotification.SendTokenWork.Companion.setConstraint
import com.penpab.firebasepushnotification.utils.NotificationUtils
import com.penpab.firebasepushnotification.utils.NotificationUtils.GENERAL_CHANNEL_NAME
import com.penpab.firebasepushnotification.utils.WorkerData
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    companion object {
        const val TAG = "MessagingService"
        const val WORK_NAME = "send_token"
    }

    private lateinit var sendTokenWork: OneTimeWorkRequest.Builder
    private lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: service created")
        sendTokenWork = OneTimeWorkRequest.Builder(SendTokenWork::class.java)
        workManager = WorkManager.getInstance(applicationContext)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val data = Data.Builder()
        data.putString(WorkerData.Token.name, token)

        sendTokenWork.setConstraint().setInputData(data.build())

        workManager.beginUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            sendTokenWork.build()
        ).enqueue()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, DetailActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)

        val notification = NotificationUtils.createNotification(
            context = this,
            channelName = GENERAL_CHANNEL_NAME,
            title = message.data["title"]!!,
            message = message.data["message"]!!,
            pendingIntent = pendingIntent
        )

        notificationManager.notify(notificationId, notification)

    }

}









