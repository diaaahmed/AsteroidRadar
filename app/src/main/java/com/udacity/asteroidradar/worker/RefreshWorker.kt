package com.udacity.asteroidradar.worker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.repository.Repository
import com.udacity.asteroidradar.room.getInstance

class RefreshWorker(context: Context, params: WorkerParameters):CoroutineWorker(context,params) {

    companion object {
        const val WORKER_NAME = "refreshworker"
    }

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result
    {
        val database = getInstance(applicationContext)
        val reopsitory = Repository(database)

        return try{
            reopsitory.refreshAsteroids()
            Result.Success()
        }catch(e:Exception)
        {
            Result.Retry()
        }
    }
}