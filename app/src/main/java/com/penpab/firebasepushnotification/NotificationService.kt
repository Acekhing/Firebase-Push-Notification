package com.penpab.firebasepushnotification

import com.penpab.firebasepushnotification.utils.Constants.BASE_URL
import com.penpab.firebasepushnotification.utils.Constants.CONTENT_TYPE
import com.penpab.firebasepushnotification.utils.Constants.SERVERKEY
import com.penpab.firebasepushnotification.models.PushNotification
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationService {

    @Headers("Authorization: key=$SERVERKEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun sendNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>

    companion object{
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NotificationService::class.java)
        }
    }

    suspend fun subscribeToTopic(topic: String): Unit

    suspend fun unSubscribeFromTopic(topic: String): Unit

}