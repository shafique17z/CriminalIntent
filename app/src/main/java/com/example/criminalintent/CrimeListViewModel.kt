package com.example.criminalintent

import androidx.lifecycle.ViewModel

//CrimeListViewModel will store a list of Crime objects.
class CrimeListViewModel : ViewModel() {

    //Add a property to store a list of Crimes.
    val crimes = mutableListOf<Crime>()

    //Populating the list with dummy data.
    init {
        for (i in 0 until 100) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i%2 == 0
            crimes += crime //Equivalent to crimes = crimes + crime.
        }
    }
}