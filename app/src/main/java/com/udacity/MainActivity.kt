package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var selectedURL = ""
    private var selectedRepository = ""
    lateinit var downloadManager: DownloadManager
    private var downloadStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        Toast.makeText(this, getString(R.string.please_select_file_to_download), Toast.LENGTH_LONG ).show()

        custom_button.setOnClickListener {
            if (selectedURL.isNotEmpty()) {
                download(selectedURL)
                performClick()
            }
        }
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private fun performClick() {
        custom_button.performButtonClick()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW)

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download repository"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(downloadId)
                    val cursor: Cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Log.i("MainActivity", "Download Success")
                                downloadStatus = getString(R.string.success)
                            }
                            DownloadManager.STATUS_FAILED -> {
                                Log.i("MainActivity", "Download Failed")
                                downloadStatus = getString(R.string.failed)
                            } else -> {
                                Log.i("MainActivity", "Download Failed")
                                downloadStatus = getString(R.string.failed)
                            }
                        }
                    }
                }
                custom_button.hasCompletedDownload()
                sendNotification()
            }
        }
    }

    fun sendNotification() {
        notificationManager.sendNotification(applicationContext, selectedRepository, downloadStatus)
    }

    private fun download(url: String) {
        notificationManager.cancelNotifications()
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalFilesDir(this,
                    Environment.DIRECTORY_DOWNLOADS,"repository.zip")

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val LOAD_APP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when(view.id) {
                R.id.button_glide -> if (checked) {
                    selectedURL = GLIDE_URL
                    selectedRepository = getString(R.string.glide_image_loading_library_by_bumptech)
                }
                R.id.button_app_load -> if (checked) {
                    selectedURL = LOAD_APP_URL
                    selectedRepository = getString(R.string.load_app_current_repository)
                }
                R.id.button_retrofit -> if (checked) {
                    selectedURL = RETROFIT_URL
                    selectedRepository = getString(R.string.retrofit_type_safe_http_client)
                }
                else {
                    selectedURL = ""
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

}
