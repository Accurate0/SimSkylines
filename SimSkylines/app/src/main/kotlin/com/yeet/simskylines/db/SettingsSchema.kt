package com.yeet.simskylines.db

/**
 * Represents the schema used for the Settings table
 * @see SettingsCursor
 */
object SettingsSchema
{
    object SettingsTable
    {
        const val NAME = "settings"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_VALUE = "value"
    }
}
