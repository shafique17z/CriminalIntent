package com.example.criminalintent

import android.app.Application

/** We subclassed the Application() because we wanted to know the lifecycle of an app.
 *
 *  Application subclass allows you to access lifecycle information about the application itself.
 *
 *  Similar to Activity.onCreate(…), Application.onCreate() is called by the system
 *  when your application is first loaded in to memory.
 *
 *  The application instance does not get constantly destroyed and re-created,
 *  It is created when the app launches and destroyed when your app process is destroyed.
 *
 *  In order for your application class to be used by the system, you need to register it in your manifest.
 */

class CriminalIntentApplication : Application() {

    //override onCreate() to set up the repository initialization.
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}