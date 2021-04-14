package com.example.feedy1

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var notificationManager: NotificationManager? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationIntent = Intent(this, WebViewActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(
            this,
            0 /* Request code */, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //You should use an actual ID instead
        val notificationId = Random().nextInt(60000)
        val bitmap =
            getBitmapfromUrl(remoteMessage.data["https://feedy.ly/public/dist/website/images/logo3.png"])
        val likeIntent = Intent(this, LikeService::class.java)
        likeIntent.putExtra(NOTIFICATION_ID_EXTRA, notificationId)
        likeIntent.putExtra(
            IMAGE_URL_EXTRA,
            remoteMessage.data["https://feedy.ly/public/dist/website/images/logo3.png"]
        )
        val likePendingIntent = PendingIntent.getService(
            this,
            notificationId + 1, likeIntent, PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels()
        }
        val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
            .setLargeIcon(bitmap)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(remoteMessage.data["title"])
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .setSummaryText(remoteMessage.data["message"])
                    .bigPicture(bitmap)
            )/*Notification with Image*/
            .setContentText(remoteMessage.data["message"])
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .addAction(
                R.drawable.feedy,
                getString(R.string.notification_add_to_cart_button), likePendingIntent
            )
            .setContentIntent(pendingIntent)

        notificationManager?.notify(notificationId, notificationBuilder.build())
    }

    fun getBitmapfromUrl(imageUrl: String?): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)

    private fun setupChannels() {
        val adminChannelName: CharSequence = getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = getString(R.string.notifications_admin_channel_description)
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(
            ADMIN_CHANNEL_ID,
            adminChannelName,
            NotificationManager.IMPORTANCE_LOW
        )
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        if (notificationManager != null) {
            notificationManager!!.createNotificationChannel(adminChannel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID_EXTRA = "notificationId"
        private const val IMAGE_URL_EXTRA = "https://feedy.ly/public/dist/website/images/logo3.png"
        private const val ADMIN_CHANNEL_ID = "admin_channel"
    }
}
