package com.example.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criminalintent.Crime

/**@Database annotation tells Room that this class represents a database in your app.
 *
 * This annotation requires 2 parameters;
 * 1. List of entities, tells Room which class to use when creating and managing tables for this databse.
 * 2. When you modify your entity, you also need to increment version of db to tell Room sth has changed.
 *
 * @CrimeDatabse class is marked abstract because we can't make an instance of it directly.
 * */

@Database(entities = [Crime::class], version = 1)

@TypeConverters(CrimeTypeConverters::class)
//Telling database to use the functions in that class when converting your types.

abstract class CrimeDatabase : RoomDatabase() {

    //Function to generate an instance of the DAO to let Room generate the concrete version of the class for us.
    abstract fun crimeDao(): CrimeDao
}