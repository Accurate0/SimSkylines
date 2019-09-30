package com.yeet.simskylines.model

/**
 * Represents a possible structure to be placed on the map. A structure simply contains a drawable
 * int reference, and a string label to be shown in the selector.
 * Abstract forcing all other structures to override
 * @see Residential
 * @see Commercial
 * @see Road
 */
abstract class Structure
{
    abstract var drawableId: Int
    abstract var label: String
    abstract var id: Int
}