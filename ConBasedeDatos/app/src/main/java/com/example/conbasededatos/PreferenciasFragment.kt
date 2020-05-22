package com.example.conbasededatos


import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class PreferenciasFragment : PreferenceFragmentCompat() { //Deprecated
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        //super.onCreate(savedInstanceState)
       setPreferencesFromResource(R.xml.preferencias, rootKey)
    }
}