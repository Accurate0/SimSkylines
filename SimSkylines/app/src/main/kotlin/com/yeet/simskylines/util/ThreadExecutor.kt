package com.yeet.simskylines.util

import java.util.concurrent.Executors

/**
 * Uses java methods to execute things on a separate thread to prevent
 * the UI from freezing on large database operations, and to stop Android
 * from complaining about it
 */
object ThreadExecutor
{
    private val executor = Executors.newSingleThreadExecutor()

    fun exec(f: () -> Unit) {
        executor.execute(f)
    }
}