package com.example.securityboxcontrol.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securityboxcontrol.R

@Composable
fun DeviceConnectScreen(
    modifier: Modifier = Modifier,
    deviceName: String = "-----",
    connectVal: Int = 2,
    connectFunc: (par: Int) -> Boolean = { true }
) {
    val bgColor = Color(0xFF23355D)
    var connected by remember { mutableStateOf(connectVal) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(20.dp, 40.dp, 20.dp, 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la pantalla
        Text(
            text = "Caja Fuerte - Conexión",
            color = Color.White,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        )

        // Contenedor que ocupa todo el espacio restante
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Card centrada
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(10.dp)
                        .shadow(14.dp, MaterialTheme.shapes.medium)
                        .clickable {
                            val previousConnect = connected
                            if (connectFunc(previousConnect) && previousConnect == 2) {
                                connected = 0
                            } else if (previousConnect == 0) {
                                connected = 2
                            } else {
                                connected = 2
                            }
                        },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    // Contenido de la card completamente centrado
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Nombre del dispositivo
                            Text(
                                text = deviceName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Estado
                            Text(
                                buildAnnotatedString {
                                    append("Estado: ")
                                    withStyle(
                                        style = SpanStyle(
                                            color = if (connected == 0) Color(0xFF00EE84) else Color(0xFFfe7473)
                                        )
                                    ) {
                                        append(if (connected == 0) "Conectado" else "Desconectado")
                                    }
                                },
                                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 32.dp)
                            )

                            // Icono o indicador centrado
                            when (connected) {
                                0 -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_link),
                                        contentDescription = "Check Connected Icon",
                                        tint = Color(0xFF47B977),
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                                1 -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(150.dp),
                                        color = Color(0xFF23355D),
                                        strokeWidth = 6.dp
                                    )
                                }
                                else -> {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_unlink),
                                        contentDescription = "Disconnected Icon",
                                        tint = Color(0xFFfe7473),
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Texto informativo inferior
                Text(
                    text = "* Presione el botón para conectar/desconectar la caja fuerte *",
                    color = Color.White,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                )
            }
        }
    }
}

// Versión simplificada con todo centrado
@Composable
fun DeviceConnectScreenSimple(
    modifier: Modifier = Modifier,
    deviceName: String = "-----",
    connectVal: Int = 2,
    connectFunc: (par: Int) -> Boolean = { true }
) {
    val bgColor = Color(0xFF23355D)
    var connected by remember { mutableStateOf(connectVal) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(20.dp, 40.dp, 20.dp, 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text(
            text = "Caja Fuerte - Conexión",
            color = Color.White,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        // Card centrada
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(8f)
                .padding(10.dp)
                .shadow(14.dp, MaterialTheme.shapes.medium)
                .clickable {
                    connected = if (connected == 0) 2 else 0
                },
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            // Todo centrado dentro de la card
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = deviceName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Estado: ${if (connected == 0) "Conectado" else "Desconectado"}",
                        fontSize = 20.sp,
                        color = if (connected == 0) Color(0xFF00EE84) else Color(0xFFfe7473),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Icono centrado
                    if (connected == 0) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_link),
                            contentDescription = "Disconnected",
                            tint = Color(0xFF47B977),
                            modifier = Modifier.size(120.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_unlink),
                            contentDescription = "Disconnected",
                            tint = Color(0xFFfe7473),
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }
            }
        }

        // Texto inferior
        Text(
            text = "Presione el botón para conectar/desconectar",
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DeviceConnectScreenPreview() {
    MaterialTheme {
        DeviceConnectScreen(connectVal = 2)
    }
}