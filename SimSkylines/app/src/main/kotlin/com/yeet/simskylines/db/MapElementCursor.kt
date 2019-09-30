package com.yeet.simskylines.db

import android.database.Cursor
import android.database.CursorWrapper
import com.yeet.simskylines.db.GameDataSchema.MapElementTable
import com.yeet.simskylines.model.MapElementData
import com.yeet.simskylines.model.Structure
import com.yeet.simskylines.model.StructureData

/**
 * Cursor class for the MapElement, extends CursorWrapper in order to
 * make it easier to return constructed MapElement data classes to rebuild
 * the in memory array for the RecyclerView
 * @see SettingsCursor
 */
class MapElementCursor constructor(cursor: Cursor) : CursorWrapper(cursor)
{
    /**
     * @return returns a MapElementData which contains all the information from the database
     */
    fun getMapElement(): MapElementData {
        val me: MapElementData?
        var bool = false

        val isBuildable = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_IS_BUILDABLE))
        if(isBuildable == 1) {
            bool = true
        }

        val row = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_ROW))
        val col = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_COLUMN))
        val northWest = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_NORTH_WEST))
        val northEast = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_NORTH_EAST))
        val southWest = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_SOUTH_WEST))
        val southEast = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_SOUTH_EAST))
        val owner = getString(getColumnIndex(MapElementTable.COLUMN_NAME_OWNER))
        val structureID = getInt(getColumnIndex(MapElementTable.COLUMN_NAME_STRUCTURE))

        var structure: Structure? = null

        if(structureID >= 0) {
            structure = StructureData.getInstance()[structureID]
        }

        me = MapElementData(
            bool,
            northWest,
            northEast,
            southWest,
            southEast,
            structure,
            owner,
            row,
            col
        )

        return me
    }
}