package com.example.weatherapp

import androidx.lifecycle.ViewModel
import com.example.weatherapp.model.City
import androidx.compose.runtime.toMutableStateList

fun getCities() = List(20) { i ->
    City(name = "Cidade $i", weather = "Carregando clima...")

}

class MainViewModel : ViewModel() {
    private val _cities = getCities().toMutableStateList()
    val cities
        get() = _cities.toList()
    fun remove(city: City) {
        _cities.remove(city)
    }
    fun add(name: String) {
        _cities.add(City(name = name))
    }
}