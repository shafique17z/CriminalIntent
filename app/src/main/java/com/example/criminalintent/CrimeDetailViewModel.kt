package com.example.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

/**Now that [CrimeFragment] has the crime ID, it needs to pull the crime object from the database
 * so it can display the crime’s data. Since this requires a database lookup that you don't want to repeat
 * unnecessarily on rotation, we're going to add a [CrimeDetailViewModel] to manage the database query.
 */

class CrimeDetailViewModel : ViewModel() {

    /** [crimeRepository] property stores a handle to the [CrimeRepository] because
     * [CrimeDetailViewModel] will communicate with the repository later on .. */
    private val crimeRepository = CrimeRepository.get()

    /** [crimeIdLiveData] stores the ID of the crime currently displayed (or about to be displayed) by [CrimeFragment]
     *
     * When [CrimeDetailViewModel] is first created, the crime ID is not set. Eventually,
     * [CrimeFragment] will call [CrimeDetailViewModel.loadCrime] to let the ViewModel know which
     * crime it needs to load.
     *
     */
    private val crimeIdLiveData = MutableLiveData<UUID>()

    /**we're explicitly defining type of [crimeLiveData] as [LiveData] bcz it is publicly exposed.
     * Since it is publicly exposed we should make sure it isn't exposed as [MutableLiveData] because
     * [ViewModels] generally never expose [MutableLiveData] */
    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }

    /** Updating the app to write the values the user enters in the crime detail screen to the database.
     *
     * This function talks to the Repository and the [UpdateCrime] function in Repository then talks to the DAO class.
     * The DAO then talks to the underlying database to save crime with annotation @Update.
     *
     */
    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }
}