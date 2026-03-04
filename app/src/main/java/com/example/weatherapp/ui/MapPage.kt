package com.example.weatherapp.ui.theme

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.weatherapp.MainViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.model.Weather


@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel // <--- PASSO 3: Adicionado
) {
    val context = LocalContext.current
    val camPosState = rememberCameraPositionState ()

    val cities by viewModel.cities.collectAsStateWithLifecycle(emptyMap())
    val weathers by viewModel.weather.collectAsStateWithLifecycle(emptyMap())

    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true),
        cameraPositionState = camPosState,
        onMapClick = {
            viewModel.addCity(it)
        }
    ) {
        cities.values.forEach { city ->
            if (city.location != null) {
                val weather = weathers[city.name] ?: Weather.LOADING

                LaunchedEffect(city.name) {
                    viewModel.loadWeather(city.name)
            }

                LaunchedEffect(weather) {
                    viewModel.loadBitmap(city.name)
                }

                val image = weather.bitmap ?: getDrawable(context, R.drawable.loading)!!.toBitmap()
                val markerIcon = BitmapDescriptorFactory.fromBitmap(image.scale(120, 120))

                val desc = if (weather == Weather.LOADING) "Carregando clima..."
                else if (weather == Weather.ERROR) "Erro ao carregar"
                else weather.desc

                Marker(
                    state = MarkerState(position = city.location!!),
                    icon = markerIcon,
                    title = city.name,
                    snippet = "Temp: ${weather.temp}℃ - $desc"
                )
            }
        }
    }
}

