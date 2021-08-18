package com.example.criminalintent

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

//CrimeListFragment will display the list of crimes on the screen.
class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null


    //Associating @CrimeListFragment with @CrimeListViewModel.
    private val crimeListViewModel: CrimeListViewModel by lazy /* by lazy: makes the crimeListViewModel val instead of var */ {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    //A RecyclerView never creates Views by themselves. It always creates ViewHolders, which bring their
    //itemViews(lists) along for the ride
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        //A property to store the Crime being bound.
        private lateinit var crime: Crime
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved_img)

        //Updating CrimeHolder to find the title and date text views in itemView’s hierarchy
        // when an instance is first created.
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title_textview)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date_textview)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.format("EEEE, MMM dd, yyyy", this.crime.date)
            solvedImageView.visibility = if(crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        //onCreateViewHolder(…) is responsible for creating a view to display
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        //onBindViewHolder(..) is responsible for populating a given holder with the crime from a given position.
        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            //Getting the crime from the crime list at the requested position.
            val crime = crimes[position]

            //We shifted all the real work to CrimeHolder because,
            //It is recommended for the adapter to know as little as possible about the inner
            //workings and details of the view holder.
            holder.bind(crime)
        }

        //When the recycler view needs to know how many items are in the data set,
        //it will ask its adapter by calling Adapter.getItemCount().
        override fun getItemCount(): Int {
            return crimes.size
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Logging the number of crimes found in CrimeListViewModel.
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    //Using newInstance(…) function so activities can call to get an instance of your fragment.
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView

        //RecyclerView requires a layout manager to work so we're giving it LinearLayoutManager.
        //The LayoutManager positions every item and also defines how scrolling works
        // because the RecyclerView doesn't do anything, it delegates the job to LayoutManager.
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        //This will create a CrimeAdapter and set it on the RecyclerView.
        updateUI()

        return view
    }

    //Now that you have an Adapter, connect it to your RecyclerView.
    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }
}
