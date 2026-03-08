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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.securityboxcontrol.screens.DeviceConnectScreen
import com.example.securityboxcontrol.screens.AlertasScreen
import com.example.securityboxcontrol.services.BluetoothWifiNotification
import java.util.*
class MainActivity : AppCompatActivity() {
    private val sppUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var connected: Boolean = false;


    //nombre del esp32
    private val deviceName: String = "NombreGenerico";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //empezar servicio de notificaciones
        val intent = Intent(this, BluetoothWifiNotification::class.java)
        startService(intent)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                1001
            )
        } else {

            if (connectToEsp32()) {
                Toast.makeText(this, "Conectado a $deviceName", Toast.LENGTH_SHORT).show()
                connected = true;
            } else {
                Toast.makeText(this, "No se puede conectar a $deviceName", Toast.LENGTH_SHORT)
                    .show()

            }
            setContent {
                Navigation()
                /*CajaFuerteEstadoScreen(
                    onLockClick = {
                        sendCommand("CERRAR")
                    },
                    onSafeClick = {
                        sendCommand("OPEN")
                    },
                )*/
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val deviceName: String = "DeviceName"
                if (connectToEsp32()) {
                    Toast.makeText(this, "Connected to $deviceName", Toast.LENGTH_SHORT).show()
                    connected = true
                } else {
                    Toast.makeText(this, "No se puede conectar a $deviceName", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun connectToEsp32(): Boolean {
        //chequear permisos
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        ) {
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

    @Composable
    private fun Navigation() {
        val navController = rememberNavController()

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFFffffff)
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("Conexión") },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_link),
                                contentDescription = "Conexión",
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        label = { Text("Conexión") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("Inicio") },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = "Inicio",
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("Alertas") },
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_notification),
                                contentDescription = "Alertas",
                                modifier = Modifier.size(30.dp),
                            )
                        },
                        label = { Text("Alertas") }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "Inicio",
                modifier = Modifier.padding(innerPadding)
            ) {
                // Define composable screens here, each referencing the respective composable function
                composable("Conexión") {
                    var connectVal = 2
                    if(connected) connectVal = 0
                    DeviceConnectScreen(connectVal = connectVal, connectFunc ={connected : Int -> connectToEsp32() })
                }
                composable("Inicio") {
                    CajaFuerteEstadoScreen(
                        onLockClick = {
                            sendCommand("CERRAR")
                        },
                        onSafeClick = {
                            sendCommand("OPEN")
                        },
                    )
                }
                composable("Alertas") {
                    AlertasScreen()
                }
            }
        }
    }
}