package pwr.edu.ilab

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class CreateNotification(
    var context: Context,
    var title: String,
    var msg: String,
    val channelId: String = "Firebase messaging ID",
    val channelName: String = "Firebase messaging"
) {
    val notificationManager =
        context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: NotificationCompat.Builder

    fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntentFlag =
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) 0 else PendingIntent.FLAG_IMMUTABLE
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlag)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.immunization)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(100, notification)
    }
}
