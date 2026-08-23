package com.example.assistivetouchclone

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.Gravity
import android.widget.ImageButton
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : BaseActivity() {
    private val backgroundColors = intArrayOf(
        Color.rgb(82, 105, 191), Color.rgb(142, 108, 91), Color.rgb(45, 143, 226), Color.rgb(148, 188, 83),
        Color.rgb(169, 169, 169), Color.rgb(230, 79, 65), Color.rgb(19, 161, 225), Color.rgb(207, 213, 52),
        Color.rgb(34, 34, 34), Color.rgb(247, 66, 108), Color.rgb(17, 170, 202), Color.rgb(255, 221, 65),
        Color.rgb(58, 67, 74), Color.rgb(166, 75, 187), Color.rgb(16, 145, 151), Color.rgb(255, 188, 27),
        Color.rgb(112, 143, 158), Color.rgb(108, 79, 188), Color.rgb(84, 172, 92), Color.rgb(255, 158, 18)
    )

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
        findViewById<View>(R.id.backgroundColorCard).setOnClickListener { showBackgroundColorMenu() }

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

    private fun showBackgroundColorMenu() {
        val preferences = getSharedPreferences("AssistiveSettings", MODE_PRIVATE)
        val selectedColor = preferences.getInt("background_color", backgroundColors[1])
        val grid = GridLayout(this).apply {
            columnCount = 4
            rowCount = 5
            alignmentMode = GridLayout.ALIGN_BOUNDS
            setPadding(16.dp, 22.dp, 16.dp, 20.dp)
        }

        val swatchSize = ((resources.displayMetrics.widthPixels - 84.dp) / 4)
            .coerceIn(52.dp, 78.dp)

        backgroundColors.forEach { color ->
            val item = FrameLayout(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = swatchSize + 8.dp
                    height = swatchSize + 8.dp
                    setMargins(4.dp, 4.dp, 4.dp, 4.dp)
                }
                contentDescription = getString(R.string.background_color)
            }
            val circle = TextView(this).apply {
                layoutParams = FrameLayout.LayoutParams(swatchSize, swatchSize)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                gravity = Gravity.CENTER
                text = if (color == selectedColor) "✓" else ""
                textSize = 30f
                setTextColor(Color.WHITE)
            }
            item.addView(circle)
            item.setOnClickListener {
                preferences.edit().putInt("background_color", color).apply()
                grid.tag = color
                (item.parent as? View)?.let { parent ->
                    (parent as? GridLayout)?.let { refreshColorChecks(it, color) }
                }
            }
            grid.addView(item)
        }

        val title = TextView(this).apply {
            text = getString(R.string.background_color)
            textSize = 26f
            setTextColor(Color.rgb(40, 40, 40))
            setPadding(24.dp, 18.dp, 24.dp, 0)
        }
        val content = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_color_dialog)
            addView(title, android.widget.LinearLayout.LayoutParams(-1, 58.dp))
            addView(grid)
        }
        val dialog = AlertDialog.Builder(this).setView(content).create()
        dialog.setOnShowListener {
            dialog.window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setLayout(-1, -2)
                attributes = attributes.apply { gravity = Gravity.BOTTOM }
            }
        }
        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(-1, -2)
            attributes = attributes.apply { gravity = Gravity.BOTTOM }
        }
    }

    private fun refreshColorChecks(grid: GridLayout, selectedColor: Int) {
        for (index in 0 until grid.childCount) {
            val item = grid.getChildAt(index) as FrameLayout
            (item.getChildAt(0) as TextView).text = if (backgroundColors[index] == selectedColor) "✓" else ""
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}