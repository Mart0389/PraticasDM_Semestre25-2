package com.example.weatherapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect // ALTERAÇÃO: Importado para corrotinas na UI
import androidx.compose.runtime.getValue     // ALTERAÇÃO: Importado para usar 'by'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // ALTERAÇÃO: Importado para ler Flows
import coil.compose.AsyncImage
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.Weather
import com.example.weatherapp.R
import java.text.DecimalFormat

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column {
        if (viewModel.city == null) {
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
            // --- INÍCIO DAS ALTERAÇÕES (Passo 2 da Parte 2) ---

            // Coleta os Flows do ViewModel considerando o ciclo de vida da UI [cite: 209, 211, 214]
            val cities by viewModel.cities.collectAsStateWithLifecycle()
            val weathers by viewModel.weather.collectAsStateWithLifecycle()
            val forecasts by viewModel.forecast.collectAsStateWithLifecycle()

            // Extrai os dados específicos da cidade selecionada [cite: 210, 212]
            val city = cities[viewModel.city!!]
            val weather = weathers[viewModel.city!!]
            val forecastList = forecasts[viewModel.city!!]

            // Dispara a carga da previsão em paralelo quando a cidade mudar [cite: 215, 217, 225]
            LaunchedEffect(viewModel.city!!) {
                viewModel.loadForecast(viewModel.city!!)
            }

            // --- FIM DAS ALTERAÇÕES ---

            Row {
                AsyncImage(
                    model = weather?.imgUrl,
                    modifier = Modifier.size(140.dp),
                    error = painterResource(id = R.drawable.loading),
                    contentDescription = "Imagem"
                )

                Column {
                    Spacer(modifier = Modifier.size(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically){
                        Text(
                            text = viewModel.city!!,
                            fontSize = 28.sp
                        )

                        // Define o ícone reativamente a partir do objeto coletado [cite: 213]
                        val icon = if (city?.isMonitored == true) Icons.Filled.Notifications
                        else Icons.Outlined.Notifications

                        Icon(
                            imageVector = icon,
                            contentDescription = "Monitorada?",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    // Atualiza via ViewModel usando o objeto reativo
                                    city?.let { viewModel.update(it.copy(isMonitored = !it.isMonitored)) }
                                }
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = weather?.desc ?: "...",
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = "Temp: ${weather?.temp ?: "--"}℃",
                        fontSize = 22.sp
                    )
                }
            }

            // Exibe a lista coletada do flow de previsões [cite: 218, 219]
            forecastList?.let { list ->
                LazyColumn {
                    items(items = list) { forecast ->
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

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable(onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
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
        Column {
            Text(text = "Min: $tempMin℃", fontSize = 16.sp)
            Text(text = "Max: $tempMax℃", fontSize = 16.sp)
        }
    }
}