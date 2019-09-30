package com.yeet.simskylines.ui

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yeet.simskylines.R
import com.yeet.simskylines.model.Settings

/**
 * Fragment used to display the Settings view
 */
class SettingsFragment : Fragment()
{
    private val TAG = this::class.java.name

    private var settingsAdapter: SettingsAdapter? = null
    val settings: Settings = Settings.getInstance()

    /**
     * Setup the RecyclerView and the adapter
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        settingsAdapter = SettingsAdapter()
        val view = inflater.inflate(R.layout.settings_recycler, container, false)
        view.findViewById<RecyclerView>(R.id.settings_recycler).apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = settingsAdapter
            setHasFixedSize(true)
        }

        return view
    }

    /**
     * Tell the adapter to reload the entire settings
     */
    fun reload() {
        settingsAdapter?.notifyDataSetChanged()
    }

    /**
     * Adapter for Settings, calls binds and constructs ViewHolders
     */
    private inner class SettingsAdapter : RecyclerView.Adapter<SettingViewHolder>()
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

    /**
     * Each Setting ViewHolder, is constructed by this class
     */
    private inner class SettingViewHolder constructor(li: LayoutInflater, parent: ViewGroup)
                : RecyclerView.ViewHolder(li.inflate(R.layout.settings_viewholder, parent, false))
    {
        private var desc: TextView? = null
        private var text: TextView? = null
        private var valueField: EditText? = null
        private var currentKey: Int = 0

        init {
            this.desc = itemView.findViewById(R.id.description)
            this.text = itemView.findViewById(R.id.option_text)
            this.valueField = itemView.findViewById(R.id.value_field)

            setupCallback()
        }

        /**
         * Bind's each viewholder using the specific key
         * @param key
         */
        fun bind(key: Int) {
            this.currentKey = key

            this.desc?.text = settings.getDescription(currentKey)
            this.text?.text = settings.getString(currentKey)
            this.valueField?.text = SpannableStringBuilder(settings.get(currentKey).toString())
        }

        /**
         * Setups up callback for textChangedListener on the valueField
         */
        private fun setupCallback() {
            valueField?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    try {
                        val value = s.toString().toInt()
                        settings.put(currentKey, value)
                    } catch(e: NumberFormatException) {
                        // This exception should never be thrown
                    }
                }
            })
        }
    }
}