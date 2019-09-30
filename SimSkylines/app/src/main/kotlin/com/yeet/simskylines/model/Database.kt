package com.yeet.simskylines.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.yeet.simskylines.db.GameDatabase

/**
 * Singleton class used to keep an open connection to the SQLite database
 */
class Database private constructor()
{
    companion object
    {
        private var instance: Database? = null

        fun getInstance(): Database {
            if(instance == null) {
                instance = Database()
            }

            return instance!!
        }
    }

    private var db: SQLiteDatabase? = null

    /**
     * Opens the database using the given context
     * @param context context to use to open the database
     * @return returns the current database to allow chaining
     */
    fun open(context: Context): Database {
        if(this.db == null) {
            this.db = GameDatabase(context.applicationContext).writableDatabase
        }

        return this
    }

    /**
     * @return the currently opened SQLite database
     */
    fun getDb(): SQLiteDatabase {
        return this.db!!
    }

    /**
     * Close the current SQLite database
     */
    fun close() {
        this.db?.close()
    }
}