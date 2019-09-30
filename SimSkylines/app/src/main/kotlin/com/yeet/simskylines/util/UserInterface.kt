package com.yeet.simskylines.util

import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.yeet.simskylines.R

/**
 * This class contains useful Utils to do with the UserInterface,
 * such as fullscreen and toasts/alerts
 */
object UserInterface
{
    /**
     * Set the given window to fullscreen
     * @param window
     */
    fun enableFullscreen(window: Window) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    /**
     * Set the given window to not fullscreen
     * @param window
     */
    fun disableFullscreen(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * Show an alert with the title and message
     * @param context
     * @param title title of the alert
     * @param message message on the alert
     */
    fun showAlert(context: Context, title: String, message: String) {
        val ad = AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
        }

        ad.show()
    }

    /**
     * Show a toast with the message, in the given context
     * @param context
     * @param message message on the toast
     */
    fun showToast(context: Context, message: String) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.view.apply {
            setBackgroundResource(R.color.grey)
            findViewById<TextView>(android.R.id.message).apply {
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

//        toast.setGravity(Gravity.CENTER, 0, 350)
        toast.show()
    }
}