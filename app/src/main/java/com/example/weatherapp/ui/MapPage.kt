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
import com.example.weatherapp.model.Weather


@Composable
fun MapPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel // <--- PASSO 3: Adicionado
) {

    val camPosState = rememberCameraPositionState ()



    val recife = LatLng(-8.05, -34.9)
    val caruaru = LatLng(-8.27, -35.98)
    val joaopessoa = LatLng(-7.12, -34.84)


    val context = LocalContext.current
    val hasLocationPermission by
    remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
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

        viewModel.cities.forEach {
            if (it.location != null) {

                val weather = viewModel.weather(it.name)
                val desc = if (weather == Weather.LOADING) "Carregando clima..."
                else weather.desc

                Marker( state = MarkerState(position = it.location!!),
                    title = it.name, snippet = desc
                )

                //Marker( state = MarkerState(position = it.location),
                  //  title = it.name, snippet = "${it.location}")
            }
        }

        //Marker(
          //  state = MarkerState(position = recife),
          //  title = "Recife",
          //  snippet = "Marcador em Recife",
          //  icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        //)

        //Marker(
          //  state = MarkerState(position = caruaru),
          //  title = "Caruaru",
          //  snippet = "Marcador em Caruaru",
          //  icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        //)

        //Marker(
          //  state = MarkerState(position = joaopessoa),
          //  title = "Joao Pessoa",
          //  snippet = "Marcador em Joao Pessoa",
          //  icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        //)
    }

}


