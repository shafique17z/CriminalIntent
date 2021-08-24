package com.example.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Database
import androidx.room.Room
import com.example.criminalintent.database.CrimeDatabase
import com.example.criminalintent.database.migration_1_2
import java.util.*
import java.util.concurrent.Executors

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
        DATABASE_NAME)
        .addMigrations(migration_1_2)
        .build()

    /** After you create your Migration, you need to provide it to your database when it is created so Room can know
     * about Migrations */


    //Creating @crimeDao property to store reference to @DAO objects.
    private val crimeDao = database.crimeDao()

    /** ABOUT EXECUTORS.. they're also defined in on [CrimeDao] very vividly..
     * An Executor is an object that references a thread. An executor instance has a function called
     * execute that accepts a block of code to run.
     * The code you provide in the block will run on whatever thread the executor points to.
     *
     * Executors are not implemented in the DAO classes but instead in a Repository ..
     */

    //Holding a reference to Executors which always does the work in new thread(always bg thread).
    private val executor = Executors.newSingleThreadExecutor()
    //The newSingleThreadExecutor() function returns an executor instance that points to a new thread.

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    //
    fun updateCrime(crime: Crime) {
        executor.execute /*execute func pushes these operations to new thread so the UI isn't blocked .. */ {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute /*execute func pushes these operations to new thread so the UI isn't blocked .. */ {
            crimeDao.addCrime(crime)
        }
    }

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