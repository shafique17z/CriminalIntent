package com.example.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.Crime

/**@Database annotation tells Room that this class represents a database in your app.
 *
 * This annotation requires 2 parameters;
 * 1. List of entities, tells Room which class to use when creating and managing tables for this databse.
 * 2. When you modify your entity, you also need to increment version of db to tell Room sth has changed.
 *
 * @CrimeDatabse class is marked abstract because we can't make an instance of it directly.
 * */

@Database(entities = [Crime::class], version = 2, exportSchema = false)

@TypeConverters(CrimeTypeConverters::class)
//Telling database to use the functions in that class when converting your types.

abstract class CrimeDatabase : RoomDatabase() {

    //Function to generate an instance of the DAO to let Room generate the concrete version of the class for us.
    abstract fun crimeDao(): CrimeDao

}

//To tell Room how to change your database from one version to another, you provide a Migration.
val migration_1_2 = object : Migration(1, 2) /**Create Migration() object */ {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }
}

