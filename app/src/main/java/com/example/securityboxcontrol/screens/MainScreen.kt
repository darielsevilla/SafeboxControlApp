@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.securityboxcontrol

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import java.io.OutputStream
import com.example.securityboxcontrol.components.CircleIconButton
import com.example.securityboxcontrol.components.TopBlueWave

@Composable
fun CajaFuerteEstadoScreen(
    modifier: Modifier = Modifier,
    onLockClick: () -> Unit = {},
    onSafeClick: () -> Unit = {},
) {
    val blueTop = Color(0xFF23355D)
    val background = Color(0xfff5f5f5)
    val activeGreen = Color(0xFF47B977)
    val red = Color(0xFFfe7473)

    var selected by remember { mutableIntStateOf(0) }
    var isLocked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background)
    ) {
        TopBlueWave(
            color = blueTop,
            waveHeight = 90.dp,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.TopCenter)
        )

        // Titulo
        Text(
            text = "Caja fuerte - Estado",
            color = Color.White,
            fontSize = 35.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .align(Alignment.TopCenter)
        )

        // --- Big center circle button (with shadow) ---
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 18.dp,
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
                .offset(y = (-90).dp)
        ) { /* empty: only shadow */ }

        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .size(230.dp)
                .align(Alignment.Center)
                .offset(y = (-90).dp)
        ) {
            // Usamos un Box para contener el Column que organizará el ícono y el texto
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Usamos una Column para alinear verticalmente el ícono y el texto
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center, // Alinea los elementos al principio (arriba)
                    modifier = Modifier.fillMaxSize() // Hace que la columna ocupe todo el espacio
                ) {
                    // Icono de estado
                    Icon(
                        painter = painterResource(id = if (isLocked) R.drawable.ic_lock else R.drawable.ic_unlock),
                        contentDescription = if (isLocked) "Locked" else "Unlocked",
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally),
                        tint = if (isLocked) Color.Red else activeGreen
                    )
                    // Texto que dice "Activo"
                    Text(
                        text = if (isLocked) "Cerrado" else "Abierto",
                        color = activeGreen,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 8.dp) // Espacio entre el ícono y el texto
                            .align(Alignment.CenterHorizontally) // Centra el texto horizontalmente
                    )
                }
            }
        }

        // --- Two small icon circle buttons horizontally aligned ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 400.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                size = 120.dp,
                icon = R.drawable.ic_lock,
                color = red,
                onClick = onLockClick,
            )
            Spacer(modifier = Modifier.width(40.dp))
            CircleIconButton(
                size = 120.dp,
                icon = R.drawable.ic_unlock,
                color = activeGreen,
                onClick = onSafeClick
            )
        }

        // --- Bottom navigation ---
        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = Color(0xFFffffff)
        ) {
            NavigationBarItem(
                selected = selected == 0,
                onClick = { selected = 0 },
                icon = {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = "Inicio")
                },
                label = { Text("Inicio") }
            )
            NavigationBarItem(
                selected = selected == 1,
                onClick = { selected = 1 },
                icon = {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = "Help")
                },
                label = { Text("Help") }
            )

            NavigationBarItem(
                selected = selected == 2,
                onClick = { selected = 2 },
                icon = {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = "Info")
                },
                label = { Text("Info") }
            )
        }
    }
}

/** Preview */
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun CajaFuerteEstadoScreenPreview() {
    MaterialTheme {
        CajaFuerteEstadoScreen()
    }
}