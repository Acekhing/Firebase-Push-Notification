package com.penpab.firebasepushnotification

import android.content.Context
import android.util.Log
import androidx.work.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.penpab.firebasepushnotification.utils.NotificationUtils.GENERAL_CHANNEL_NAME
import com.penpab.firebasepushnotification.utils.NotificationUtils.createNotification
import com.penpab.firebasepushnotification.utils.WorkerData
import kotlin.random.Random

class SendTokenWork(
    private val context: Context,
    private val workerParams: WorkerParameters
): CoroutineWorker(context, workerParams) {

    companion object{
        const val TAG = "MessagingService"
        fun OneTimeWorkRequest.Builder.setConstraint(): OneTimeWorkRequest.Builder {
            return setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
        }
    }

    override suspend fun doWork(): Result {
        startForegroundService()
        val clientToken = inputData.getString(WorkerData .Token.name) ?: ""
        return sendRegistrationToServer(clientToken)
    }

    private suspend fun startForegroundService(){
        Log.d(TAG, "startForegroundService: called")
        val title = "Sending token"
        val message = "Our App is sending your registration token"
        setForeground(
            ForegroundInfo(
                Random.nextInt(),
                createNotification(context, GENERAL_CHANNEL_NAME, title, message)
            )
        )
    }

    private fun sendRegistrationToServer(token: String): Result {
        Log.d(TAG, "sendRegistrationToServer: called")
        var result: Result = Result.failure()

        if (token.isEmpty()){
            Log.d(TAG, "sendRegistrationToServer: Token empty")
            return result
        }

        val currentUser = Firebase.auth.currentUser

        if (currentUser == null){
            Log.d(TAG, "sendRegistrationToServer: User not registered")
            Firebase.auth.signInAnonymously().addOnSuccessListener {
                Log.d(TAG, "sendRegistrationToServer: registering user")
                result = uploadToken(it.user!!.uid, token)
            }.addOnFailureListener {
                Log.d(TAG, "sendRegistrationToServer: registration failed")
                result = Result.failure(
                    workDataOf(
                        WorkerData.Failure.name to it.message.toString()
                    )
                )
            }
        }else{
            Log.d(TAG, "sendRegistrationToServer: User registered")
            result = uploadToken(currentUser.uid, token)
        }
        return result
    }

    private fun uploadToken(userId: String, token: String): Result {
        Log.d(TAG, "uploadToken: called")
        var result: Result = Result.failure()
        if (token.isEmpty()){
            return result
        }
        Firebase.firestore
            .collection("Tokens")
            .document(userId)
            .set(hashMapOf(
                    "token" to token,
                    "timestamp" to Timestamp.now()
                ))
            .addOnSuccessListener {
                Log.d(TAG, "uploadToken: token uploaded")
                result = Result.success(
                    workDataOf(
                        WorkerData.Success.name to "Upload success"
                    )
                )
            }
            .addOnFailureListener {
                Log.d(TAG, "uploadToken: uploading token failed")
                Log.e(TAG, "uploadToken: Error", it)
                result =  Result.failure(
                    workDataOf(
                        WorkerData.Failure.name to it.message.toString()
                    )
                )
            }
        return result
    }

}