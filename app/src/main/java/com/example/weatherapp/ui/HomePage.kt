package com.example.weatherapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Necessário para carregar o placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Biblioteca para carregar imagens da rede [cite: 13]
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.R // Referência aos recursos do projeto [cite: 30]
import java.text.DecimalFormat

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column {
        if (viewModel.city == null) {
            // Caso nenhuma cidade esteja selecionada
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Blue).wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Selecione uma cidade!",
                    fontWeight = FontWeight.Bold, color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center, fontSize = 28.sp
                )
            }
        } else {
            // Caso uma cidade esteja selecionada
            Row {

                AsyncImage( // Substitui o Icon
                    model = viewModel.weather(viewModel.city!!).imgUrl,
                    modifier = Modifier.size(140.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )

                Column {
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = viewModel.city ?: "Selecione uma cidade...",
                        fontSize = 28.sp
                    )
                    viewModel.city?.let { name ->
                        val weather = viewModel.weather(name)
                        Spacer(modifier = Modifier.size(12.dp))
                        Text(
                            text = weather?.desc ?: "...",
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        // A temperatura deve estar dentro do escopo onde 'weather' existe
                        Text(
                            text = "Temp: " + weather?.temp + "℃",
                            fontSize = 22.sp
                        )
                    }
                }
            }

            // A lista de previsão deve ficar abaixo do Row principal
            viewModel.forecast(viewModel.city!!)?.let { forecasts ->
                LazyColumn {
                    items(items = forecasts) { forecast ->
                        ForecastItem(forecast, onClick = { })
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier: Modifier = Modifier
) {
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)

    // O Row envolve todo o conteúdo do item
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage( // Substitui o Icon
            model = forecast.imgUrl,
            modifier = Modifier.size(70.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )

        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = forecast.weather, fontSize = 24.sp)
            Row {
                Text(text = forecast.date, fontSize = 20.sp)
                Spacer(modifier = Modifier.size(12.dp))
            }
        }
        // Temperaturas Min e Max alinhadas horizontalmente ou verticalmente no Row
        Column {
            Text(text = "Min: $tempMin℃", fontSize = 16.sp)
            Text(text = "Max: $tempMax℃", fontSize = 16.sp)
        }
    }
}