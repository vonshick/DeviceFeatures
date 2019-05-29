package com.example.devicefeatures

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.app.PendingIntent
import android.content.IntentFilter
import android.os.BatteryManager
import android.content.BroadcastReceiver


class MainActivity : AppCompatActivity() {

    private var notificationManager: NotificationManager? = null

    private var mContext: Context? = null

    private var temperature: Float = 0.0f

    private var mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnClickListeners()
        prepareToTempMeasurment()
        prepareToShowNotifications()
        hideSupportActionBar()
    }

    override fun onUserLeaveHint() {
        showNotification()
    }

    fun setOnClickListeners(){
        gpsButton.setOnClickListener{
            temperatureTextView.setText("")
            var intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        temperatureButton.setOnClickListener {
            temperatureTextView.setText("Battery Temperature\n" + temperature + " " + 0x00B0.toChar() + "C")
        }

        ledButton.setOnClickListener {
            temperatureTextView.setText("")
        }
    }

    fun prepareToTempMeasurment(){
        mContext = applicationContext
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        mContext?.registerReceiver(mBroadcastReceiver, iFilter)
    }

    fun prepareToShowNotifications(){
        notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel(
            "com.example.devicefeatures.news",
            "Example notification",
            "Example News Channel")
    }

    fun hideSupportActionBar(){
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)
    }

    private fun showNotification(){
        val channelId = "com.example.devicefeatures.news"
        val notification = Notification.Builder(this@MainActivity, channelId)
            .setContentTitle("Working in background")
            .setContentText("Tap on notification and back to the app")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelId)
            .setNumber(10)
            .setAutoCancel(true)

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        notification.setContentIntent(pendingIntent)

        notificationManager?.notify(101, notification.build())
    }

}
