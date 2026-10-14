package com.example.myapp004objednavka

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp004objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View Binding místo findViewById
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ošetření systémových lišt – použije se přímo binding.main nebo binding.root
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tlačítko Objednat – vypíše souhrn do žlutého TextView
        binding.btnOrder.setOnClickListener {

            // který nůž je vybraný (RadioGroup vrací id zaškrtnutého RadioButtonu)
            val knife = when (binding.rgKnives.checkedRadioButtonId) {
                binding.rbKnife1.id -> binding.rbKnife1
                binding.rbKnife2.id -> binding.rbKnife2
                else -> binding.rbKnife3
            }

            // nůž + zaškrtnuté doplňky (texty CheckBoxů už jsou přeložené ze strings.xml)
            var items = knife.text.toString()
            if (binding.cbStatTrak.isChecked) items += "; " + binding.cbStatTrak.text
            if (binding.cbNameTag.isChecked) items += "; " + binding.cbNameTag.text
            if (binding.cbFactoryNew.isChecked) items += "; " + binding.cbFactoryNew.text

            // %1$s ve strings.xml se nahradí seznamem položek – slovosled určuje překlad
            binding.tvOrder.text = getString(R.string.order_summary, items)

            Toast.makeText(
                this,
                getString(R.string.order_confirmation, knife.text),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Změna obrázku v závislosti na vybraném radioButtonu
        binding.rbKnife1.setOnClickListener {
            binding.ivKnife.setImageResource(R.drawable.img_knife_butterfly)
        }

        binding.rbKnife2.setOnClickListener {
            binding.ivKnife.setImageResource(R.drawable.img_knife_karambit)
        }

        binding.rbKnife3.setOnClickListener {
            binding.ivKnife.setImageResource(R.drawable.img_knife_skeleton)
        }
    }
}
