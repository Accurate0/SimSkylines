package com.yeet.simskylines.model

import com.yeet.simskylines.R

/**
 * Taken from Practical 3, modified to fit assignment
 */
class StructureData private constructor() {

    companion object {
        private var instance: StructureData? = null

        /**
         * Get a current instance of StructureData
         * @return the current structuredata
         */
        fun getInstance(): StructureData {
            if (instance == null) {
                instance = StructureData()
            }
            return instance!!
        }

        /**
         * Return the cost of the given structure
         * @param structure
         * @return cost of given structure
         */
        fun getCost(structure: Structure): Int {
            return when(structure) {
                is Residential -> Settings.getInstance().get(Settings.HOUSE_COST)
                is Commercial  -> Settings.getInstance().get(Settings.COMM_COST)
                is Road        -> Settings.getInstance().get(Settings.ROAD_COST)
                else           -> 0
            }
        }
    }

    /**
     * List containing all the structures
     */
    private val structureList: ArrayList<Structure> = arrayListOf(
            Residential(R.drawable.ic_building1, "House", 0),
            Residential(R.drawable.ic_building2, "House", 1),
            Residential(R.drawable.ic_building3, "House", 2),
            Residential(R.drawable.ic_building4, "House", 3),
            Commercial(R.drawable.ic_building5, "Shops", 4),
            Commercial(R.drawable.ic_building6, "Shops", 5),
            Commercial(R.drawable.ic_building7, "Shops", 6),
            Commercial(R.drawable.ic_building8, "Shops", 7),
            Road(R.drawable.ic_road_ns, "Road", 8),
            Road(R.drawable.ic_road_ew, "Road", 9),
            Road(R.drawable.ic_road_nsew, "Road", 10),
            Road(R.drawable.ic_road_ne, "Road", 11),
            Road(R.drawable.ic_road_nw, "Road", 12),
            Road(R.drawable.ic_road_se, "Road", 13),
            Road(R.drawable.ic_road_sw, "Road", 14),
            Road(R.drawable.ic_road_n, "Road", 15),
            Road(R.drawable.ic_road_e, "Road", 16),
            Road(R.drawable.ic_road_s, "Road", 17),
            Road(R.drawable.ic_road_w, "Road", 18),
            Road(R.drawable.ic_road_nse, "Road", 19),
            Road(R.drawable.ic_road_nsw, "Road", 20),
            Road(R.drawable.ic_road_new, "Road", 21),
            Road(R.drawable.ic_road_sew, "Road", 22)
    )

    /**
     * Get the structure at the given index
     * @param i
     * @return structure or null at given index
     */
    operator fun get(i: Int): Structure? {
        // Construct new object to prevent issues with references
        return when(structureList[i]) {
            is Residential  -> Residential(structureList[i].drawableId, structureList[i].label, structureList[i].id)
            is Commercial   -> Commercial(structureList[i].drawableId, structureList[i].label, structureList[i].id)
            is Road         -> Road(structureList[i].drawableId, structureList[i].label, structureList[i].id)
            else            -> null
        }
    }

    /**
     * @return the entire structureList
     */
    fun get(): ArrayList<Structure> {
        return this.structureList
    }

    /**
     * @return the size of the entire structure list
     */
    fun size(): Int {
        return structureList.size
    }
}