package com.yeet.simskylines.model

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.yeet.simskylines.db.SettingsCursor
import com.yeet.simskylines.db.SettingsSchema.SettingsTable

/**
 * Singleton based class to store all the settings information
 * and flush to database when required
 * Uses a hashmap to keep the settings in memory for both storage and RecyclerView
 */
class Settings private constructor()
{
    /**
     * A class representing a single Setting used by the cursor
     * @see SettingsCursor
     */
    class Setting constructor(val id: Int, val value: Int)

    companion object {
        private val TAG = this::class.java.name
        /**
         * Key used for HashMap and for RecyclerView
         */
        const val MAP_WIDTH = 0
        const val MAP_HEIGHT = 1
        const val INITIAL_MONEY = 2
        const val FAMILY_SIZE = 3
        const val SHOP_SIZE = 4
        const val SALARY = 5
        const val SERVICE_COST = 6
        const val HOUSE_COST = 7
        const val COMM_COST = 8
        const val ROAD_COST = 9

        /**
         * Constant default values set
         */
        const val mapWidth = 50
        const val mapHeight = 10
        const val initialMoney = 1000
        const val familySize = 4
        const val shopSize = 6
        const val salary = 10
        const val serviceCost = 2
        const val houseBuildingCost = 100
        const val commBuildingCost = 500
        const val roadBuildingCost = 20
        // Tax Rate isn't loaded into HashMap
        // It's used as a constant instead
        const val TAX_RATE = 0.3

        /**
         * Array of Defaults used to loop through and put into database initially
         */
        private val DEFAULTS = arrayOf(arrayOf(MAP_WIDTH, mapWidth),
                               arrayOf(MAP_HEIGHT, mapHeight),
                               arrayOf(INITIAL_MONEY, initialMoney),
                               arrayOf(FAMILY_SIZE, familySize),
                               arrayOf(SHOP_SIZE, shopSize),
                               arrayOf(SALARY, salary),
                               arrayOf(SERVICE_COST, serviceCost),
                               arrayOf(HOUSE_COST, houseBuildingCost),
                               arrayOf(COMM_COST, commBuildingCost),
                               arrayOf(ROAD_COST, roadBuildingCost))

        private var instance: Settings? = null

        // We don't want multiple instances
        // of settings, that's pointless
        fun getInstance(): Settings {
            if(instance == null) {
                instance = Settings()
                instance?.load()
            }

            return instance!!
        }

        /**
         * Saves the defaults into the database
         * @param db database to save into
         */
        fun saveDefaults(db: SQLiteDatabase) {
            val cursor = SettingsCursor(db.query(SettingsTable.NAME,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null))

            cursor.use {
                if (it.count > 0) instance?.delete()
            }

            for(i in DEFAULTS.indices) {
                val values = ContentValues().apply {
                    put(SettingsTable.COLUMN_NAME_ID, DEFAULTS[i][0])
                    put(SettingsTable.COLUMN_NAME_VALUE, DEFAULTS[i][1])
                }

                db.insert(SettingsTable.NAME, null, values)
            }

            Log.i(TAG, "Saved defaults into database")
        }
    }

    /**
     * HashMap used to store settings in memory
     */
    private var map: MutableMap<Int, Int> = HashMap()
    private var db: SQLiteDatabase = Database.getInstance().getDb()
    var changed = false

    init {
        load()
        changed = checkChange()
    }

    /**
     * Checks if map height or width have been changed from default
     * values, used to decide if the map needs to be regenerated or not
     * @return boolean representing if an important changed has been made
     */
    fun checkChange(): Boolean {
        var ret = false
        if(map[MAP_HEIGHT] != DEFAULTS[MAP_HEIGHT][1]
            || map[MAP_WIDTH] != DEFAULTS[MAP_WIDTH][1]) {
            ret = true
        }

        return ret
    }

    /**
     * Returns the cost corresponding to the structure type
     * @param structure
     * @return cost for structure
     */
    fun getCost(structure: Structure): Int {
        return when(structure) {
            is Residential  -> map[HOUSE_COST]!!
            is Commercial   -> map[COMM_COST]!!
            is Road         -> map[ROAD_COST]!!
            else            -> 0
        }
    }

    /**
     * Returns the string corresponding to the given key
     * @param key
     * @return string for key
     */
    fun getString(key: Int?): String? {
        return when(key) {
            MAP_WIDTH       -> "Map Width"
            MAP_HEIGHT      -> "Map Height"
            INITIAL_MONEY   -> "Initial Money"
            FAMILY_SIZE     -> "Family Size"
            SHOP_SIZE       -> "Shop Size"
            SALARY          -> "Salary"
            SERVICE_COST    -> "Service Cost"
            HOUSE_COST      -> "House Cost"
            COMM_COST       -> "Commercial Cost"
            ROAD_COST       -> "Road Cost"
            else            -> null
        }
    }

    /**
     * Returns the description corresponding to the given key
     * @param key
     * @return description for key
     */
    fun getDescription(key: Int?): String? {
        return when(key) {
            MAP_WIDTH       -> "Total Width of the Map to generate"
            MAP_HEIGHT      -> "Total Height of the Map to generate"
            INITIAL_MONEY   -> "Amount of money player starts with"
            FAMILY_SIZE     -> "Static size of a family"
            SHOP_SIZE       -> "Static size of a shop"
            SALARY          -> "Player's Salary"
            SERVICE_COST    -> "Cost to purchase services"
            HOUSE_COST      -> "Cost to purchase and build houses"
            COMM_COST       -> "Cost to build commercial properties"
            ROAD_COST       -> "Cost to build roads"
            else            -> null
        }
    }

    /**
     * Place a value into the HashMap
     * and set changed variable if needed
     * @param key
     * @param value
     */
    fun put(key: Int, value: Int) {
        if(key == MAP_WIDTH || key == MAP_HEIGHT) {
            if(this.map[key] != value) {
                this.changed = true
            }
        }

        this.map[key] = value
    }

    /**
     * Get the value stored with the given key
     * @param key
     * @return int stored in the map at key
     */
    fun get(key: Int): Int {
        return this.map.getValue(key)
    }

    /**
     * @return size of the current hashmap
     */
    fun size(): Int {
        return this.map.size
    }

    /**
     * Load the settings into running memory
     */
    fun load() {
        Log.i(TAG, "Loading settings into in-memory HashMap")
        val cursor = SettingsCursor(this.db.query(SettingsTable.NAME,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null))

        cursor.use {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                this.map[cursor.getSetting().id] = cursor.getSetting().value
                cursor.moveToNext()
            }
        }
    }

    /**
     * Save the settings into database
     */
    fun save() {
        for(i in 0 until size()) {
            update(DEFAULTS[i][0], map[i]!!)
        }
        Log.i(TAG, "Updated database with settings HashMap data")
    }

    /**
     * Update a given ID with a value in the database
     * @param id
     * @param value
     */
    private fun update(id: Int, value: Int) {
        val whereValue = arrayOf(id.toString())
        val values = ContentValues().apply {
            put(SettingsTable.COLUMN_NAME_ID, id)
            put(SettingsTable.COLUMN_NAME_VALUE, value)
        }

        this.db.update(SettingsTable.NAME, values,
            SettingsTable.COLUMN_NAME_ID + "= ?", whereValue)
    }

    /**
     * Delete all from the settings table in the database
     */
    private fun delete() {
        this.db.execSQL("DELETE FROM ${SettingsTable.NAME}")
    }
}
