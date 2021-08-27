package com.example.criminalintent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

//Data class to hold data for each crime w/ properties, ID, title, date, isSolved.

/**@Entity annotation indicates that this class defines the structure of a table.
 *
 * Each property defined on the class will be a column in the table. *property name == column name.*
 * This table will have 4 columns as indicated by properties. id, title, date, and isSolved.
 *
 * @PrimaryKey annotation specifies which column in your database is the primary key.
 * */
@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var suspect: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
) {

    /** A computed property to get a well-known filename.
     */
    val photoFileName
        get() = "IMG_$id.jpg"
}