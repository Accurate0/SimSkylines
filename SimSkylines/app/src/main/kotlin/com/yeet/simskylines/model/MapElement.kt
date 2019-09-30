package com.yeet.simskylines.model

import android.graphics.Bitmap

/**
 * Represents a single grid square in the map. Each map element
 * has both terrain and an optional structure.
 */
data class MapElement constructor (val isBuildable: Boolean, val northWest: Int, val northEast: Int,
                 val southWest: Int, val southEast: Int,
                 /**
                  * Retrieves the structure built on this map element.
                  * @return The structure, or null if one is not present.
                  */
                 var structure: Structure?, var image: Bitmap?, var owner: String?)