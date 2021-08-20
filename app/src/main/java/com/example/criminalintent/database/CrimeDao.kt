package com.example.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.criminalintent.Crime
import java.util.*

/**To interact with DB tables you need a data access object or DAO.
 * A DAO is an interface that contains functions for each database operation you want to perform.
 *
 * @Dao annotation lets Room know that CrimeDao is one of your data access objects.
 *
 * When you hook CrimeDao up to your database class, Room will generate implementations of these functions.
 * */

@Dao
interface CrimeDao {

    /**The @Query annotation indicates that getCrimes() and getCrime(UUID) are meant to pull information
     * out of the database.
     * */

    //In this chapter CrimeDao needs two functions.
    //One to return a list of all crimes in the db.
    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>
     /** The return type of this query function in this DAO interface reflects the type of result the query will return.
      *
     * When we set queries to return LiveData object, Room will automatically execute background threads for us and
     * then Room will publish the results to LiveData object when the query is done.
     */

    //The other to return a single crime matching a given id.
    @Query("SELECT * FROM crime WHERE id = (:id)")
    fun getCrime(id: UUID): LiveData<Crime?>
    /** The return type of this query function in this DAO interface reflects
     * the type of result the query will return.
     */
}