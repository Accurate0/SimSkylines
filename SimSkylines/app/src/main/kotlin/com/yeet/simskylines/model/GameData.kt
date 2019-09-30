package com.yeet.simskylines.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.yeet.simskylines.db.GameDataSchema.InformationTable
import com.yeet.simskylines.db.GameDataSchema.MapElementTable
import com.yeet.simskylines.db.MapElementCursor
import com.yeet.simskylines.util.MapCreator
import com.yeet.simskylines.util.ThreadExecutor
import com.yeet.simskylines.util.UserInterface
import kotlin.math.min

/**
 * Class used to represent all the current GameData including the information
 * Also a singleton to make it easier to access and prevent multiple instances
 * It also uses the database to store itself into tables
 * @see GameData
 * @see Database
 * @see GameDataSchema
 */
class GameData private constructor(inGrid: Array<Array<MapElement?>>?) {
    companion object {
        private var instance: GameData? = null

        fun getInstance(): GameData {
            if(instance == null) {
                instance = GameData()
                ThreadExecutor.exec {
                    instance?.load()
                    instance?.loadInformation()
                }
            }

            return instance!!
        }
    }

    /**
     * Get an instance of settings
     */
    private val settings = Settings.getInstance()
    private val TAG = this::class.java.name

    private var grid: Array<Array<MapElement?>>? = null
    private var db: SQLiteDatabase = Database.getInstance().getDb()

    /**
     * Class fields for the current game information
     */
    var nResidential = 0
    var nCommercial = 0
    var money = settings.get(Settings.INITIAL_MONEY).toDouble()
        private set
    var gameTime = 0
        private set
    var lastIncome = 0.0
        private set
    var population = 0
        private set
    var employmentRate = 0.0
        private set

    private constructor() : this(null)

    init {
        this.grid = inGrid
    }

    /**
     * Regenerate the current map grid, delete the information from the current tables,
     * and save everything back into the table
     */
    fun regenerate() {
        // This method is called when a map related setting is changed
        // which leads to the creation of a new map
        // and replacing all data in the database
        this.grid = MapCreator.generateGrid(settings.get(Settings.MAP_HEIGHT),
                                            settings.get(Settings.MAP_WIDTH))
        Log.i(TAG, "Map generated")
        // Delete all data in current table
        delete()
        resetFields()
        save()
        saveInformation()
        Log.i(TAG, "New map saved to database")
    }

    /**
     * Reset all information tracking fields
     */
    private fun resetFields() {
        this.nResidential = 0
        this.nCommercial = 0
        this.money = settings.get(Settings.INITIAL_MONEY).toDouble()
        this.gameTime = 0
        this.lastIncome = 0.0
        this.population = 0
        this.employmentRate = 0.0
    }

    /**
     * Operator/function return the MapElement at the specified location
     * @param i
     * @param j
     * @return MapElement at i and j
     */
    operator fun get(i: Int, j: Int): MapElement {
        return grid!![i][j]!!
    }

    /**
     * Increase the current game time and do all required
     * calculations
     * @param context the current context of the application
     */
    fun increaseGameTime(context: Context) {
        val last = population * (employmentRate * settings.get(Settings.SALARY) *
                    Settings.TAX_RATE - settings.get(Settings.SERVICE_COST))


        if(checkMoneyAlert(context, last)) {
            this.gameTime++
            this.money += last
            this.lastIncome = last
        }
        refreshData()
        ThreadExecutor.exec { saveInformation() }
    }

    /**
     * Check current money, use context to create an alert if required
     * @param context to create the alert
     */
    fun checkMoney(context: Context): Boolean {
        var hasMoney = true
        if(this.money <= 0) {
            UserInterface.showAlert(context, "Game Over!", "You lost lmao")
            hasMoney = false
        }

        return hasMoney
    }

    /**
     * Check current money, use context to create an alert if required
     * @param context to create the alert
     * @param diff diff this against the current money before checking
     */
    fun checkMoneyAlert(context: Context, diff: Double): Boolean {
        var hasMoney = true
        if(this.money + diff <= 0) {
            UserInterface.showAlert(context, "Game Over!", "You lost lmao")
            hasMoney = false
        }

        return hasMoney
    }

