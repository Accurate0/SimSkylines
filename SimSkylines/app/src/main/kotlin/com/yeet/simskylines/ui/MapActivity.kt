package com.yeet.simskylines.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yeet.simskylines.R
import com.yeet.simskylines.model.GameData
import com.yeet.simskylines.model.Settings
import com.yeet.simskylines.util.ThreadExecutor
import com.yeet.simskylines.util.UserInterface

class MapActivity : AppCompatActivity()
{
    private val TAG = this::class.java.name
    private var gameData: GameData? = null
    private var settings: Settings? = null

    companion object {
        /**
         * Return an intent for this class
         * @param context
         * @return Intent for current class
         */
        fun getIntent(context: Context): Intent {
            return Intent(context, MapActivity::class.java)
        }
    }

    /**
     * setup MapActivity and create/find required fragments
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        UserInterface.enableFullscreen(window)

        settings = Settings.getInstance()
        gameData = GameData.getInstance()

        val fm = supportFragmentManager

        var mapFragment = fm.findFragmentById(R.id.map_area)
        var selectorFragment = fm.findFragmentById(R.id.selector_area)
        var informationFragment = fm.findFragmentById(R.id.information_area)

        if(mapFragment == null || selectorFragment == null || informationFragment == null) {
            mapFragment = MapFragment()
            selectorFragment = SelectorFragment()
            informationFragment = InformationFragment()

            mapFragment.selectorFragment = selectorFragment
            mapFragment.informationFragment = informationFragment

            fm.beginTransaction()
                .add(R.id.map_area, mapFragment)
                .add(R.id.selector_area, selectorFragment)
                .add(R.id.information_area, informationFragment)
                .commit()
        }
    }

    /**
     * Save to database when the activity comes to a stop
     */
    override fun onStop() {
        super.onStop()
        ThreadExecutor.exec {
            GameData.getInstance().save()
            GameData.getInstance().saveInformation()
        }
    }

    /**
     * Reset to fullscreen when an alert or similar occurs
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        UserInterface.enableFullscreen(window)
    }

    /**
     * Reset to full screen when the activity resumes
     */
    override fun onResume() {
        super.onResume()
        UserInterface.enableFullscreen(window)
    }

//    /**
//     * Checks the settings class for changes, and regens
//     * gameData if needed
//     */
//    private fun regenIfNeeded() {
//        settings?.let {
//            if(it.checkChange()) {
//                gameData?.regenerate()
//                UserInterface.showToast(this, "A map setting was changed, map regenerated")
//            }
//        }
//    }
}