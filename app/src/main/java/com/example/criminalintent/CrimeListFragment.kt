package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"

//CrimeListFragment will display the list of crimes on the screen.
class CrimeListFragment : Fragment() {

    /**
     * Required interface for hosting activities to get the functionality of fragments.
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    //A callbacks property to hold an object that implements Callbacks.
    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    //Associating @CrimeListFragment with @CrimeListViewModel.
    private val crimeListViewModel: CrimeListViewModel by lazy /* by lazy: makes the crimeListViewModel val instead of var */ {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    /**The Fragment.onAttach(Context) lifecycle function is called
     * when a fragment is attached to an activity.
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)

        //Here we're storing context argument in callback property ..
        callbacks = context as Callbacks?
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
            callbacks?.onCrimeSelected(crime.id)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }


    /**Implementing onOptionsItemSelected(MenuItem) to respond to MenuItem selection by creating a new Crime,
     * saving it to the database, and then notifying the parent activity that the new crime has been selected.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime_menu -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //I defined this method to let the FragmentManager know to receive a Callback
    // to onCreateOptionsMenu() or menu callbacks
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }

    ////New thing not commented yet till end of onDetach block..
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }


    //Now that you have an Adapter, connect it to your RecyclerView.
    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }
}
