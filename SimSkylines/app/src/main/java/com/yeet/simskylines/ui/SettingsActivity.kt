package com.yeet.simskylines.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yeet.simskylines.R

class SettingsActivity : AppCompatActivity()
{
    private val TAG = this::class.java.name

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val fm = supportFragmentManager
        var settings = fm.findFragmentById(R.id.settings)

        if(settings == null) {
            Log.i(TAG, "Initialising settings fragment")
            settings = SettingsFragment()
            fm.beginTransaction()
                .add(R.id.settings, settings)
                .commit()
        }
    }
}