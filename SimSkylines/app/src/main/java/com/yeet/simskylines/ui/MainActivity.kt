package com.yeet.simskylines.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yeet.simskylines.R
import com.yeet.simskylines.util.Utils

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.yeet.simskylines.R.layout.activity_main)
        Utils.setFullscreen(window)

        val startButton = findViewById<Button>(R.id.start_game_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        settingsButton.setOnClickListener{
            Log.i(TAG, "Starting Settings Activity")
            startActivity(SettingsActivity.getIntent(this))
        }

        startButton.setOnClickListener{
            Log.i(TAG, "Starting Map Activity")
            startActivity(MapActivity.getIntent(this))
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setFullscreen(window)
    }
}
