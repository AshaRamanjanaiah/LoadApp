package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val id = intent.extras?.getInt(ID)
        val message = intent.extras?.getString(MESSAGE)
        val status = intent.extras?.getString(STATUS)

        Log.d("Asha", id.toString())
        message?.let { Log.d("Asha", it) }
        status?.let { Log.d("Asha", it) }

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        id?.let { notificationManager.cancel(id) }

        button.setOnClickListener {
            Log.d("Asha", "Button Clicked")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        text_repository_name.text = message
        text_status_value.apply {
            if (status == getString(R.string.success)) {
                text = getString(R.string.success)
                setTextColor(getColor(R.color.colorPrimaryDark))
            } else {
                text = getString(R.string.failed)
                setTextColor(Color.RED)
            }
        }

    }

}
