package com.example.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.ViewCompat.jumpDrawablesToCurrentState
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import java.util.*

//New things not commented yet till TAG..
private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE_TAG = "DatePickerFragment"
private const val REQUEST_DATE = 0
private const val REQUEST_CONTACT = 1
private const val DATE_FORMAT = "EEE, MMM, dd"


//First fragment to be displayed on Activity for adding crime details ..
//To turn any class into fragment extend it from the Fragment() class. That simple.
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {

    //Instance of @Crime model/data class.
    private lateinit var crime: Crime

    /**The values in this [crime] property represent the edits the user is currently making.*/

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var crimeSolvedCheckBox: CheckBox
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //You configure/create the fragment instance in Fragment.onCreate(Bundle?)
        crime = Crime()

        //New thing not commented yet Till Log.d statement..
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
        // Eventually, load crime from database
    }

    //This fragment lifecycle callback is used to inflate the fragment's layout.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        //Wiring up views with View.findViewById().
        //The fragment class doesn't have a findViewById so you've to call it from the View class' object i.e @view here.
        titleField = view.findViewById(R.id.crime_title_edit_text)
        dateButton = view.findViewById(R.id.crime_date_button) //as Button
        crimeSolvedCheckBox = view.findViewById(R.id.crime_solved_checkbox) //as CheckBox
        reportButton = view.findViewById(R.id.crime_report_button) //as Button
        suspectButton = view.findViewById(R.id.choose_suspect_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher =
            object : TextWatcher /* object : TextWatcher means we're creating anonymous class. */ {
                override fun beforeTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // This space intentionally left blank
                }

                override fun onTextChanged(
                    sequence: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    /* sequence is the user input*/
                    crime.title = sequence.toString() /* get the user input in CharSequence type and then cast to String using toString().
                And then store it in title property of crime model class i.e set the crime's title */
                }

                override fun afterTextChanged(sequence: Editable?) {
                    // This one too
                }
            }
        titleField.addTextChangedListener(titleWatcher)

        crimeSolvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE_TAG)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

            //Guarding against if there's no contact app to some particular user ..

            /**If this search is successful, it will return an instance of ResolveInfo telling you all about which
            * activity it found. On the other hand, if the search returns null, the game is up – no contacts app. So
            * you disable the useless CHOOSE SUSPECT button. */
            //pickContactIntent.addCategory(Intent.CATEGORY_HOME)
            //This category (above commented line) does nothing, but it will prevent any contacts applications from matching your intent.
            val packageManager: PackageManager = requireActivity().packageManager
            /** Calling resolveActivity(Intent, Int) to find an activity that matches the Intent we gave it.
             * The MATCH_DEFAULT_ONLY flag restricts this search to activities with the CATEGORY_DEFAULT flag. */
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

        }


    }

    /** onStop() is called any time your fragment moves entirely out of view or user navigates away from fragment..
     * That's why we're saving any writes the user enters so we can update the database and
     * the title of the crime in CrimeListFragment ..
     *
     */

    override fun onStop() {
        super.onStop()

        //Saving the user’s edited crime data to the database.
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        crimeSolvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
    }


    /** Because you started the activity for a result with [ACTION_PICK], you will receive an intent via
     * [onActivityResult]. This intent includes a data URI. The URI is a locator that points at the single
     * contact the user picked. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        /** Creating a query that asks for all the display names of the contacts in the returned data.
         * Then quering the contacts database and get a Cursor object to work with. Once you verify that
         * the cursor returned contains at least one row, you call Cursor.moveToFirst() to move the cursor to the
         * first row. Finally, you call Cursor.getString(Int) to pull the contents of the first column in that first
         * row as a string. This string will be the name of the suspect, and you use it to set the Crime’s suspect
         * and the text of the CHOOSE SUSPECT button.
         */

        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                // Specify which fields you want your query to return values for
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                // Perform your query - the contactUri is like a "where" clause here
                val cursor = requireActivity().contentResolver
                    .query(contactUri!!, queryFields, null, null, null)
                cursor?.use {
                    // Verify cursor contains at least one result
                    if (it.count == 0) {
                        return
                    }
                    // Pull out the first column of the first row of data -
                    // that is your suspect's name
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect

                }

            }
        }
    }


    /**  A function that creates four strings and then pieces them together and
     * returns a complete report. */
    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspect
        )
    }

    //New thing not commented yet till end of object block..
    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }

}