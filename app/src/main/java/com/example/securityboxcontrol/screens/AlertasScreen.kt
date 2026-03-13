package com.example.securityboxcontrol.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securityboxcontrol.R
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.securityboxcontrol.theme.RojoClaro
import com.example.securityboxcontrol.theme.RojoMedio
import com.example.securityboxcontrol.theme.RojoOscuro
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

// Gradiente rojo para fondo
val RedGradient = Brush.verticalGradient(
    colors = listOf(
        RojoClaro,
        RojoMedio,
        RojoOscuro,
    )
)

@Composable
fun AlertasScreen(
    modifier: Modifier = Modifier,
    buzzerActive: Boolean,
    onSafeClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.bell_alert)
    )
    val context = LocalContext.current
    // Vibrate the device for a given duration (default 200ms)
    fun Context.vibrateDevice(duration: Long = 200) {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(duration)
        }
    }

    // Launch vibration when buzzer is active (infinite animation)
    LaunchedEffect(buzzerActive) {
        if (buzzerActive) {
            // Start the vibration for 20 seconds
            val vibratorJob = launch {
                while(buzzerActive){
                    context.vibrateDevice(1000)
                    delay(1000)
                }
                 // Keep vibrating for 20 seconds
            }

            // If buzzerActive becomes false, cancel the vibration immediately
            launch {
                while (isActive) {
                    if (!buzzerActive) {
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.cancel()
                        vibratorJob.cancel()
                        break;
                    }
                    delay(100) // Check every 100ms
                }
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RedGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "ALERTA",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Animación Lottie bell_alert
            if(buzzerActive){
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            spotColor = RojoClaro.copy(alpha = 0.5f),
                            ambientColor = RojoOscuro.copy(alpha = 0.3f)
                        ),
                    iterations = LottieConstants.IterateForever,
                    speed = 1f
                )
            }else{
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .size(300.dp)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            spotColor = RojoClaro.copy(alpha = 0.5f),
                            ambientColor = RojoOscuro.copy(alpha = 0.3f)


                        ),
                    iterations = 1,
                    speed = 1f

                )

            }

            Spacer(modifier = Modifier.height(40.dp))

            // Texto informativo
            Text(
                text = "Alerta SOS enviada a tus acompañantes.",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Botón "Soy yo"
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(50.dp),
                        spotColor = Color(0xFFFFB800).copy(alpha = 0.5f)
                    )
                    .clickable { onSafeClick() }

            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Soy yo",
                        color = RojoOscuro,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun SOSScreenPreview() {
    MaterialTheme {
        AlertasScreen(buzzerActive = false, onSafeClick = {});
    }
}