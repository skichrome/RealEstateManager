package com.skichrome.realestatemanager.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.skichrome.realestatemanager.R

class NotificationHelper(context: Context?) : ContextWrapper(context)
{
    // =================================
    //              Fields
    // =================================

    private var notificationManager: NotificationManagerCompat? = null
    private var notificationChannel: NotificationChannel? = null

    init
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_name),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel?.apply {
                description = getString(R.string.notification_description)
                lightColor = Color.BLUE
                setShowBadge(true)
                getNotificationManager().createNotificationChannel(this)
            }
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun getNotificationManager(): NotificationManagerCompat
    {
        notificationManager?.let {
            return it
        }
        return NotificationManagerCompat.from(applicationContext)
    }

    private fun getIntent(): PendingIntent
    {
        val intent = Intent(applicationContext, MainActivity::class.java)
        return PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun getNotificationBuilder(title: String, content: String, intent: PendingIntent = getIntent()): NotificationCompat.Builder =
        NotificationCompat.Builder(applicationContext, getString(R.string.notification_channel_name))
            .setContentTitle(title)
            .setContentIntent(intent)
            .setSmallIcon(R.drawable.real_estate_logo)
            .setContentText(content)
            .setAutoCancel(true)

    fun notify(id: Int, builder: NotificationCompat.Builder) =
        getNotificationManager().notify(id, builder.build())
}