package com.yeet.simskylines.model

/**
 * Data class used to represent a Commercial building
 * @see Structure
 */
data class Commercial(override var drawableId: Int, override var label: String, override  var id: Int) : Structure()
