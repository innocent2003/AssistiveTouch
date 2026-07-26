package com.example.assistivetouchclone

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class IconActivity : AppCompatActivity() {

    private lateinit var icon1: ImageView
    private lateinit var icon2: ImageView
    private lateinit var icon3: ImageView
    private lateinit var icon4: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_icon)

        icon1 = findViewById(R.id.icon1)
        icon2 = findViewById(R.id.icon2)
        icon3 = findViewById(R.id.icon3)
        icon4 = findViewById(R.id.icon4)

        icon1.setOnClickListener {
            selectIcon(R.drawable.mood_bad_24px)
        }

        icon2.setOnClickListener {
            selectIcon(R.drawable.mood_heart_24px)
        }

        icon3.setOnClickListener {
            selectIcon(R.drawable.sentiment_calm_24px)
        }

        icon4.setOnClickListener {
            selectIcon(R.drawable.sentiment_satisfied_24px)
        }
    }

    private fun selectIcon(iconResId: Int) {

        val preferences = getSharedPreferences(
            "AssistiveSettings",
            MODE_PRIVATE
        )

        preferences.edit()
            .putInt("selected_icon", iconResId)
            .apply()

        finish()
    }
}