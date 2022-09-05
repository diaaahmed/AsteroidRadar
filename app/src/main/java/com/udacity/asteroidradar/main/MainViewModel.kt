package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Filter
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidObj
import com.udacity.asteroidradar.repository.Repository
import com.udacity.asteroidradar.room.getInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application:Application) : AndroidViewModel(application)
{

    private val database = getInstance(application)
    private val repository = Repository(database)

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay:LiveData<PictureOfDay>
                get() = _pictureOfDay

    private val _navigateToDetailAsteroid = MutableLiveData<Asteroid?>()
    val navigateToDetailAsteroid: MutableLiveData<Asteroid?>
                get() = _navigateToDetailAsteroid

    private val _filterAsteroid = MutableLiveData(Filter.ALL)


    @RequiresApi(Build.VERSION_CODES.O)
    val asteroidList = Transformations.switchMap(_filterAsteroid){
        when(it)
        {
            Filter.WEEK -> repository.weekAsteroids
            Filter.TODAY -> repository.todayAsteroids
            else -> repository.allAsteroids
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid)
    {
        _navigateToDetailAsteroid.value = asteroid
    }

    fun onAsteroidNav()
    {
        _navigateToDetailAsteroid.value = null
    }

    fun onFilterChange(filter: Filter)
    {
        _filterAsteroid.postValue(filter)
    }

    class MainViewModelFactory(val app:Application): ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java))
            {
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable")
        }
    }

    init{
        viewModelScope.launch {
            repository.refreshAsteroids()
            refreshPictureOfDay()
        }
    }

    private suspend fun refreshPictureOfDay()
    {
        withContext(Dispatchers.IO)
        {
            try{
                _pictureOfDay.postValue(AsteroidObj.retrofitS.getPictureOfTheDay(Constants.API_KEY))
            }
            catch(e: java.lang.Exception)
            {
                Log.d("refreshPictureOfDay",
                    e.printStackTrace().toString())
            }
        }
    }
}