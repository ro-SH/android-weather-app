package com.example.weatherapp.data.model

data class Coordinates(
    val lat: Double,
    val lon: Double
)

fun Coordinates.areFilled() = lat != 0.0 && lon != 0.0
