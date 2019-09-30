package com.yeet.simskylines.model

/**
 * Data class used to represent a Residential building
 * @see Structure
 */
data class Residential constructor(override var drawableId: Int, override var label: String, override  var id: Int) : Structure()
