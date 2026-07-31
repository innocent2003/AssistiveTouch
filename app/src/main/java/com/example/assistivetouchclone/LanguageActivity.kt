package com.example.assistivetouchclone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class LanguageActivity : BaseActivity() {
    private lateinit var languageGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val preferences = getSharedPreferences("AssistiveSettings", MODE_PRIVATE)

        languageGroup = findViewById(R.id.languageRadioGroup)
        val continueButton = findViewById<Button>(R.id.btnContinue)

        continueButton.setOnClickListener {
            val selectedLanguage = when (languageGroup.checkedRadioButtonId) {
                R.id.radioVietnamese -> "vi"
                R.id.radioEnglish -> "en"
                R.id.radioArabic -> "ar"
                R.id.radioGerman -> "de"
                R.id.radioFinnish -> "fi"
                R.id.radioIndonesian -> "id"
                R.id.radioPortuguese -> "pt"
                R.id.radioMalay -> "ms"
                R.id.radioTagalog -> "tl"
                else -> "default"
            }

            preferences.edit()
                .putString("selected_language", selectedLanguage)
                .apply()

            launchMainActivity()
        }
    }

    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
