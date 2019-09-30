package com.yeet.simskylines.db

import android.database.Cursor
import android.database.CursorWrapper
import com.yeet.simskylines.model.Settings
import com.yeet.simskylines.db.SettingsSchema.SettingsTable

/**
 * Cursor class for Settings
 * @see MapElementCursor
 */
class SettingsCursor constructor(cursor: Cursor): CursorWrapper(cursor)
{
    fun getSetting(): Settings.Setting {
        val id = getInt(getColumnIndex(SettingsTable.COLUMN_NAME_ID))
        val value = getInt(getColumnIndex(SettingsTable.COLUMN_NAME_VALUE))

        return Settings.Setting(id, value)
    }
}