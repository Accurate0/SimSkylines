package com.yeet.simskylines.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.yeet.simskylines.R
import com.yeet.simskylines.model.GameData

/**
 * The fragment used to display the information about the current game
 * contains many views which are updated as required
 */
class InformationFragment : Fragment()
{
    var demolishSet = false

    private var gameData = GameData.getInstance()
    var time: TextView? = null
    var money: TextView? = null
    var income: TextView? = null
    var population: TextView? = null
    var employment: TextView? = null
    var increaseTime: Button? = null
    var demolish: ToggleButton? = null

    /**
     * Overriden onCreateView, sets up the defaults and assigns
     * values to the classfields
     */
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.information_section, container, false)

        time = view.findViewById(R.id.game_time)
        money = view.findViewById(R.id.current_money)
        income = view.findViewById(R.id.recent_income)
        population = view.findViewById(R.id.population)
        employment = view.findViewById(R.id.employment_rate)
        increaseTime = view.findViewById(R.id.game_time_button)
        demolish = view.findViewById(R.id.demolish_button)

        demolish?.textOn = getString(R.string.demolish_button)
        demolish?.textOff = getString(R.string.build_button)

        refresh()
        setupOnClick()

        return view
    }

    /**
     * Extension to double class to add feature that should exist anyway
     * Thanks Kotlin for extension functions
     */
    private fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

    /**
     * Refresh the current text shown in fragment
     */
    fun refresh() {
        // to deal with -0.0, because that looks dumb lmao
        val inc = gameData.lastIncome + 0.0

        time?.text = getString(R.string.game_time, gameData.gameTime)
        money?.text = getString(R.string.money, gameData.money.format(2))
        income?.text = getString(R.string.income, inc.format(2))
        population?.text = getString(R.string.people, gameData.population)
        employment?.text = getString(R.string.employment, (gameData.employmentRate * 100.0).format(2))
    }

    /**
     * Set up the on click listeners for some views
     */
    private fun setupOnClick() {
        demolish?.setOnClickListener {
            demolishSet = !demolishSet
        }

        increaseTime?.setOnClickListener {
            gameData.increaseGameTime(this.context!!)
            refresh()
        }
    }
}