package com.example.securityboxcontrol.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.service.notification.NotificationListenerService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class BluetoothWifiNotification : Service(){
    private val sppUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private var inputStream: InputStream? = null
    private val bluetoothReceiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                if (state == BluetoothAdapter.STATE_ON) {
                    sendNotification("ALERTA: Intento de apertura de caja fuerte")
                } else if (state == BluetoothAdapter.STATE_OFF) {
                    sendNotification("ALERTA: Intento de apertura de caja fuerte")
                }
            }
        }
    }

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // For Android 12 and above, Bluetooth permissions need to be requested at runtime
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        super.onCreate()
        startForegroundService()
        val deviceName: String = "WeAreCharlieKirk"
        val connected = connectToEsp32(deviceName)
        Log.d("NOTIFICATION", "LLEGO AQUI");
        var passedOnce : Boolean = false
        /*while(!connected){
            if(!passedOnce){
                Toast.makeText(this, "Failed to connect to $deviceName", Toast.LENGTH_SHORT).show()
                passedOnce = true
            }
            var handler = Handler(Looper.getMainLooper())

                handler.postDelayed({ handler.removeCallbacksAndMessages(null)
                connectToEsp32(deviceName)
            }, 5000)

        }*/

        //no va a avanzar aqui hasta que funcione el esp32
        Toast.makeText(this, "Connected to $deviceName", Toast.LENGTH_SHORT).show()
        listenForData()

    }

    private fun startForegroundService() {

        val channelId = "bluetooth_notification_channel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bluetooth Notifications",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        //convertirlo a servicio foreground
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Bluetooth WiFi Service")
            .setContentText("Listening for Bluetooth signals...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            startForeground(1, notification, serviceType)
        } else {
            startForeground(1, notification)
        }

    }
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "bluetooth_wifi_channel"
            val channelName = "Bluetooth/Wi-Fi Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create a notification
        val notification = NotificationCompat.Builder(this, "bluetooth_wifi_channel")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Bluetooth/Wi-Fi Status")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Send the notification
        with(NotificationManagerCompat.from(this)) {
            notify(1, notification)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister receiver when service is destroyed
        unregisterReceiver(bluetoothReceiver)
    }

    //metodos para esp32

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectToEsp32(deviceName: String): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            return false // Bluetooth not supported
        }

        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter?.bondedDevices ?: return false
        val esp32Device = pairedDevices.find { it.name == deviceName }
        if (esp32Device == null) {
            Toast.makeText(this, "ESP32 not paired", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(sppUUID)
            bluetoothSocket?.connect()
            outputStream = bluetoothSocket?.outputStream
            inputStream = bluetoothSocket?.inputStream
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Connection failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
        return false
    }

    // Listen for incoming data
    private fun listenForData() {
        Thread {
            try {
                val buffer = ByteArray(1024) // buffer for reading incoming data
                var bytes: Int

                // Read data from the input stream
                while (true) {
                    bytes = inputStream?.read(buffer) ?: 0
                    if (bytes > 0) {
                        val receivedData = String(buffer, 0, bytes)
                        showNotification(receivedData)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun showNotification(data: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channelId = "bluetooth_notification_channel"

        // Create notification
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("ESP32 Message")
            .setContentText("Received: $data")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show the notification
        notificationManager?.notify(0, notification)
    }


}