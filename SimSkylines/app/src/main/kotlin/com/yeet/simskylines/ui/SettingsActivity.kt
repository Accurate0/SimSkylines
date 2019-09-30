package com.yeet.simskylines.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yeet.simskylines.R
import com.yeet.simskylines.model.Database
import com.yeet.simskylines.model.GameData
import com.yeet.simskylines.model.Settings
import com.yeet.simskylines.util.ThreadExecutor
import com.yeet.simskylines.util.UserInterface

/**
 * The activity that holds the SettingsFragment
 */
class SettingsActivity : AppCompatActivity()
{
    private val TAG = this::class.java.name
    private val settings = Settings.getInstance()

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    /**
     * When the activity is created, load the fragments
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val fm = supportFragmentManager
        var settings: SettingsFragment? = fm.findFragmentById(R.id.settings) as SettingsFragment?

        if(settings == null) {
            Log.i(TAG, "Initialising settings fragment")
            settings = SettingsFragment()
            fm.beginTransaction()
                .add(R.id.settings, settings)
                .commit()
        }

        findViewById<Button>(R.id.settings_reset_button).apply {
            setOnClickListener {
                UserInterface.showToast(context!!, "Resetting Settings..")

                Settings.saveDefaults(Database.getInstance().getDb())
                Settings.getInstance().load()
                settings.reload()
            }
        }
    }

    /**
     * When the settings activity stops, regenerate the map and save the settings
     */
    override fun onStop() {
        super.onStop()
        UserInterface.showToast(applicationContext, "Regenerating Map..")
        ThreadExecutor.exec {
            settings.save()
            GameData.getInstance().regenerate()
        }
    }
}