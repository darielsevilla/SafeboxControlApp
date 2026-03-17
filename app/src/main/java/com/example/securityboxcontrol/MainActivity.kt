package com.example.securityboxcontrol

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import java.io.OutputStream
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.securityboxcontrol.notifications.showEsp32AlertNotification
import com.example.securityboxcontrol.screens.DeviceConnectScreen
import com.example.securityboxcontrol.screens.AlertasScreen
import com.example.securityboxcontrol.services.BluetoothWifiNotification
import java.util.*
class MainActivity : AppCompatActivity() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var connected: Boolean = false;
    private var bluetoothGatt: BluetoothGatt? = null

    private var device : BluetoothDevice? = null;
     //nombre del esp32
    private val deviceName: String = "SBESP32";


    //variables necesarias para el lock
    private var isLocked by mutableStateOf(true);
    private var connectedVal by mutableStateOf(2);
    private var buzzerActive by mutableStateOf(false);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure Bluetooth permissions are granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                1001
            )
        } else {
            val serviceIntent = Intent(this, BluetoothWifiNotification::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
            initializeBluetooth()

        }

        setContent {
            Navigation()
        }
    }

    private fun initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Toast.makeText(this, "Bluetooth not supported or not enabled", Toast.LENGTH_SHORT).show()
            return
        }

        //petición de permisos de location
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1002
            )
        } else {
            // Permission already granted
            startScanning()
        }
    }
    override fun onStop() {
        super.onStop()
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
        }catch(e : SecurityException){

        }
    }
    override fun onPause() {
        super.onPause()
        if (bluetoothGatt != null) {
            try{
            val deviceAddress = bluetoothGatt?.device?.address
            val sharedPreferences = getSharedPreferences("BluetoothPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("deviceAddress", deviceAddress)  // Save device address
            editor.apply()

            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null
        }catch(e: SecurityException){

        }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            // Restore the GATT connection using the saved device address
            val sharedPreferences = getSharedPreferences("BluetoothPrefs", MODE_PRIVATE)
            val deviceAddress = sharedPreferences.getString("deviceAddress", null)

            if (deviceAddress != null) {
                val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                if (device != null) {
                    // Attempt to reconnect to the device
                    bluetoothGatt = device.connectGatt(this, false, gattCallback)
                    Log.d("Bluetooth", "Reconnected to device: $deviceAddress")
                }
            }
        }catch(e: SecurityException){

        }
    }

    // Permissions result handling
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                initializeBluetooth()
            } else {
                Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Start scanning for devices

    private fun startScanning(){
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        try {
            // Ensure Bluetooth is supported and enabled
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
                Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show()
                return
            }

            Log.d("ScanResult", "Starting BLE scan...")

            val scanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    if (result == null) {
                        Log.d("ScanResult", "No result received")
                    } else {
                        Log.d("ScanResult", "Device found: ${result.device.name}")
                    }


                    result?.let { scanResult ->
                        device = scanResult.device
                        var name = device?.name
                        if (device?.name == deviceName) {
                            Log.d("ScanResult", "Device matched: ${device?.name}")
                            if (connectToEsp32(device)) {
                                Log.d("ScanResult", "Successfully connected to SBESP32")
                                connectedVal  = 0;
                                connected = true;
                                bluetoothAdapter.bluetoothLeScanner.stopScan(this)
                                Toast.makeText(this@MainActivity, "Conectado a SBESP32!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.d("ScanResult", "Device not matching: ${device?.name}")
                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e("ScanFailed", "Scan failed with error code: $errorCode")
                    Toast.makeText(this@MainActivity, "Scan failed: $errorCode", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            //revisión de permisos
            val bluetoothConnectPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            val bluetoothScanPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            )
            val locationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            Log.d("ScanResult", "Bluetooth permissions: ")
            Log.d("ScanResult", "BLUETOOTH_CONNECT: ${if (bluetoothConnectPermission == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied"}")
            Log.d("ScanResult", "BLUETOOTH_SCAN: ${if (bluetoothScanPermission == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied"}")
            Log.d("ScanResult", "ACCESS_FINE_LOCATION: ${if (locationPermission == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied"}")

            //scaneo de dispositivos
            bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)
            Toast.makeText(this, "Scanning for devices...", Toast.LENGTH_SHORT).show()
            val handler = android.os.Handler()
            handler.postDelayed({
                if(connected != true){
                Log.d("ScanResult", "Scanning stopped after timeout.")
                bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
                Toast.makeText(this, "Scanning stopped after timeout", Toast.LENGTH_SHORT).show()
                    connectedVal = 2
                } }, 10000)
        }catch(e : SecurityException){
            Log.e("ScanFailed", "Scan failed with Exception: ${e.printStackTrace()}")
        }
    }


    // GATT callback to handle connection status
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            try {
                runOnUiThread {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        connected = true
                        connectedVal  = 0
                        Toast.makeText(this@MainActivity, "Connected to ESP32", Toast.LENGTH_SHORT)
                            .show()
                        gatt.discoverServices()



                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        connected = false
                        connectedVal = 2
                        Toast.makeText(
                            this@MainActivity,
                            "Disconnected from ESP32",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }catch( e : SecurityException){

            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Find the characteristic for notifications
                try {
                    Log.d("ScanResult", "arrived at onServicesDiscovered")
                    var serviceUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";

                    var lockCharacteristicUID = "beb5483e-36e1-4688-b7f5-ea07361b26a9";
                    var service = gatt.getService(UUID.fromString(serviceUID))
                    var characteristic =
                        service?.getCharacteristic(UUID.fromString(lockCharacteristicUID))

                    // Enable notifications on the characteristic
                    if(characteristic == null){
                        Log.d("ScanResult", "Characteristic not found")
                    }
                    gatt.setCharacteristicNotification(characteristic, true)

                    // Enable the descriptor for notifications
                    var descriptor = characteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)

                    //caracteristica de buzzer

                }catch ( e : SecurityException){
                    Log.d("ScanResult", e.printStackTrace().toString());
                }
            }
        }



            override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            try {
                Log.d(
                    "ScanResult",
                    "Descriptor write for ${descriptor.characteristic.uuid}, status=$status"
                )
                var serviceUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
                var buzzerCharacteristicUID = "beb5483e-36e1-4688-b7f5-ea07361b26a7";
                var lockCharacteristicUID = "beb5483e-36e1-4688-b7f5-ea07361b26a9";
                if (descriptor.characteristic.uuid.toString() == lockCharacteristicUID) {
                    // Now enable buzzer notifications
                    val buzzerChar = gatt.getService(UUID.fromString(serviceUID))
                        ?.getCharacteristic(UUID.fromString(buzzerCharacteristicUID))
                    gatt.setCharacteristicNotification(buzzerChar, true)
                    val buzzerDesc =
                        buzzerChar?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    buzzerDesc?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(buzzerDesc)
                }
            }catch(e : SecurityException){

            }
        }
        // This method is called when the characteristic changes (notification received)
        override
        fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val response = characteristic.value.toString(Charsets.UTF_8)
            Log.d("ScanResult", "Received response: $response")
            // You can add further processing here to handle the response (OPEN, CLOSE, UNKNOWN)
            if(characteristic.uuid.toString() == "beb5483e-36e1-4688-b7f5-ea07361b26a9"){
                if (response == "OPEN") {
                    // Handle OPEN command response
                    isLocked = false;
                    Log.d("ScanResult", "ESP32 confirmed OPEN command")


                    if(!isLocked){
                        Log.d("ScanResult", "caja abierta");
                    }else{
                        Log.d("ScanResult", "caja cerrado");
                    }

                } else if (response == "CLOSE") {
                    // Handle CLOSE command response
                    Log.d("ScanResult", "ESP32 confirmed CLOSE command")

                    isLocked = true;
                    if(!isLocked){
                        Log.d("ScanResult", "caja abierta");
                    }else{
                        Log.d("ScanResult", "caja cerrado");
                    }

                }
            } else if (characteristic.uuid.toString() == "beb5483e-36e1-4688-b7f5-ea07361b26a7"){
                if(response == "STOP") {
                    Log.d("ScanResult", "El buzzer esta apagado");
                    buzzerActive = false;
                } else if(response == "START"){
                    Log.d("ScanResult", "El buzzer esta encendido");
                    buzzerActive = true;
                    showEsp32AlertNotification("ALERTA: intento de apertura de caja fuerte")
                }
            }

        }


    }

    // Connect to ESP32 device
    private fun connectToEsp32(device: BluetoothDevice?): Boolean {
        try{
            device?.let {
                try {
                    bluetoothGatt = it.connectGatt(this, false, gattCallback)
                    Toast.makeText(this, "Connecting to ${it.name}...", Toast.LENGTH_SHORT).show()

                    return true
                } catch (e: Exception) {
                    Log.e("ConnectError", "Connection failed: ${e.message}")
                    Toast.makeText(this, "Connection failed: ${e.message}", Toast.LENGTH_LONG).show()
                    return false
                }
            }}catch(e: SecurityException){

        }
        return false
    }

    // Send command to ESP32
    private fun abrirCerrarCaja(command: String) {
        //conseguir la caracteristica designada a abrir y cerrar la caja
        var serviceUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
        var lockCharacteristicUID ="beb5483e-36e1-4688-b7f5-ea07361b26a9";
        try {
            var characteristic = bluetoothGatt?.getService(UUID.fromString(serviceUID))
                ?.getCharacteristic(UUID.fromString(lockCharacteristicUID));
            if (characteristic != null) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    bluetoothGatt?.writeCharacteristic(characteristic, command.toByteArray(),
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                }else{
                    characteristic.setValue(command);
                    bluetoothGatt?.writeCharacteristic(characteristic);
                }


            } else {
                Log.d("ScanResult", "No se encontró la caracteristica con UID ${command}")
                Toast.makeText(
                    this@MainActivity,
                    "No se encontró la caracteristica con UID ${command}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }catch(e : SecurityException){

        }

        //
    }


    private fun apagarAlarma(command: String) {

        var serviceUID = "4fafc201-1fb5-459e-8fcc-c5c9c331914b";
        var lockCharacteristicUID ="beb5483e-36e1-4688-b7f5-ea07361b26a7";
        try {
            Log.d("ScanResult", "LLego a apagar alarma. comando: ${command}")
            var characteristic = bluetoothGatt?.getService(UUID.fromString(serviceUID))
                ?.getCharacteristic(UUID.fromString(lockCharacteristicUID));
            if (characteristic != null) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    bluetoothGatt?.writeCharacteristic(characteristic, command.toByteArray(),
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                }else{
                    characteristic.setValue(command);
                    bluetoothGatt?.writeCharacteristic(characteristic);
                }


            } else {
                Log.d("ScanResult", "No se encontró la caracteristica con UID ${command}")
                Toast.makeText(
                    this@MainActivity,
                    "No se encontró la caracteristica con UID ${command}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }catch(e : SecurityException){
            Log.d("ScanResult", "ERROR EN APAGAR ALARMA: ${e.printStackTrace().toString()}")
        }

        //
    }

    @Composable
    private fun Navigation()  {
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

                    DeviceConnectScreen(connectVal = connectedVal, connectFunc = {
                        if (connectedVal == 2 || connectedVal == 1){
                            connectedVal = 1
                            initializeBluetooth()
                        }else if(connectedVal == 0){
                            connectedVal = 2;
                            bluetoothGatt = null;
                            device = null;
                            connected = false;
                            Toast.makeText(
                                this@MainActivity,
                                "Disconnected from ESP32",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                }
                composable("Inicio") {
                    CajaFuerteEstadoScreen(
                        isLocked = isLocked,
                        onLockClick = {
                            abrirCerrarCaja("CLOSE")
                        },
                        onSafeClick = {
                            abrirCerrarCaja("OPEN");
                        },
                    )
                }
                composable("Alertas") {
                    AlertasScreen(buzzerActive = buzzerActive, onSafeClick = {
                        if(buzzerActive){
                            apagarAlarma("STOP");
                        }else{
                            apagarAlarma("START");
                        }
                    })
                }
            }
        }
    }
}