package com.example.assistivetouchclone

import android.os.Bundle
import android.widget.ImageView
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity

class IconActivity : BaseActivity() {

    private val iconResources = listOf(
        R.drawable.mood_bad_24px,
        R.drawable.mood_heart_24px,
        R.drawable.sentiment_calm_24px,
        R.drawable.sentiment_satisfied_24px,
        R.drawable.ic_touch_01,
        R.drawable.ic_touch_02,
        R.drawable.ic_touch_03,
        R.drawable.ic_touch_04,
        R.drawable.ic_touch_05,
        R.drawable.ic_touch_06,
        R.drawable.ic_touch_07,
        R.drawable.ic_touch_08,
        R.drawable.ic_touch_09,
        R.drawable.ic_touch_10,
        R.drawable.ic_touch_11,
        R.drawable.ic_touch_12,
        R.drawable.ic_touch_13,
        R.drawable.ic_touch_14,
        R.drawable.ic_touch_15,
        R.drawable.ic_touch_16,
        R.drawable.ic_touch_17,
        R.drawable.ic_touch_18,
        R.drawable.ic_touch_19,
        R.drawable.ic_touch_20,
        R.drawable.ic_touch_21,
        R.drawable.ic_touch_22,
        R.drawable.ic_touch_23,
        R.drawable.ic_touch_24,
        R.drawable.ic_touch_25,
        R.drawable.ic_touch_26,
        R.drawable.ic_touch_27,
        R.drawable.ic_touch_28,
        R.drawable.ic_touch_29,
        R.drawable.ic_touch_30,
        R.drawable.ic_touch_31,
        R.drawable.ic_touch_32,
        R.drawable.ic_touch_33,
        R.drawable.ic_touch_34,
        R.drawable.ic_touch_35,
        R.drawable.ic_touch_36,
        R.drawable.ic_touch_37,
        R.drawable.ic_touch_38,
        R.drawable.ic_touch_39,
        R.drawable.ic_touch_40
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_icon)

        val iconGrid = findViewById<GridLayout>(R.id.iconGrid)
        iconResources.forEach { iconResId ->
            val iconView = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = dpToPx(85)
                    height = dpToPx(85)
                    setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10))
                }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setImageResource(iconResId)
                setOnClickListener { selectIcon(iconResId) }
            }
            iconGrid.addView(iconView)
        }
    }

    private fun dpToPx(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

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