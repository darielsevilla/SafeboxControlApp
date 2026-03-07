@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.securityboxcontrol

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import java.io.OutputStream

@Composable
fun CajaFuerteEstadoScreen(
    modifier: Modifier = Modifier,
    onLockClick: () -> Unit = {},
    onSafeClick: () -> Unit = {},
) {
    val blueTop = Color(0xFF23355D)
    val background = Color(0xFFFFFFFF)
    val activeGreen = Color(0xFF00EE84)

    var selected by remember { mutableIntStateOf(0) }

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
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Activo",
                    color = activeGreen,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium
                )
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
                iconRes = R.drawable.lockicon,
                onClick = onLockClick,
            )
            Spacer(modifier = Modifier.width(40.dp)) // Espacio reducido entre los botones
            CircleIconButton(
                size = 120.dp,
                iconRes = R.drawable.safeicon,
                onClick = onSafeClick
            )
        }

        // --- Bottom navigation ---
        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = Color(0xFFEFE7F4) // light purple-ish like screenshot
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

@Composable
private fun TopBlueWave(
    color: Color,
    waveHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val wavePx = waveHeight.toPx()

        // Fill everything blue first
        drawRect(
            color = color,
            topLeft = Offset(0f, 0f),
            size = Size(w, h)
        )

        val path = Path().apply {
            // start at bottom-left (slightly above bottom)
            moveTo(0f, h - wavePx)

            quadraticBezierTo(
                w / 2f, h + wavePx,
                w, h - wavePx
            )
            // close the shape down to the bottom edge
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = path,
            color = Color.White
        )
    }
}

/**
 * A circular icon button with visible shadow (like your small lock/safe buttons).
 */
@Composable
private fun CircleIconButton(
    size: androidx.compose.ui.unit.Dp,
    iconRes: Int,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 10.dp,
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(46.dp)
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