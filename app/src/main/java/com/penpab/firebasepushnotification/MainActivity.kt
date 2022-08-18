package com.penpab.firebasepushnotification

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.penpab.firebasepushnotification.models.NotificationData
import com.penpab.firebasepushnotification.models.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),
    DialogInterface.OnClickListener,
    NotificationService by NotificationServiceImpl() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var btnAll: MaterialButton
    private var currentUserToken: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Inform user that app will not show notifications.
    }

    @RequiresApi(33)
    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {

        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            val dialog = MaterialAlertDialogBuilder(this).apply {
                setTitle("Notification Permission")
                setMessage("Permission is required to show notifications...")
                setPositiveButton("Allow", this@MainActivity)
                setNegativeButton("", this@MainActivity)
            }
            dialog.show()
        } else {
            // Directly ask for the permission
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askNotificationPermission()
        btnAll = findViewById(R.id.btn_all)
        retrieveUserToken()

        btnAll.setOnClickListener {
            val title = findViewById<TextInputLayout>(R.id.tvtitle).editText?.text.toString()
            val message = findViewById<TextInputLayout>(R.id.tv_body).editText?.text.toString()

            if (title.isNotEmpty() && message.isNotEmpty()
                && !currentUserToken.isNullOrEmpty()
            ) {
                sendPushNotification(
                    PushNotification(
                        data = NotificationData(title, message),
                        to = currentUserToken!!
                    )
                )
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun retrieveUserToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                currentUserToken = it
                findViewById<TextInputLayout>(R.id.tv_token).editText?.setText(it)
            }
    }

    private fun sendPushNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = sendNotification(notification)
                if (!response.isSuccessful) {
                    Log.d(TAG, response.errorBody().toString())
                } else {
                    Log.d(TAG, "Response: ${Gson().toJson(response)}")
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }

    @RequiresApi(33)
    override fun onClick(p0: DialogInterface?, p1: Int) {
        if (p1 == -1) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}