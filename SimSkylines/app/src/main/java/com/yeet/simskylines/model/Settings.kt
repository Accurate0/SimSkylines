package com.yeet.simskylines.model

class Settings private constructor()
{
    private var map: MutableMap<Int, Int> = HashMap()

    companion object {
        // Constants in RecyclerView order
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
        const val TAX_RATE = 10

        // Default Values, loaded into HashMap
        const val mapWidth: Int = 50
        const val mapHeight: Int = 10
        const val initialMoney: Int = 1000
        const val familySize: Int = 4
        const val shopSize: Int = 6
        const val salary: Int = 10
        const val serviceCost: Int = 2
        const val houseBuildingCost: Int = 100
        const val commBuildingCost: Int = 500
        const val roadBuildingCost: Int = 20
        const val taxRate: Double = 0.3

        private var current: Settings = Settings()

        // We don't want multiple instances
        // of settings, that's pointless
        fun getInstance(): Settings {
            return current
        }
    }

    init {
        map[MAP_WIDTH] = mapWidth
        map[MAP_HEIGHT] = mapHeight
        map[INITIAL_MONEY] = initialMoney
        map[FAMILY_SIZE] = familySize
        map[SHOP_SIZE] = shopSize
        map[SALARY] = salary
        map[SERVICE_COST] = serviceCost
        map[HOUSE_COST] = houseBuildingCost
        map[COMM_COST] = commBuildingCost
        map[ROAD_COST] = roadBuildingCost
//        map[TAX_RATE] = (taxRate * 10).toInt()
    }

    fun getString(key: Int?): String? {
        when(key) {
            MAP_WIDTH -> return "Map Width"
            MAP_HEIGHT -> return "Map Height"
            INITIAL_MONEY -> return "Initial Money"
            FAMILY_SIZE -> return "Family Size"
            SHOP_SIZE -> return "Shop Size"
            SALARY -> return "Salary"
            SERVICE_COST -> return "Service Cost"
            HOUSE_COST -> return "House Cost"
            COMM_COST -> return "Community Cost"
            ROAD_COST -> return "Road Cost"
            TAX_RATE -> return "Tax Rate"
        }

        return null
    }

    fun put(key: Int, value: Int) {
        (map as HashMap)[key] = value
    }

    fun get(key: Int): Int {
        return map.getValue(key)
    }

    fun size(): Int {
        return map.size
    }
}
