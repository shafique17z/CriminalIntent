package com.example.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import com.example.criminalintent.database.CrimeDatabase
import java.util.*

/**Why did we create a singleton? Because to access the data from a source or set of sources.
 *
 * CrimeRepository is a singleton. This means there will only ever be one instance of it in your app process.
 * Why create a singleton? Because singleton can exist as long as the app stays in memory so it's a good place
 * to save properties..
 *
 * To make CrimeRepository a singleton, you add two functions to its companion object.
 * One initializes a new instance of the repository and
 * The other accesses the repository.
 *
 * Also Constructor is private to ensure no components can create their own instance
 *
 * */

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {

    //Creating @database property to store reference to @CrimeDatabase
    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
        ).build()

    //Creating @crimeDao property to store reference to @DAO objects.
    private val crimeDao = database.crimeDao()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        //This function initializes a new instance of the repository.
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        //This function accesses the repository.
        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

}