package com.example.myapp001bdicethrowcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aplikace kreslí i pod systémové lišty, odsazení řeší safeDrawingPadding() v DiceApp
        enableEdgeToEdge()
        // Místo XML layoutu se celé UI popisuje Compose funkcemi
        setContent {
            MaterialTheme { DiceApp() }
        }
    }
}

// Hlavní obrazovka: nadpis, kostka, tlačítko a statistiky hodů
@Composable
fun DiceApp(){
    // Unicode symboly stěn kostky, index 0 = jednička
    val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")
    // Stav obrazovky – při jeho změně Compose UI automaticky překreslí (rekompozice).
    // rememberSaveable = hodnota přežije i otočení telefonu
    var diceValue by rememberSaveable { mutableStateOf(value = 1) }
    var history by rememberSaveable { mutableStateOf(listOf<Int>()) }
    // true během animace hodu – tlačítko je mezitím vypnuté
    var isRolling by remember { mutableStateOf(false) }
    // Korutiny pro animaci (delay neblokuje hlavní vlákno)
    val scope = rememberCoroutineScope()

    val backgroundColor = Color(0xFFF5F3FF)
    val primaryColor = Color(0xFF352060)

    // Column skládá prvky pod sebe; obsah je vystředěný a odsazený od systémových lišt
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hoď kostkou",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        // Kostka – stačí změnit diceValue a text se překreslí sám
        Text(
            text = diceSymbols[diceValue - 1],
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Button(
            onClick = {
                isRolling = true
                scope.launch {
                    // Animace: 10 náhodných stěn po 250 ms, pak výsledný hod
                    repeat(10) {
                        diceValue = (1..6).random()
                        delay(250)
                    }
                    diceValue = (1..6).random()
                    history = history + diceValue
                    isRolling = false
                }
            },
            // Během animace je tlačítko zakázané
            enabled = !isRolling,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            )
        ) {
            Text(text = "Hodit", fontSize = 24.sp)
        }

        // Statistiky jsou vidět pořád, aby tlačítko po prvním hodu neposkočilo
        Spacer(modifier = Modifier.height(32.dp))
        Statistics(history, diceSymbols, primaryColor)

        TextButton(
            onClick = { history = emptyList() },
            enabled = !isRolling && history.isNotEmpty()
        ) {
            Text(text = "Vymazat historii")
        }
    }
}

// Souhrn všech hodů: počet, součet, průměr, četnosti a posledních 10 hodů
@Composable
fun Statistics(history: List<Int>, diceSymbols: List<String>, color: Color) {
    val average = if (history.isEmpty()) "–"
        else String.format(Locale.getDefault(), "%.2f", history.average())

    Text(
        text = "Hodů: ${history.size}   Součet: ${history.sum()}   Průměr: $average",
        fontSize = 16.sp,
        color = color
    )

    // Kolikrát padla která hodnota
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (value in 1..6) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = diceSymbols[value - 1], fontSize = 32.sp, color = color)
                Text(text = "${history.count { it == value }}×", fontSize = 14.sp, color = color)
            }
        }
    }

    // Posledních 10 hodů, nejnovější vlevo
    Text(
        text = "Poslední hody",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(top = 16.dp)
    )
    Text(
        text = if (history.isEmpty()) "zatím žádné"
            else history.takeLast(10).reversed().joinToString(" ") { diceSymbols[it - 1] },
        fontSize = 28.sp,
        color = color
    )
}
