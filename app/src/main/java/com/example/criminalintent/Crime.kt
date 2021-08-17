package com.example.criminalintent

import java.util.Date
import java.util.UUID

//Data class to hold data for each crime w/ properties, ID, title, date, isSolved.
data class Crime(
    val id: UUID = UUID.randomUUID(),
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false) {
}