    /**
     * Check current money, use context to create an alert if required
     * @param context to create the alert
     * @param diff diff this against the current money before checking
     */
    fun checkMoneyToast(context: Context, diff: Double): Boolean {
        var hasMoney = true
        if(this.money + diff <= 0) {
            UserInterface.showToast(context, "You can't afford to build that")
            hasMoney = false
        }

        return hasMoney
    }

    /**
     * 3 building functions for each type of possible structure
     * @see Residential
     * @see Commercial
     * @see Road
     * @see Structure
     */
    fun buildResidential() {
        this.nResidential++
        this.money -= settings.get(Settings.HOUSE_COST)
    }

    fun buildCommercial() {
        this.nCommercial++
        this.money -= settings.get(Settings.COMM_COST)
    }

    fun buildRoad() {
        this.money -= settings.get(Settings.ROAD_COST)
    }

    /**
     * Refresh the current data for population and employment
     */
    fun refreshData() {
        this.population = settings.get(Settings.FAMILY_SIZE) * nResidential
        if(this.population > 0) {
            this.employmentRate = min(1.0, nCommercial *
                    settings.get(Settings.SHOP_SIZE) / population.toDouble())
        }
    }

    /**
     * Check money and then
     * refresh the current data for population and employmeny
     * @param context the context to pass to checkMoney
     * @see checkMoney
     */
    fun refreshData(context: Context) {
        checkMoney(context)
        refreshData()
    }

    /**
     * Overloaded save method which saves a mapElement to a specific row and column
     * into the database
     * @param mapElement MapElement to save
     * @param row row loc
     * @param col col loc
     */
    fun save(mapElement: MapElement, row: Int, col: Int) {
        val structureID = mapElement.structure?.id
        updateMapElement(row, col, mapElement)
        Log.i(TAG, "Saving $row, $col and $structureID to database")
    }

    /**
     * Save the entire grid into the database
     */
    fun save() {
        for(i in 0 until settings.get(Settings.MAP_HEIGHT)) {
            for (j in 0 until settings.get(Settings.MAP_WIDTH)) {
                val me = grid!![i][j]!!
                val cv = ContentValues().apply {
                    var buildable = 0
                    if(me.isBuildable) buildable = 1
                    put(MapElementTable.COLUMN_NAME_ROW, i)
                    put(MapElementTable.COLUMN_NAME_COLUMN, j)
                    put(MapElementTable.COLUMN_NAME_IS_BUILDABLE, buildable)
                    put(MapElementTable.COLUMN_NAME_NORTH_EAST, me.northEast)
                    put(MapElementTable.COLUMN_NAME_NORTH_WEST, me.northWest)
                    put(MapElementTable.COLUMN_NAME_SOUTH_EAST, me.southEast)
                    put(MapElementTable.COLUMN_NAME_SOUTH_WEST, me.southWest)

                    val struct = me.structure

                    if(struct != null) {
                        put(MapElementTable.COLUMN_NAME_STRUCTURE, struct.id)
                    } else {
                        put(MapElementTable.COLUMN_NAME_STRUCTURE, -1)
                    }

                    if(me.owner != null) {
                        put(MapElementTable.COLUMN_NAME_OWNER, me.owner)
                    }
                }

                val ret = db.insertWithOnConflict(MapElementTable.NAME, null,
                                                cv, SQLiteDatabase.CONFLICT_IGNORE).toInt()
                if(ret == -1) {
                    // if its already in the db, update the information instead
                    updateMapElement(i, j, me)
                }
            }
        }
        Log.i(TAG, "Updated database with GameData")
    }

    /**
     * Save Information classfields into the Information table
     */
    fun saveInformation() {
        val cv = ContentValues().apply {
            put(InformationTable.COLUMN_NAME_ID, 1)
            put(InformationTable.COLUMN_NAME_COMMERCIAL, nCommercial)
            put(InformationTable.COLUMN_NAME_RESIDENTIAL, nResidential)
            put(InformationTable.COLUMN_NAME_GAME_TIME, gameTime)
            put(InformationTable.COLUMN_NAME_LAST_INCOME, lastIncome)
            put(InformationTable.COLUMN_NAME_MONEY, money)
        }

        val ret = db.insertWithOnConflict(InformationTable.NAME, null,
            cv, SQLiteDatabase.CONFLICT_IGNORE).toInt()
        if(ret == -1) {
            updateInformation(cv)
        }

        Log.i(TAG, "Updated database with Information")
    }

