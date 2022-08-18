package com.penpab.firebasepushnotification

import android.app.Application
import android.os.Build
import android.util.Log
import com.penpab.firebasepushnotification.utils.NotificationUtils.createChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class App: Application(), NotificationService by NotificationServiceImpl() {

    companion object{
        const val TAG = "MessagingService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: App Instanced created")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels(this)
        }
        CoroutineScope(Dispatchers.IO).launch { subscribeToTopic("News") }
    }
}


