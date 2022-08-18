package com.penpab.firebasepushnotification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.penpab.firebasepushnotification.models.PushNotification
import com.squareup.okhttp.ResponseBody
import retrofit2.Response

class NotificationServiceImpl : NotificationService {

    companion object{
        const val TAG = "MessagingService"
    }

    override suspend fun sendNotification(notification: PushNotification): Response<ResponseBody> {
        return NotificationService.instance.sendNotification(notification = notification)
    }

    override suspend fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnSuccessListener {
                Log.d(TAG, "subscribeToTopic: subscribed")
            }
            .addOnFailureListener {
                Log.e(TAG, "subscribeToTopic: Error subscribing to topic", it)
                it.printStackTrace()
            }
    }

    override suspend fun unSubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnSuccessListener {
                Log.d(TAG, "unSubscribeFromTopic: unsubscribed")
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }
}