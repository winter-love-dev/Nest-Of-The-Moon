//package com.example.nest_of_the_moon.Service
//
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.os.IBinder
//import android.util.Log.e
//import androidx.core.app.NotificationCompat
//import com.example.nest_of_the_moon.Client.Activity_Client_Home
//import com.example.nest_of_the_moon.R
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//
//class MyFirebaseMessagingService: FirebaseMessagingService()
//{
//    //    override fun onBind(intent: Intent): IBinder
//    //    {
//    //        TODO("Return the communication channel to the service.")
//    //    }
//
//    private val TAG = "FirebaseService"
//
//    /**
//     * FirebaseInstanceIdService is deprecated.
//     * this is new on firebase-messaging:17.1.0
//     */
//    override fun onNewToken(token: String?)
//    {
//        e(TAG, "new Token: $token")
//    }
//
//    /**
//     * this method will be triggered every time there is new FCM Message.
//     */
//    override fun onMessageReceived(remoteMessage: RemoteMessage)
//    {
//        e(TAG, "From: " + remoteMessage.from)
//
//        if (remoteMessage.notification != null)
//        {
//            e(TAG, "Notification Message Body: ${remoteMessage.notification?.body}")
//            sendNotification(remoteMessage.notification?.body)
//        }
//    }
//
//    private fun sendNotification(body: String?)
//    {
//        val intent = Intent(this, Activity_Client_Home::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            putExtra("Notification", body)
//        }
//
//        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//        var notificationBuilder = NotificationCompat.Builder(this, "Notification").setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle("Push Notification FCM").setContentText(body).setAutoCancel(true)
//            .setSound(notificationSound).setContentIntent(pendingIntent)
//
//        var notificationManager: NotificationManager =
//            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.notify(0, notificationBuilder.build())
//    }
//}
