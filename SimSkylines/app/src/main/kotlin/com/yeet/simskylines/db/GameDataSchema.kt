package com.yeet.simskylines.db

/**
 * This object contains the schema for the entire GameData
 * excluding the schema for Settings which is kept separately
 * @see SettingsSchema
 */
object GameDataSchema
{
    /**
     * Represents the table used to stored all the MapElements
     */
    object MapElementTable
    {
        const val NAME = "mapelement"
        const val COLUMN_NAME_ROW = "row"
        const val COLUMN_NAME_COLUMN = "column"
        const val COLUMN_NAME_IS_BUILDABLE = "buildable"
        const val COLUMN_NAME_NORTH_WEST = "northwest"
        const val COLUMN_NAME_NORTH_EAST = "northeast"
        const val COLUMN_NAME_SOUTH_WEST = "southwest"
        const val COLUMN_NAME_SOUTH_EAST = "southeast"
        const val COLUMN_NAME_OWNER = "owner"
        const val COLUMN_NAME_STRUCTURE = "structure" // Linked to ID from Structure
    }

    /**
     * Single Row Table used to store all the information
     */
    object InformationTable
    {
        const val NAME = "information"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_RESIDENTIAL = "res"
        const val COLUMN_NAME_COMMERCIAL = "comm"
        const val COLUMN_NAME_MONEY = "money"
        const val COLUMN_NAME_GAME_TIME = "game_time"
        const val COLUMN_NAME_LAST_INCOME = "last_income"
    }
}