package com.example.myapp004objednavka

import android.os.Bundle
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

            val orderText = getString(R.string.textview_order_summary_text) + " " +
                    knife.text +
                    (if (binding.cbStatTrak.isChecked) "; " + binding.cbStatTrak.text else "") +
                    (if (binding.cbNameTag.isChecked) "; " + binding.cbNameTag.text else "") +
                    (if (binding.cbFactoryNew.isChecked) "; " + binding.cbFactoryNew.text else "")

            binding.tvOrder.text = orderText
        }




        // Změna obrázku v závislosti na vybraném radioButtonu
        binding.rbKnife1.setOnClickListener {
            binding.ivKnife.setImageResource(R.drawable.knife_butterfly)
        }

        binding.rbKnife2.setOnClickListener {
            binding.ivKnife.setImageResource(R.drawable.knife_karambit)
        }

        binding.rbKnife3.setOnClickListener {
            binding.ivKnife.setImageResource(R.drawable.knife_skeleton)
        }
    }
}
