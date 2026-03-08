package com.example.securityboxcontrol.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securityboxcontrol.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.example.securityboxcontrol.theme.Azul

@Composable
fun DeviceConnectScreen(
    modifier: Modifier = Modifier,
    deviceName: String = "-----",
    connectVal: Int = 2,
    connectFunc: (par: Int) -> Boolean = { true }
) {
    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Azul,
            Color(0xFF2A3F6A),
            Color(0xFF3A4F7A)
        )
    )

    var connected by remember { mutableStateOf(connectVal) }

    // Animación Lottie
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.connection_wave))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(blueGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Text(
                text = "CONEXIÓN",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Box(
                modifier = Modifier.size(550.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animación Lottie
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(550.dp)
                        .align(Alignment.Center),
                    iterations = LottieConstants.IterateForever,
                    speed = 1f
                )

                // Círculo blanco con los iconos
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .size(250.dp)
                        .shadow(
                            elevation = 30.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFF00A3FF).copy(alpha = 0.5f),
                            ambientColor = Color(0xFF0066CC).copy(alpha = 0.3f)
                        )
                        .clickable {
                            val previousConnect = connected
                            if (connectFunc(previousConnect) && previousConnect == 2) {
                                connected = 0
                            } else if (previousConnect == 0) {
                                connected = 2
                            } else {
                                connected = 2
                            }
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        when (connected) {
                            0 -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_link),
                                        contentDescription = "Connected",
                                        tint = Color(0xFF47B977),
                                        modifier = Modifier.size(120.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "CONECTADO",
                                        color = Color(0xFF47B977),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            1 -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(100.dp),
                                        color = Azul,
                                        strokeWidth = 6.dp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "CONECTANDO",
                                        color = Azul,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            else -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_unlink),
                                        contentDescription = "Disconnected",
                                        tint = Color(0xFFfe7473),
                                        modifier = Modifier.size(120.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "DESCONECTADO",
                                        color = Color(0xFFfe7473),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Presione el botón para conectar/desconectar la caja fuerte.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DeviceConnectScreenPreview() {
    MaterialTheme {
        DeviceConnectScreen(connectVal = 2)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DeviceConnectScreenConnectedPreview() {
    MaterialTheme {
        DeviceConnectScreen(connectVal = 0)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DeviceConnectScreenConnectingPreview() {
    MaterialTheme {
        DeviceConnectScreen(connectVal = 1)
    }
}