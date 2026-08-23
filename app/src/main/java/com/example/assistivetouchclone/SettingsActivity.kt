package com.example.assistivetouchclone

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val root = findViewById<View>(R.id.settingsRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(view.paddingLeft, bars.top, view.paddingRight, bars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val speedGroup = findViewById<RadioGroup>(R.id.speedGroup)
        val preferences = getSharedPreferences("AssistiveSettings", MODE_PRIVATE)
        speedGroup.check(
            when (preferences.getFloat("transition_speed", 2.0f)) {
                0.5f -> R.id.speed05
                1.0f -> R.id.speed10
                else -> R.id.speed20
            }
        )
        speedGroup.setOnCheckedChangeListener { _, checkedId ->
            val speed = when (checkedId) {
                R.id.speed05 -> 0.5f
                R.id.speed10 -> 1.0f
                else -> 2.0f
            }
            preferences.edit().putFloat("transition_speed", speed).apply()
        }
    }
}