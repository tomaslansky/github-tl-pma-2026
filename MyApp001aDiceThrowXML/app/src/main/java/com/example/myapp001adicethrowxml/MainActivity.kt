package com.example.myapp001adicethrowxml

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")
    private var diceValue = 1
    private val history = mutableListOf<Int>()

    private lateinit var tvDice: TextView
    private lateinit var tvStats: TextView
    private lateinit var tvHistory: TextView
    private lateinit var tvCounts: List<TextView>
    private lateinit var btnRoll: Button
    private lateinit var btnClear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.llMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvDice = findViewById(R.id.tvDice)
        tvStats = findViewById(R.id.tvStats)
        tvHistory = findViewById(R.id.tvHistory)
        tvCounts = listOf(
            findViewById(R.id.tvCount1), findViewById(R.id.tvCount2), findViewById(R.id.tvCount3),
            findViewById(R.id.tvCount4), findViewById(R.id.tvCount5), findViewById(R.id.tvCount6)
        )
        btnRoll = findViewById(R.id.btnRoll)
        btnClear = findViewById(R.id.btnClear)

        // Po otočení telefonu se aktivita vytvoří znovu – obnovíme uložený stav
        if (savedInstanceState != null) {
            diceValue = savedInstanceState.getInt("diceValue", 1)
            history.addAll(savedInstanceState.getIntArray("history")?.toList() ?: emptyList())
        }
        tvDice.text = diceSymbols[diceValue - 1]
        updateStatistics()

        btnRoll.setOnClickListener {

            lifecycleScope.launch {
                btnRoll.isEnabled = false
                btnClear.isEnabled = false
                repeat(times = 10){
                    tvDice.text = diceSymbols.random()
                    delay(timeMillis = 250)
                }
                diceValue = (1..6).random()
                tvDice.text = diceSymbols[diceValue -1 ]
                history.add(diceValue)
                updateStatistics()
                btnRoll.isEnabled = true

            }
        }

        btnClear.setOnClickListener {
            history.clear()
            updateStatistics()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("diceValue", diceValue)
        outState.putIntArray("history", history.toIntArray())
    }

    // V XML verzi se UI samo nepřekreslí – po každé změně historie ho musíme aktualizovat ručně
    private fun updateStatistics() {
        val average = if (history.isEmpty()) "–"
            else String.format(Locale.getDefault(), "%.2f", history.average())
        tvStats.text = "Hodů: ${history.size}   Součet: ${history.sum()}   Průměr: $average"

        for (value in 1..6) {
            tvCounts[value - 1].text = "${diceSymbols[value - 1]}\n${history.count { it == value }}×"
        }

        tvHistory.text = if (history.isEmpty()) "zatím žádné"
            else history.takeLast(10).reversed().joinToString(" ") { diceSymbols[it - 1] }

        btnClear.isEnabled = history.isNotEmpty()
    }
}
