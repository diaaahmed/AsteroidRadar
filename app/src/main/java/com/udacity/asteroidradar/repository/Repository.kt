package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidObj
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.room.AsteroidsDatabase
import com.udacity.asteroidradar.room.asDatabaseModel
import com.udacity.asteroidradar.room.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class Repository(private val database: AsteroidsDatabase)
{
    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = LocalDateTime.now().minusDays(7)

    val allAsteroids:LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDatabaseDao.getAsteroids())
        {
            it.asDomainModel()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    val todayAsteroids:LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDatabaseDao
            .getAsteroidDay(startDate.format(DateTimeFormatter.ISO_DATE))){
                it.asDomainModel()
        }


    @RequiresApi(Build.VERSION_CODES.O)
    val weekAsteroids:LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDatabaseDao
            .getAsteroidDate(
                startDate.format(DateTimeFormatter.ISO_DATE),
            endDate.format(DateTimeFormatter.ISO_DATE))
        ){
            it.asDomainModel()
        }


    suspend fun refreshAsteroids()
    {
        withContext(Dispatchers.IO)
        {
            try{
                val asteroids = AsteroidObj.retrofitS.getAsteroids(Constants.API_KEY)
                val result = parseAsteroidsJsonResult(JSONObject(asteroids))
                database.asteroidDatabaseDao.insertAll(*result.asDatabaseModel())
                Log.d("Refresh Success", "refreshAsteroids: ")
            }
            catch(e:Exception)
            {
                Log.d("Exc", e.message.toString())
            }
        }
    }

}