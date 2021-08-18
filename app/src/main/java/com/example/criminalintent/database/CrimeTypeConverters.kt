package com.example.criminalintent.database

import androidx.room.TypeConverter
import java.util.*

/** @Crime class relies on the Date and UUID objects, which Room does not know how to store by default.
 * To tell Room how to convert your data types, you specify a type converter.
 * @TypeConverter tells Room how to convert a specific type to the format it needs to store in the database/
 * You'll need as many functions as there are types that Room or DBs don't understand.. (we need 2 for now..)
 * One tells Room how to convert the type to store it in the database, The other tells Room
 * how to convert from the database representation back to the original type.
 * */

class CrimeTypeConverters {

    //The first two functions handle the Date object
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    //The other two handle the UUIDs
    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}