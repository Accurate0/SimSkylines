package com.yeet.simskylines.ui

import android.os.Bundle
import android.os.StrictMode
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
 * The MainActivity which is launched when the application is launched
 * does some basic tasks with initialising singletons and setups buttons
 */
class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.name
    private var settings: Settings? = null
    private var gameData: GameData? = null
    private var database: Database? = null

    /**
     * overridden method to perform tasks when the activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Cause a crash if a cursor is left unclosed
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UserInterface.enableFullscreen(window)

        // Initialise singletons
        this.database = Database.getInstance()
                                .open(this)

        this.settings = Settings.getInstance()
        this.gameData = GameData.getInstance()

        val startButton: Button = findViewById(R.id.start_game_button)
        val settingsButton: Button = findViewById(R.id.settings_button)
        val resetButton: Button = findViewById(R.id.reset_button)

        settingsButton.setOnClickListener{
            Log.i(TAG, "Starting Settings Activity")
            startActivity(SettingsActivity.getIntent(this))
        }

        startButton.setOnClickListener{
            Log.i(TAG, "Starting Map Activity")
            startActivity(MapActivity.getIntent(this))
        }

        resetButton.setOnClickListener {
            Log.i(TAG, "Resetting and regenerating map")
            ThreadExecutor.exec { gameData?.regenerate() }
            UserInterface.showToast(this, "Regenerating map..")
        }
    }

    /**
     * Enable fullscreen again when the application resumes
     */
    override fun onResume() {
        super.onResume()
        UserInterface.enableFullscreen(window)
    }

    /**
     * Save to database when the main activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        settings?.save()
        gameData?.save()
        gameData?.saveInformation()
    }
}
