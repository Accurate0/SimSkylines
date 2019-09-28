package com.yeet.simskylines.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yeet.simskylines.R
import com.yeet.simskylines.model.GameData

import com.yeet.simskylines.model.MapElement
import com.yeet.simskylines.model.Settings

class MapFragment : Fragment() {
    var selectorFragment: SelectorFragment? = null

    private var map: GameData? = null
    private var mapAdapter: MapAdapter? = null
    private var settings = Settings.getInstance()

//    fun setSelectorFragment(selectorFragment: SelectorFragment) {
//        this.selectorFragment = selectorFragment
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        map = GameData.getInstance()
        map?.regenerate()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.map_recycler, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.map_recyclerview)
        recyclerView.layoutManager= (
            GridLayoutManager(
                activity,
                settings.get(Settings.MAP_HEIGHT),
                GridLayoutManager.HORIZONTAL,
                false
            )
        )

        mapAdapter = MapAdapter()
        recyclerView.adapter = mapAdapter

        return recyclerView
    }

    private inner class MapAdapter : RecyclerView.Adapter<MapViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
            return MapViewHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
            val row = position % settings.get(Settings.MAP_HEIGHT)
            val col = position / settings.get(Settings.MAP_HEIGHT)
            holder.bind(map!![row, col])
        }

        override fun getItemCount(): Int {
            return settings.get(Settings.MAP_HEIGHT) * settings.get(Settings.MAP_WIDTH)
        }
    }

    private inner class MapViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.map_grid, parent, false)) {
        private val northEast: ImageView
        private val northWest: ImageView
        private val southEast: ImageView
        private val southWest: ImageView
        private val structure: ImageView

        private var currentMapElement: MapElement? = null

        init {

            northEast = itemView.findViewById(R.id.imageNE)
            northWest = itemView.findViewById(R.id.imageNW)
            southEast = itemView.findViewById(R.id.imageSE)
            southWest = itemView.findViewById(R.id.imageSW)
            structure = itemView.findViewById(R.id.structure)

            val size = parent.measuredHeight / settings.get(Settings.MAP_HEIGHT) + 3
            val lp = itemView.layoutParams
            lp.width = size
            lp.height = size

            setupOnClick()
        }

        fun bind(mapElement: MapElement) {
            this.currentMapElement = mapElement

            northEast.setImageResource(mapElement.northEast)
            northWest.setImageResource(mapElement.northWest)
            southWest.setImageResource(mapElement.southWest)
            southEast.setImageResource(mapElement.southEast)

            if (mapElement.structure == null) {
                structure.setImageResource(android.R.color.transparent)
            } else {
                structure.setImageResource(mapElement.structure!!.drawableId)
            }
        }

        fun setupOnClick() {
            northEast.setOnClickListener {
                currentMapElement!!.structure = selectorFragment!!.selectedStructure
                mapAdapter!!.notifyItemChanged(adapterPosition)
            }

            southEast.setOnClickListener {
                currentMapElement!!.structure = selectorFragment!!.selectedStructure
                mapAdapter!!.notifyItemChanged(adapterPosition)
            }

            northWest.setOnClickListener {
                currentMapElement!!.structure = selectorFragment!!.selectedStructure
                mapAdapter!!.notifyItemChanged(adapterPosition)
            }

            southWest.setOnClickListener {
                currentMapElement!!.structure = selectorFragment!!.selectedStructure
                mapAdapter!!.notifyItemChanged(adapterPosition)
            }
        }
    }
}