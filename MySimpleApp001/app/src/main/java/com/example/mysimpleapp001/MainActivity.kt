package com.example.mysimpleapp001

import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var secretNumber = Random.nextInt(1, 101)
    private var attempts = 0
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ošetření systémových lišt (status bar / navigační lišta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vazba na prvky definované v XML
        val editGuess = findViewById<EditText>(R.id.editGuess)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val textResult = findViewById<TextView>(R.id.textResult)
        val textAttempts = findViewById<TextView>(R.id.textAttempts)

        btnSubmit.setOnClickListener {
            // Restart hry po uhodnutí
            if (gameOver) {
                secretNumber = Random.nextInt(1, 101)
                attempts = 0
                gameOver = false
                btnSubmit.text = "Tipnout"
                textResult.text = "Nová hra! Zadej číslo od 1 do 100."
                textResult.setTextColor(Color.DKGRAY)
                textAttempts.text = "Počet pokusů: 0"
                editGuess.text.clear()
                return@setOnClickListener
            }

            // Skrytí softwarové klávesnice po odeslání tipu
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editGuess.windowToken, 0)

            val guess = editGuess.text.toString().toIntOrNull()

            // Ošetření prázdného nebo neplatného vstupu
            if (guess == null || guess !in 1..100) {
                textResult.text = "Zadej platné číslo mezi 1 a 100!"
                textResult.setTextColor(Color.RED)
                return@setOnClickListener
            }

            attempts++
            textAttempts.text = "Počet pokusů: $attempts"

            // Logika vyhodnocení
            when {
                guess < secretNumber -> {
                    textResult.text = "PŘIDEJ! Moje číslo je VĚTŠÍ ⬆️"
                    textResult.setTextColor(Color.rgb(33, 150, 243))
                }
                guess > secretNumber -> {
                    textResult.text = "UBER! Moje číslo je MENŠÍ ⬇️"
                    textResult.setTextColor(Color.rgb(255, 152, 0))
                }
                else -> {
                    textResult.text = "TREFIL JSI TO! 🎉 ($secretNumber)"
                    textResult.setTextColor(Color.rgb(76, 175, 80))
                    btnSubmit.text = "Hrát znovu"
                    gameOver = true
                }
            }

            editGuess.text.clear()
        }
    }
}
