package com.yeet.simskylines.model

/**
 * A data class used by the cursor to return additional information than what is contained in
 * MapElement
 * @see MapElement
 * @see MapElementCursor
 */
data class MapElementData (val isBuildable: Boolean, val northWest: Int, val northEast: Int,
                           val southWest: Int, val southEast: Int,
                           var structure: Structure?, var owner: String?,
                           var row: Int, var col: Int)