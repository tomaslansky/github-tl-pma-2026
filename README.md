# PMA 2026 – Programování mobilních aplikací

Repozitář k předmětu **Programování mobilních aplikací** (TUL, 2026/2027).

| Složka | Obsah |
|---|---|
| [`MySimpleApp001/`](MySimpleApp001) | **Uhodni číslo** – první aplikace podle [wiki 01 ‐ Příprava prostředí](https://github.com/zizanek/github-tz-pma-2026/wiki/01-%E2%80%90-P%C5%99%C3%ADprava-prost%C5%99ed%C3%AD) (Empty Views Activity, XML + Kotlin) |
| [`MyApp001aDiceThrowXML/`](MyApp001aDiceThrowXML) | **Hoď kostkou** – XML + Kotlin, přístup k prvkům přes `findViewById` ([úkol 003](https://github.com/zizanek/github-tz-pma-2026/issues/3)) |
| [`MyApp001bDiceThrowCOMPOSE/`](MyApp001bDiceThrowCOMPOSE) | **Hoď kostkou** – Jetpack Compose, rozhraní definované v Kotlinu ([úkol 003](https://github.com/zizanek/github-tz-pma-2026/issues/3)) |
| [`MyApp004Objednavka/`](MyApp004Objednavka) | **Objednávka nože** – LinearLayout, View Binding, lokalizace EN/CS/DE, výpočet ceny ([úkol 004](https://github.com/zizanek/github-tz-pma-2026/issues/4)) |

---

## Úkol 003 – Hoď kostkou (XML a Jetpack Compose)

Dvě aplikace se stejnou funkcí: po stisku **Hodit** se kostka desetkrát náhodně změní
(prodleva 250 ms) a pak ukáže výsledný hod. Během animace je tlačítko zakázané,
obsah je vystředěný a odsazený od systémových lišt.

| XML + Kotlin | Jetpack Compose |
|:---:|:---:|
| <img src="MyApp001aDiceThrowXML/screenshots/xml.png" width="260" alt="XML verze"> | <img src="MyApp001bDiceThrowCOMPOSE/screenshots/compose.png" width="260" alt="Compose verze"> |

### Jak každá varianta aktualizuje zobrazenou kostku

**XML (imperativně)** – vzhled je v `activity_main.xml`, kód si prvek najde
a jeho text **ručně přepíše**:

```kotlin
tvDice = findViewById(R.id.tvDice)
// ...
tvDice.text = diceSymbols[diceValue - 1]   // přepíše text kostky
btnRoll.isEnabled = false                  // ručně vypne tlačítko
```

Každou změnu musí kód do UI promítnout sám – i statistiky se po hodu
aktualizují voláním `updateStatistics()`.

**Compose (deklarativně)** – UI je funkce, která popisuje, jak obrazovka vypadá
pro aktuální **stav**. Kód mění jen stavové proměnné, překreslení zařídí Compose sám:

```kotlin
var diceValue by rememberSaveable { mutableStateOf(1) }
// ...
Text(text = diceSymbols[diceValue - 1])     // kostka je odvozená ze stavu
Button(enabled = !isRolling, ...)           // tlačítko také
// ...
diceValue = (1..6).random()                 // změna stavu → automatické překreslení
```

| | XML + `findViewById` | Jetpack Compose |
|---|---|---|
| Kde je vzhled | `activity_main.xml` | `@Composable` funkce v Kotlinu |
| Změna kostky | `tvDice.text = …` | `diceValue = …` (stav) |
| Zakázání tlačítka | `btnRoll.isEnabled = false` | `enabled = !isRolling` |
| Animace | `lifecycleScope.launch` + `delay` | `rememberCoroutineScope()` + `delay` |
| Odsazení od lišt | `setOnApplyWindowInsetsListener` | `Modifier.safeDrawingPadding()` |
| Stav při otočení telefonu | `onSaveInstanceState` | `rememberSaveable` |

### Vylepšení (návazný úkol)

V obou verzích navíc:

- **statistiky** – počet hodů, součet a průměr všech hodů,
- **četnost** – kolikrát padla která hodnota,
- **historie** – posledních 10 hodů (nejnovější vlevo) a tlačítko *Vymazat historii*,
- **zachování stavu** při otočení telefonu,
- jednotné barvy (`#352060` na `#F5F3FF`), v XML verzi definované v `colors.xml`.

---

## Úkol 004 – Objednávka nože

Jednoduchý objednávkový systém na vlastní téma: **nože z Counter-Strike 2**
(Butterfly, Karambit, Skeleton – skin Fade). Postup a commity: issue [#4](https://github.com/tomaslansky/github-tl-pma-2026/issues/4).

| Čeština | English (výchozí) | Deutsch |
|:---:|:---:|:---:|
| <img src="MyApp004Objednavka/screenshots/objednavka_cs.png" width="240" alt="Česká verze"> | <img src="MyApp004Objednavka/screenshots/objednavka_en.png" width="240" alt="Anglická verze"> | <img src="MyApp004Objednavka/screenshots/objednavka_de.png" width="240" alt="Německá verze"> |

### Základ aplikace

- **LinearLayout** (vertikální): nadpis, `RadioGroup` s výběrem nože, `CheckBox`y s doplňky
  (StatTrak™, Name Tag, Factory New), tlačítko **Objednat**, žlutý souhrn objednávky a obrázek.
- **RadioButton mění obrázek** nože (`binding.ivKnife.setImageResource(...)`).
- **Objednat** vypíše souhrn (vybraný nůž + zaškrtnuté doplňky) a zobrazí `Toast`.
- **View Binding** místo `findViewById` – `buildFeatures { viewBinding = true }`,
  ke všem prvkům se přistupuje přes `binding.<id>`.
- Obrázky pojmenované podle konvence `img_knife_*.png` ([wiki 000d](https://github.com/zizanek/github-tz-pma-2026/wiki/000d-%E2%80%90-Pravidla-a-konvence-pro-pojmenov%C3%A1n%C3%AD-soubor%C5%AF-ve-slo%C5%BEce-%60drawable%60)).

### Lokalizace

| Složka | Jazyk |
|---|---|
| `res/values/strings.xml` | angličtina – výchozí jazyk (fallback pro všechny ostatní jazyky) |
| `res/values-cs/strings.xml` | čeština |
| `res/values-de/strings.xml` | němčina |

V layoutu ani v kódu nejsou žádné texty natvrdo. Dynamické texty (souhrn, Toast, cena)
se skládají přes `getString()` se zástupnými znaky, takže slovosled určuje překlad:

```xml
<string name="order_summary">Souhrn objednávky: %1$s\nCena celkem: %2$,d Kč</string>
```

```kotlin
binding.tvOrder.text = getString(R.string.order_summary, items, price)
```

Ověřeno změnou jazyka aplikace v emulátoru (snímky výše).

### Vlastní rozšíření – výpočet ceny

- Každý nůž i doplněk má cenu (orientační ceny ze Steam Marketu v Kč).
- **Aktuální cena** se přepočítá při každé změně výběru (`setOnCheckedChangeListener`
  na `RadioGroup` i všech `CheckBox`ech).
- Celková cena je i v **souhrnu** a v **Toastu** po odeslání objednávky.
- Formát `%,d` odděluje tisíce podle jazyka: `69 000 Kč` / `69,000 CZK` / `69.000 CZK`.
