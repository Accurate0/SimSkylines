package com.yeet.simskylines.ui

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yeet.simskylines.R
import com.yeet.simskylines.model.Settings

class SettingsFragment : Fragment()
{
    private val TAG = this::class.java.name

    var settings: Settings = Settings.getInstance()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_recycler, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.settings_recycler)

        recyclerView.layoutManager = LinearLayoutManager(activity,
                                        LinearLayoutManager.VERTICAL,
                                        false)
        recyclerView.adapter = SettingsAdapter()

        return view
    }

    inner class SettingsAdapter : RecyclerView.Adapter<SettingViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
            return SettingViewHolder(LayoutInflater.from(activity), parent)
        }

        override fun getItemCount(): Int {
            return settings.size()
        }

        override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
            holder.bind(position)
        }

    }

    inner class SettingViewHolder constructor(li: LayoutInflater, parent: ViewGroup)
                : RecyclerView.ViewHolder(li.inflate(R.layout.settings_viewholder, parent, false))
    {
        private var text: TextView? = null
        private var button: Button? = null
        private var valueField: EditText? = null
        private var currentKey: Int = 0

        init {
            this.text = itemView.findViewById(R.id.option_text)
            this.button = itemView.findViewById(R.id.confirm_button)
            this.valueField = itemView.findViewById(R.id.value_field)

            setupCallback()
        }

        fun bind(key: Int) {
            this.currentKey = key

            this.text?.text = settings.getString(currentKey)
            this.button?.text = getString(R.string.set_button)
            this.valueField?.text = SpannableStringBuilder(settings.get(currentKey).toString())
        }

        private fun setupCallback() {
            this.button?.setOnClickListener{
                try {
                    val value = valueField?.text.toString().toInt()
                    settings.put(currentKey, value)
                } catch(e: NumberFormatException) {}
            }
        }
    }
}