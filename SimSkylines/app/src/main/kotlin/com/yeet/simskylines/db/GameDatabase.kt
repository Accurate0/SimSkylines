package com.yeet.simskylines.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yeet.simskylines.db.GameDataSchema.InformationTable
import com.yeet.simskylines.db.GameDataSchema.MapElementTable
import com.yeet.simskylines.db.SettingsSchema.SettingsTable
import com.yeet.simskylines.model.Settings

/**
 * This class extends SQLiteOpenHelper, and contains all the code for
 * creating the database and it's tables
 */
class GameDatabase constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        private const val DATABASE_NAME = "game.db"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_SETTINGS =
            "CREATE TABLE ${SettingsTable.NAME} (" +
                    "${SettingsTable.COLUMN_NAME_ID} INTEGER PRIMARY KEY," +
                    "${SettingsTable.COLUMN_NAME_VALUE} INTEGER)"

        private const val SQL_CREATE_MAP_ELEMENT =
            "CREATE TABLE ${MapElementTable.NAME} (" +
                    "${MapElementTable.COLUMN_NAME_ROW} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_COLUMN} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_IS_BUILDABLE} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_NORTH_EAST} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_NORTH_WEST} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_SOUTH_EAST} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_SOUTH_WEST} INTEGER," +
                    "${MapElementTable.COLUMN_NAME_OWNER} TEXT," +
                    "${MapElementTable.COLUMN_NAME_STRUCTURE} INTEGER," +
                    "PRIMARY KEY (${MapElementTable.COLUMN_NAME_ROW}, ${MapElementTable.COLUMN_NAME_COLUMN}))"


        private const val SQL_CREATE_INFORMATION =
            "CREATE TABLE ${InformationTable.NAME} (" +
                    "${InformationTable.COLUMN_NAME_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${InformationTable.COLUMN_NAME_COMMERCIAL} INTEGER," +
                    "${InformationTable.COLUMN_NAME_GAME_TIME} INTEGER," +
                    "${InformationTable.COLUMN_NAME_LAST_INCOME} REAL," +
                    "${InformationTable.COLUMN_NAME_MONEY} REAL," +
                    "${InformationTable.COLUMN_NAME_RESIDENTIAL} INTEGER)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.let {
            it.execSQL(SQL_CREATE_SETTINGS)
            it.execSQL(SQL_CREATE_MAP_ELEMENT)
            it.execSQL(SQL_CREATE_INFORMATION)
            // Write the defaults into the db when loaded
            Settings.saveDefaults(db)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        throw UnsupportedOperationException("Database upgrade not supported")
    }
}