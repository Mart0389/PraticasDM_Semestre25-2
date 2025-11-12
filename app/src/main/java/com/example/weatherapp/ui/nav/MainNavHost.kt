package com.example.weatherapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.ui.theme.HomePage
import com.example.weatherapp.ui.theme.ListPage
import com.example.weatherapp.ui.theme.MapPage
import com.example.weatherapp.MainViewModel

@Composable
fun MainNavHost(
    navController: NavHostController,
    viewModel: MainViewModel // <--- ADICIONE ESTE PARÂMETRO
){

    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel)  }
        composable<Route.List> { ListPage(viewModel = viewModel)  }
        composable<Route.Map>  { MapPage(viewModel = viewModel)  }
    }
}
