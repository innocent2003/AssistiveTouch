package com.example.assistivetouchclone

import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.GridLayout

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
        iconGrid.columnCount = 4

        val selectedIcon = getSharedPreferences("AssistiveSettings", MODE_PRIVATE)
            .getInt("selected_icon", R.drawable.mood_bad_24px)

        iconResources.forEach { iconResId ->
            val cell = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = dpToPx(96)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
                }
                background = iconCellBackground(iconResId == selectedIcon)
                setOnClickListener { selectIcon(iconResId) }
            }

            val iconView = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(dpToPx(72), dpToPx(72), Gravity.CENTER)
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(iconResId)
            }
            cell.addView(iconView)
            iconGrid.addView(cell)
        }
    }

    private fun iconCellBackground(isSelected: Boolean): GradientDrawable =
        GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            cornerRadius = dpToPx(10).toFloat()
            if (isSelected) {
                setStroke(dpToPx(2), Color.rgb(66, 160, 255))
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