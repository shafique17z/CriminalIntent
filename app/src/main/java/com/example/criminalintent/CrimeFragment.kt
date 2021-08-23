package com.example.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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



//First fragment to be displayed on Activity for adding crime details ..
//To turn any class into fragment extend it from the Fragment() class. That simple.
class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {



    //Instance of @Crime model/data class.
    private lateinit var crime: Crime
    /**The values in this [crime] property represent the edits the user is currently making.*/

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var crimeSolvedCheckBox: CheckBox

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
        dateButton = view.findViewById(R.id.crime_date_button)
        crimeSolvedCheckBox = view.findViewById(R.id.crime_solved_checkbox) //as CheckBox

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
        val titleWatcher = object : TextWatcher /* object : TextWatcher means we're creating anonymous class. */ {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                // This space intentionally left blank
                }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
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
            setOnCheckedChangeListener{ _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener{
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE_TAG)
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