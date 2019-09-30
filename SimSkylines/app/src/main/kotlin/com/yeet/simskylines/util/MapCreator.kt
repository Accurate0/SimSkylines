package com.yeet.simskylines.util

import com.yeet.simskylines.R
import com.yeet.simskylines.model.MapElement
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Taken directly from Practical 3
 * Converted to Kotlin, with a few minor changes
 */
object MapCreator
{
    private const val WATER = R.drawable.ic_water
    private val GRASS = intArrayOf(
        R.drawable.ic_grass1,
        R.drawable.ic_grass2,
        R.drawable.ic_grass3,
        R.drawable.ic_grass4
    )

    private val rng = Random()

    private const val HEIGHT_RANGE = 256
    private const val WATER_LEVEL = 112
    private const val INLAND_BIAS = 24
    private const val AREA_SIZE = 1
    private const val SMOOTHING_ITERATIONS = 2

    fun generateGrid(height: Int, width: Int): Array<Array<MapElement?>> {
        var heightField = Array(height) { IntArray(width) }
        for(i in 0 until height) {
            for(j in 0 until width) {
                heightField[i][j] = rng.nextInt(HEIGHT_RANGE) + INLAND_BIAS * (min(
                    min(i, j),
                    min(height - i - 1, width - j - 1)
                ) - min(height, width) / 4)
            }
        }

        var newHf = Array(height) { IntArray(width) }
        for(s in 0 until SMOOTHING_ITERATIONS) {
            for(i in 0 until height) {
                for(j in 0 until width) {
                    var areaSize = 0
                    var heightSum = 0

                    for (areaI in max(0, i - AREA_SIZE) until min(
                        height,
                        i + AREA_SIZE + 1
                    )) {
                        for (areaJ in max(0, j - AREA_SIZE) until min(
                            width,
                            j + AREA_SIZE + 1
                        )) {
                            areaSize++
                            heightSum += heightField[areaI][areaJ]
                        }
                    }

                    newHf[i][j] = heightSum / areaSize
                }
            }

            val tmpHf = heightField
            heightField = newHf
            newHf = tmpHf
        }

        val grid = Array<Array<MapElement?>>(height) { arrayOfNulls(width) }
        for(i in 0 until height) {
            for(j in 0 until width) {
//                    val element: MapElement

                if(heightField[i][j] >= WATER_LEVEL) {
                    val waterN = i == 0 || heightField[i - 1][j] < WATER_LEVEL
                    val waterE = j == width - 1 || heightField[i][j + 1] < WATER_LEVEL
                    val waterS = i == height - 1 || heightField[i + 1][j] < WATER_LEVEL
                    val waterW = j == 0 || heightField[i][j - 1] < WATER_LEVEL

                    val waterNW = i == 0 || j == 0 || heightField[i - 1][j - 1] < WATER_LEVEL
                    val waterNE =
                        i == 0 || j == width - 1 || heightField[i - 1][j + 1] < WATER_LEVEL
                    val waterSW =
                        i == height - 1 || j == 0 || heightField[i + 1][j - 1] < WATER_LEVEL
                    val waterSE =
                        i == height - 1 || j == width - 1 || heightField[i + 1][j + 1] < WATER_LEVEL

                    val coast = false//waterN || waterE || waterS || waterW ||
                    //waterNW || waterNE || waterSW || waterSE

                    grid[i][j] = MapElement(
                        !coast,
                        choose(
                            waterN, waterW, waterNW,
                            R.drawable.ic_coast_north, R.drawable.ic_coast_west,
                            R.drawable.ic_coast_northwest, R.drawable.ic_coast_northwest_concave
                        ),
                        choose(
                            waterN, waterE, waterNE,
                            R.drawable.ic_coast_north, R.drawable.ic_coast_east,
                            R.drawable.ic_coast_northeast, R.drawable.ic_coast_northeast_concave
                        ),
                        choose(
                            waterS, waterW, waterSW,
                            R.drawable.ic_coast_south, R.drawable.ic_coast_west,
                            R.drawable.ic_coast_southwest, R.drawable.ic_coast_southwest_concave
                        ),
                        choose(
                            waterS, waterE, waterSE,
                            R.drawable.ic_coast_south, R.drawable.ic_coast_east,
                            R.drawable.ic_coast_southeast, R.drawable.ic_coast_southeast_concave
                        ), null, null, null
                    )
                } else {
                    grid[i][j] = MapElement(
                        false, WATER, WATER, WATER, WATER, null, null, null
                    )
                }
            }
        }

        return grid
    }

    private fun choose(nsWater: Boolean,
                       ewWater: Boolean,
                       diagWater: Boolean,
                       nsCoastId: Int,
                       ewCoastId: Int,
                       convexCoastId: Int,
                       concaveCoastId: Int): Int {
        val id: Int
        if(nsWater) {
            if(ewWater) {
                id = convexCoastId
            } else {
                id = nsCoastId
            }
        } else {
            if(ewWater) {
                id = ewCoastId
            } else if(diagWater) {
                id = concaveCoastId
            } else {
                id = GRASS[rng.nextInt(GRASS.size)]
            }
        }
        return id
    }
}