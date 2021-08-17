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
import androidx.fragment.app.Fragment

//First fragment to be displayed on Activity for adding crime details ..
//To turn any class into fragment extend it from the Fragment() class. That simple.
class CrimeFragment : Fragment() {

    //Instance of @Crime model/data class.
    private lateinit var crime: Crime

    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var crimeSolvedCheckBox: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //You configure/create the fragment instance in Fragment.onCreate(Bundle?)
        crime = Crime()
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

        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        return view
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

    }

}