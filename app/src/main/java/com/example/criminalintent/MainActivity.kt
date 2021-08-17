package com.example.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//MainActivity was first hosting the CrimeFragment for adding details of Crime.
//Now, MainActivity will host CrimeListFragment to display lists of Crimes ..

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //HOSTING A FRAGMENT IN MAINACTIVITY'S VIEW HEIRARCHY.

        /** When Fragments were introduced, FragmentManager was also added in an activity class to manage Fragments.
         * FragmentManager is responsible for adding/attaching/detaching/replacing/removing the fragments’ views
         * to the activity’s view hierarchy and driving the fragments’ lifecycles.
         * You can access the activity’s FragmentManager using the supportFragmentManager property
         */


        //When you need to retrieve the CrimeFragment (or any fragment) from the FragmentManager, you ask for it by container view ID.
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        /** Asking the FragmentManager for the fragment with a container view ID of R.id.fragment_container.
         * If this fragment is already in the list, the FragmentManager will return it.
         * If there's no fragment in the list then creating one below ...
         */


        if (currentFragment == null) {

            val fragment = CrimeListFragment.newInstance()

            /* The following code till .commit() means;
            Create a new fragment transaction, include one add operation in it, and then commit it. */
            supportFragmentManager /* You can access the activity’s fragment manager using the supportFragmentManager property. */

                .beginTransaction() /* This function creates and returns an instance of FragmentTransaction. */

                .add(R.id.fragment_container, fragment) /** The add() function has two parameters, one for container where the fragment will be held
                                                         * and the fragment that will be sent to container to be held.
                                                         * A container view id serves 2 purposes.
                                                         * 1- Activity ke view heirarchy main fragment ka view kahan appear ho, tells the FragmentManager.
                                                         * 2- Aur agar Fragments ki list bann jaye tou each fragment ko uniquely identify karte hain
                                                         * iss id ka use karke.
                                                         */
                .commit()
        }
    }
}