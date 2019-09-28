package com.yeet.simskylines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.islandbuilder.StructureData
import com.yeet.simskylines.R

import com.yeet.simskylines.model.Structure

class SelectorFragment : Fragment() {
    private var structureData: StructureData? = null
    var selectedStructure: Structure? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        structureData = StructureData.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.selector_recycler, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.selector_recyclerview)
        recyclerView.layoutManager = (
            LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        val selectorAdapter = SelectorAdapter()
        recyclerView.setAdapter(selectorAdapter)

        return recyclerView
    }

    private inner class SelectorAdapter : RecyclerView.Adapter<SelectorViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorViewHolder {
            return SelectorViewHolder(LayoutInflater.from(activity), parent)
        }

        override fun onBindViewHolder(holder: SelectorViewHolder, position: Int) {
            val row = position % structureData!!.size()

            holder.bind(structureData!!.get(row))
        }

        override fun getItemCount(): Int {
            return structureData!!.size()
        }
    }

    private inner class SelectorViewHolder(li: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(li.inflate(R.layout.selector_grid, parent, false)) {
        private val selectorImage: ImageView
        private val selectorText: TextView

        init {

            selectorImage = itemView.findViewById(R.id.selectorImage)
            selectorText = itemView.findViewById(R.id.list_data)

            setupOnClick()
        }

        fun bind(structure: Structure) {
            selectorImage.setImageResource(structure.drawableId)
            selectorText.text = structure.label
        }

        fun setupOnClick() {
            selectorImage.setOnClickListener {
                selectedStructure = structureData!!.get(adapterPosition % structureData!!.size())
            }
        }
    }
}