package com.penpab.firebasepushnotification

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService

class MessagingService: FirebaseMessagingService() {

    companion object{
        const val TAG = "MessagingService"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")

        // [Use work manager]
        sendRegistrationToServer(token);
    }

    private fun sendRegistrationToServer(token: String) {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null){
            Firebase.auth.signInAnonymously().addOnSuccessListener {
                Log.d(TAG, "sendRegistrationToServer: Sign in success")
                uploadToken(it.user!!.uid, token)
            }.addOnFailureListener {
                Log.e(TAG, "sendRegistrationToServer: Error", it)
            }
        }else{
            uploadToken(currentUser.uid, token)
        }
    }

    private fun uploadToken(userId: String, token: String){
        Firebase.firestore
            .collection("Tokens")
            .document(userId.toString())
            .set("token" to token)
            .addOnSuccessListener {
                Log.d(TAG, "uploadToken: Upload success")
            }
            .addOnFailureListener {
                Log.e(TAG, "uploadToken: Error", it)
            }
    }
}









