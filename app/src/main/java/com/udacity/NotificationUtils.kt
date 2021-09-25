package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat


private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
val ID = "id"
val MESSAGE = "message"
val STATUS = "status"

fun NotificationManager.sendNotification(appContext: Context, messageBody: String, status: String ) {

    val contentIntent = Intent(appContext, DetailActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    contentIntent.putExtra(ID, NOTIFICATION_ID)
    contentIntent.putExtra(MESSAGE, messageBody)
    contentIntent.putExtra(STATUS, status)

    val contentPendingIntent = PendingIntent.getActivity(
        appContext,
        REQUEST_CODE,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        appContext,
        appContext.getString(R.string.notification_channel_id)
    )
    builder.apply {
        setSmallIcon(R.drawable.ic_assistant_black_24dp)
        setContentTitle(appContext.getString(R.string.notification_title))
        setContentText(appContext.getString(R.string.notification_description))
        setAutoCancel(true)
        addAction(
            R.drawable.ic_assistant_black_24dp,
            getActionText(appContext,
                R.string.check_download_status, R.color.colorPrimaryDark),
            contentPendingIntent
        )

    }

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}

private fun getActionText(context: Context, @StringRes stringRes: Int, @ColorRes colorRes: Int): Spannable {
    val spannable: Spannable = SpannableString(context.getText(stringRes))
        spannable.setSpan(
            ForegroundColorSpan(context.getColor(colorRes)), 0, spannable.length, 0
        )
    return spannable
}