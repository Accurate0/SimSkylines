package com.yeet.simskylines.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yeet.simskylines.R
import com.yeet.simskylines.util.Utils

class MapActivity : AppCompatActivity()
{
    private val TAG = this::class.java.name

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MapActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        Utils.setFullscreen(window)

        val fm = supportFragmentManager

        var mapFragment = fm.findFragmentById(R.id.map_area)
        var selectorFragment = fm.findFragmentById(R.id.selector_area)

        if(mapFragment == null || selectorFragment == null) {
            mapFragment = MapFragment()
            fm.beginTransaction()
                .add(R.id.map_area, mapFragment)
                .commit()

            selectorFragment = SelectorFragment()
            fm.beginTransaction()
                .add(R.id.selector_area, selectorFragment)
                .commit()

            mapFragment.selectorFragment = selectorFragment
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setFullscreen(window)
    }
}