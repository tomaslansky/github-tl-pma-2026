package com.example.myapp004objednavka

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp004objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Vlastní rozšíření: ceny v Kč (orientační ceny ze Steam Marketu)
    companion object {
        const val PRICE_BUTTERFLY = 65_000
        const val PRICE_KARAMBIT = 53_000
        const val PRICE_SKELETON = 21_000
        const val PRICE_STATTRAK = 10_000
        const val PRICE_NAME_TAG = 50
        const val PRICE_FACTORY_NEW = 6_000
    }

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

            // nůž + zaškrtnuté doplňky (texty CheckBoxů už jsou přeložené ze strings.xml)
            val knife = selectedKnife()
            var items = knife.text.toString()
            if (binding.cbStatTrak.isChecked) items += "; " + binding.cbStatTrak.text
            if (binding.cbNameTag.isChecked) items += "; " + binding.cbNameTag.text
            if (binding.cbFactoryNew.isChecked) items += "; " + binding.cbFactoryNew.text

            val price = totalPrice()

            // %1$s a %2$,d ve strings.xml se nahradí položkami a cenou – slovosled určuje překlad
            binding.tvOrder.text = getString(R.string.order_summary, items, price)

            Toast.makeText(
                this,
                getString(R.string.order_confirmation, knife.text, price),
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

        // Vlastní rozšíření: cena se přepočítá při každé změně výběru
        binding.rgKnives.setOnCheckedChangeListener { _, _ -> showPrice() }
        binding.cbStatTrak.setOnCheckedChangeListener { _, _ -> showPrice() }
        binding.cbNameTag.setOnCheckedChangeListener { _, _ -> showPrice() }
        binding.cbFactoryNew.setOnCheckedChangeListener { _, _ -> showPrice() }
        showPrice()
    }

    // který nůž je vybraný (RadioGroup vrací id zaškrtnutého RadioButtonu)
    private fun selectedKnife() = when (binding.rgKnives.checkedRadioButtonId) {
        binding.rbKnife1.id -> binding.rbKnife1
        binding.rbKnife2.id -> binding.rbKnife2
        else -> binding.rbKnife3
    }

    // cena vybraného nože + zaškrtnutých doplňků
    private fun totalPrice(): Int {
        var price = when (selectedKnife()) {
            binding.rbKnife1 -> PRICE_BUTTERFLY
            binding.rbKnife2 -> PRICE_KARAMBIT
            else -> PRICE_SKELETON
        }
        if (binding.cbStatTrak.isChecked) price += PRICE_STATTRAK
        if (binding.cbNameTag.isChecked) price += PRICE_NAME_TAG
        if (binding.cbFactoryNew.isChecked) price += PRICE_FACTORY_NEW
        return price
    }

    private fun showPrice() {
        binding.tvPrice.text = getString(R.string.price_label, totalPrice())
    }
}