    /**
     * Load the current database into in-memory array
     * If a database doesn't exist regenerate the entire map and save into it
     */
    fun load() {
        val cursor = MapElementCursor(this.db.query(MapElementTable.NAME,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null))

        Log.d(TAG, "Attempting to load ${cursor.count} rows")

        if(cursor.count > 0) {
            this.grid = Array(settings.get(Settings.MAP_HEIGHT))
                        { arrayOfNulls<MapElement?>(settings.get(Settings.MAP_WIDTH)) }
            cursor.use {
                it.moveToFirst()
                while (!it.isAfterLast) {
                    val data = it.getMapElement()
                    val row = data.row
                    val col = data.col

                    this.grid!![row][col] = MapElement(data.isBuildable,
                                                    data.northWest,
                                                    data.northEast,
                                                    data.southWest,
                                                    data.southEast,
                                                    data.structure,
                                                    null,
                                                    data.owner)
//                    Log.d("${row}:${col}", this.grid!![row][col].toString())
                    it.moveToNext()
                }
            }
            Log.d(TAG, "Loaded map into in memory array")
        } else {
            // If count is 0, means there's nothing in the table
            Log.d(TAG, "Database empty")
            Log.d(TAG, "Regenerating map, and saving into database")
            regenerate()
            cursor.close()
        }
    }

    /**
     * Load the information from the database into current classfields
     */
    fun loadInformation() {
        val infoCursor = this.db.query(InformationTable.NAME,
            null,
            null,
            null,
            null,
            null,
            null,
            null)

        if(infoCursor.count > 0) {
            infoCursor.use {
                it.moveToFirst()
                money = it.getDouble(it.getColumnIndex(InformationTable.COLUMN_NAME_MONEY))
                nResidential =
                    it.getInt(it.getColumnIndex(InformationTable.COLUMN_NAME_RESIDENTIAL))
                nCommercial = it.getInt(it.getColumnIndex(InformationTable.COLUMN_NAME_COMMERCIAL))
                gameTime = it.getInt(it.getColumnIndex(InformationTable.COLUMN_NAME_GAME_TIME))
                lastIncome =
                    it.getDouble(it.getColumnIndex(InformationTable.COLUMN_NAME_LAST_INCOME))
            }
        }

        infoCursor.close()
        refreshData()
    }

    /**
     * Update an individual mapElement
     * @param row
     * @param col
     * @param me mapElement to save
     * @see save
     */
    private fun updateMapElement(row: Int, col: Int, me: MapElement) {
        val whereValue = arrayOf(row.toString(), col.toString())
        val values = ContentValues().apply {
            var buildable = 0
            if(me.isBuildable) buildable = 1

            put(MapElementTable.COLUMN_NAME_IS_BUILDABLE, buildable)
            put(MapElementTable.COLUMN_NAME_NORTH_EAST, me.northEast)
            put(MapElementTable.COLUMN_NAME_NORTH_WEST, me.northWest)
            put(MapElementTable.COLUMN_NAME_SOUTH_EAST, me.southEast)
            put(MapElementTable.COLUMN_NAME_SOUTH_WEST, me.southWest)

            if(me.structure != null) {
                put(MapElementTable.COLUMN_NAME_STRUCTURE, me.structure?.id)
            } else {
                put(MapElementTable.COLUMN_NAME_STRUCTURE, -1)
            }

            if(me.owner != null) {
                put(MapElementTable.COLUMN_NAME_OWNER, me.owner)
            }
        }

        this.db.update(MapElementTable.NAME, values,
            "${MapElementTable.COLUMN_NAME_ROW} = ? AND ${MapElementTable.COLUMN_NAME_COLUMN} = ?", whereValue)
    }

    /**
     * Update information based on input contentvalues
     * @param cv ContentValues
     */
    private fun updateInformation(cv: ContentValues) {
        this.db.update(InformationTable.NAME, cv, "${InformationTable.COLUMN_NAME_ID}= ?", arrayOf(1.toString()))
    }

    /**
     * Delete all from information and mapelement database tables
     */
    private fun delete() {
        this.db.execSQL("DELETE FROM ${MapElementTable.NAME}")
        this.db.execSQL("DELETE FROM ${InformationTable.NAME}")
    }
}