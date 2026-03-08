@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.securityboxcontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.io.OutputStream
import com.example.securityboxcontrol.components.CircleIconButton
import com.example.securityboxcontrol.components.TopBlueWave

// Colores
import com.example.securityboxcontrol.theme.Gris
import com.example.securityboxcontrol.theme.Azul
import com.example.securityboxcontrol.theme.Verde
import com.example.securityboxcontrol.theme.Rojo

@Composable
fun CajaFuerteEstadoScreen(
    modifier: Modifier = Modifier,
    onLockClick: () -> Unit = {},
    onSafeClick: () -> Unit = {},
) {

    var selected by remember { mutableIntStateOf(0) }
    var isLocked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Gris)
    ) {
        TopBlueWave(
            color = Azul,
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
        ) { }

        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .size(230.dp)
                .align(Alignment.Center)
                .offset(y = (-90).dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = if (isLocked) R.drawable.ic_lock else R.drawable.ic_unlock),
                        contentDescription = if (isLocked) "Locked" else "Unlocked",
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally),
                        tint = if (isLocked) Color.Red else Verde
                    )

                    Text(
                        text = if (isLocked) "Cerrado" else "Abierto",
                        color = Verde,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 500.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircleIconButton(
                            size = 140.dp,
                            icon = R.drawable.ic_lock,
                            color = Rojo,
                            onClick = onLockClick,
                        )
                        Text(
                            text = "Bloquear",
                            color = Color.Gray,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(60.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircleIconButton(
                            size = 140.dp,
                            icon = R.drawable.ic_unlock,
                            color = Verde,
                            onClick = onSafeClick
                        )
                        Text(
                            text = "Desbloquear",
                            color = Color.Gray,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Este Spacer empuja los botones hacia el centro
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