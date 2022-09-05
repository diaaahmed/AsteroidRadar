package com.udacity.asteroidradar.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidsDatabaseDao
{
    // Here we get all asteroids
    @Query("SELECT * from asteroids ORDER BY closeApproachDate DESC")
    fun getAsteroids():LiveData<List<AsteroidEntity>>

    // Insert all asteroids
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroidEntity: AsteroidEntity)

    // Get asteroid date
    @Query("SELECT * from asteroids where closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate DESC")
    fun getAsteroidDate(startDate:String, endDate:String):LiveData<List<AsteroidEntity>>

    // Get asteroid day
    @Query("SELECT * from asteroids where closeApproachDate = :startDate ORDER BY closeApproachDate DESC ")
    fun getAsteroidDay(startDate:String):LiveData<List<AsteroidEntity>>

}

@Database(entities = [AsteroidEntity::class], version = 1, exportSchema = false)
abstract class AsteroidsDatabase: RoomDatabase()
{
    abstract val asteroidDatabaseDao:AsteroidsDatabaseDao

}
private lateinit var INSTANCE:AsteroidsDatabase

fun getInstance(context: Context): AsteroidsDatabase
{
    synchronized(AsteroidsDatabase::class.java)
    {
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }

    return INSTANCE
}