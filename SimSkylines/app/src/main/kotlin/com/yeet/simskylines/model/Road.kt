package com.yeet.simskylines.model

/**
 * Data class used to represent a Road type building
 * @see Structure
 */
data class Road(override var drawableId: Int, override var label: String, override  var id: Int) : Structure()

