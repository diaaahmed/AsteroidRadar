package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.PictureOfDay
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidApi
{
    // Here we get asteroids by use our api key
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("api_key") api_key: String):String


    // Here we get picture of the day by use our api key
    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(@Query("api_key") api_key: String):PictureOfDay
}