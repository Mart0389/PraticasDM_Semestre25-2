package com.example.weatherapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.nav.BottomNavBar
import com.example.weatherapp.ui.nav.MainNavHost
import com.example.weatherapp.ui.nav.BottomNavItem
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.ui.theme.CityDialog
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.weatherapp.api.WeatherService
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.ui.nav.Route
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

//private val MainActivity.items: List<BottomNavItem>

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    //private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val fbDB = remember { FBDatabase() }

            val weatherService = remember { WeatherService(this) }

            //val weatherService = remember { WeatherService() }

            val viewModel : MainViewModel = viewModel(
                factory = MainViewModelFactory(fbDB, weatherService)
            )

            val navController = rememberNavController()

            var showDialog by remember { mutableStateOf(false) }

            val bottomNavItems = listOf(
                BottomNavItem.HomeButton,
                BottomNavItem.ListButton,
                BottomNavItem.MapButton,

            )

            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val launcher = rememberLauncherForActivityResult(contract =
                ActivityResultContracts.RequestPermission(), onResult = {} )

            WeatherAppTheme {

                if (showDialog) CityDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { city ->
                        if (city.isNotBlank()) { viewModel.addCity(city)}
                        showDialog = false
                    })

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val name = viewModel.user?.name?:"[carregando...]"
                                Text("Bem-vindo/a! $name")
                            },
                            actions = {
                                IconButton( onClick = {
                                    Firebase.auth.signOut()

                                } ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Sair da Aplicação"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        //BottomNavBar(viewModel, items) -- o que tem na prática

                        BottomNavBar(viewModel = viewModel, items = bottomNavItems) // o que a IA sugeriu

                    //BottomNavBar(navController = navController, items = bottomNavItems)
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }


                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(navController = navController, viewModel = viewModel)
                    }

                    LaunchedEffect(viewModel.page) {
                        navController.navigate(viewModel.page) {
                            // Volta pilha de navegação até HomePage (startDest).
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    }

                }
            }
        }
    }
}