package com.example.securityboxcontrol

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import java.io.OutputStream
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.securityboxcontrol.services.BluetoothWifiNotification
import java.util.*
class MainActivity : AppCompatActivity() {
    private val sppUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var connected: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //empezar servicio de notificaciones
        val intent = Intent(this, BluetoothWifiNotification::class.java)
        startService(intent)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1001)
        }else{
            val deviceName : String = "NombreGenerico";
            if(connectToEsp32(deviceName)){
                Toast.makeText(this, "Conectado a $deviceName", Toast.LENGTH_SHORT).show()
                connected = true;
            }else{
                Toast.makeText(this, "No se puede conectar a $deviceName", Toast.LENGTH_SHORT).show()

            }
            setContent { CajaFuerteEstadoScreen (
                onLockClick = {
                    sendCommand("CERRAR")
                },
                onSafeClick = {
                    sendCommand("OPEN")
                },
            ) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, proceed with the connection
                val deviceName: String = "DeviceName"
                if (connectToEsp32(deviceName)) {
                    Toast.makeText(this, "Connected to $deviceName", Toast.LENGTH_SHORT).show()
                    connected = true
                } else {
                    Toast.makeText(this, "No se puede conectar a $deviceName", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Permission denied
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun connectToEsp32(deviceName: String) : Boolean {
        //chequear permisos
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter?.bondedDevices ?: return true
            val esp32Device = pairedDevices.find { it.name == deviceName }
            if (esp32Device == null) {
                Toast.makeText(this, "ESP32 not paired", Toast.LENGTH_SHORT).show()
                return false
            }

            try {
                bluetoothSocket = esp32Device.createRfcommSocketToServiceRecord(sppUUID)
                bluetoothSocket?.connect()
                outputStream = bluetoothSocket?.outputStream
                Toast.makeText(this, "Connected to $deviceName", Toast.LENGTH_SHORT).show()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Connection failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    // Send command to ESP32
    private fun sendCommand(command: String) {
        if (outputStream == null || connected == false) {
            Toast.makeText(this, "No esta emparejado a la caja fuerte", Toast.LENGTH_SHORT).show()
            return
        }
        val message = "$command\n"
        outputStream?.write(message.toByteArray())
        outputStream?.flush()
        Toast.makeText(this, "Comando enviado: $command", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CajaFuerteEstadoScreen(
        onLockClick = {  },
        onSafeClick = {  }
    )
}