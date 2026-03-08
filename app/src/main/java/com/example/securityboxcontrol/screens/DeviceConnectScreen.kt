package com.example.securityboxcontrol.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.securityboxcontrol.CajaFuerteEstadoScreen
import com.example.securityboxcontrol.R
@Composable
fun DeviceConnectScreen(modifier: Modifier = Modifier,
                        deviceName: String = "-----",
                        connectVal: Int = 2,
                        connectFunc: (par:Int) -> Boolean = {true}){
    //connected: 0-> conectado, 1->conectandose, 2-> no conectado
    var bgColor = Color(0xFF23355D)
    var cardColor = Color(0xFFFFF)
    var connected = connectVal
    Column(modifier = modifier
        .fillMaxSize()
        .background(bgColor)
        .padding(20.dp, 40.dp, 20.dp, 20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)){
        Text(
            text = "Caja fuerte - Conexión",
            color = Color.White,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding()
                )

        Column(
            Modifier.fillMaxHeight().padding(0.dp, 40.dp, 0.dp, 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(10.dp, 0.dp)
                    .shadow(14.dp, MaterialTheme.shapes.medium)
                    .clickable{
                        var previousConnect = connected
                        connected = 1
                        if(connectFunc(previousConnect) && previousConnect == 2){
                            connected = 0
                        }else{
                            connected = 2
                        }
                    }

                ,

                shape = MaterialTheme.shapes.medium
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(30.dp, 30.dp, 30.dp, 35.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = deviceName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            buildAnnotatedString {


                                if (connected == 0) {
                                    append("Estado: ")
                                    withStyle(style = SpanStyle(color = Color(0xFF00EE84))) {
                                        append("Conectado")
                                    }
                                } else {
                                    withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                        append("Estado: ")
                                        withStyle(style = SpanStyle(color = Color(0xFFfe7473))) {
                                            append("Desconectado")
                                        }
                                    }
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 24.sp)
                        )
                    }

                    if (connected == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.checkconnected),
                            contentDescription = "Check Connected Icon",
                            modifier = Modifier.size(150.dp)
                        )
                    } else if(connected == 1){
                        CircularProgressIndicator(
                            modifier = Modifier.size(150.dp),
                            color = Color(0xFF23355D),
                            strokeWidth = 6.dp
                        )
                    }else{
                        Image(
                            painter = painterResource(id = R.drawable.xdisconnected),
                            contentDescription = "Check Connected Icon",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }

            }

            Text(
                text = "* Presione el boton para conectar/desconectar la caja fuerte *",
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
            )
        }
    }
}


/** Preview */
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
private fun DeviceConnectScreenPreview() {
    MaterialTheme {
        DeviceConnectScreen( connectVal = 2)
    }
}