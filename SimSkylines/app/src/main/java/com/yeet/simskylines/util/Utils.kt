package com.yeet.simskylines.util

import android.view.View
import android.view.Window

class Utils
{
    companion object {
        fun setFullscreen(window: Window) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}