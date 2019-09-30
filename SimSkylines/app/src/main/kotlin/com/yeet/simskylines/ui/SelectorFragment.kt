package com.yeet.simskylines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yeet.simskylines.R
import com.yeet.simskylines.model.Structure
import com.yeet.simskylines.model.StructureData

/**
 * Selector Fragment for the MapActivity, allows selection of structures
 */
class SelectorFragment : Fragment() {
    private var structureData: StructureData? = null
    private var selectorAdapter: SelectorAdapter? = null
    var selectedStructure: Structure? = null
        private set
    private var currentPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        structureData = StructureData.getInstance()
    }

    /**
     * Setup RecyclerView and adapter
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        this.selectorAdapter = SelectorAdapter()
        val view = inflater.inflate(R.layout.selector_recycler, container, false)

        view.findViewById<RecyclerView>(R.id.selector_recyclerview).apply {
            layoutManager = (
                    LinearLayoutManager(
                        activity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                        )
                    )

            adapter = selectorAdapter
        }

        return view
    }

    /**
     * Adapter for the Structure Selector, the last position gets selected
     * on onBindViewHolder, and is used to highlight the currently selected
     * structure
     */
    private inner class SelectorAdapter : RecyclerView.Adapter<SelectorViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorViewHolder {
            return SelectorViewHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: SelectorViewHolder, position: Int) {
            val row = position % structureData!!.size()

            if(position == currentPosition) {
                holder.itemView.setBackgroundColor(
                                    ContextCompat.getColor(this@SelectorFragment.context!!,
                                                            R.color.blue)
                                    )
            } else {
                holder.itemView.setBackgroundColor(
                                    ContextCompat.getColor(this@SelectorFragment.context!!,
                                                            android.R.color.transparent)
                                    )
            }

            structureData?.get(row)?.let {
                holder.bind(it)
            }
        }

        override fun getItemCount(): Int {
            return structureData!!.size()
        }
    }

    /**
     * Represents each viewholder in the recyclerview
     */
    private inner class SelectorViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.selector_grid, parent, false)) {
        private val selectorImage: ImageView
        private val selectorText: TextView
        private val costText: TextView

        init {
            selectorImage = itemView.findViewById(R.id.selectorImage)
            selectorText = itemView.findViewById(R.id.list_data)
            costText = itemView.findViewById(R.id.cost_data)

            setupOnClick()
        }

        /**
         * Set information on the viewholder
         */
        fun bind(structure: Structure) {
            selectorImage.setImageResource(structure.drawableId)
            selectorText.text = structure.label
            costText.text = getString(R.string.cost, StructureData.getCost(structure))
        }

        /**
         * Setup on click for the view and image
         */
        private fun setupOnClick() {
            itemView.setOnClickListener {
                changeStructure()
            }

            selectorImage.setOnClickListener {
                changeStructure()
            }
        }

        /**
         * Swap between the previously clicked one, and the newly clicked one,
         * notify the adapter at both the old and new position to, rehighlight,
         * and select the new structure
         */
        private fun changeStructure() {
            val oldPos = currentPosition
            currentPosition = adapterPosition
            selectorAdapter?.notifyItemChanged(oldPos)
            selectorAdapter?.notifyItemChanged(currentPosition)
            selectedStructure = structureData!![currentPosition % structureData!!.size()]
        }
    }
}