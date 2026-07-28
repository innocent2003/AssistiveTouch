package com.example.assistivetouchclone

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class PopupIconSettingsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup_icon_settings)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val pages = listOf(
            PopupIconSettingsFragment.newInstance("Home", R.drawable.home_24px),
            PopupIconSettingsFragment.newInstance("Lock", R.drawable.block_24px),
            PopupIconSettingsFragment.newInstance("Favourite", R.drawable.kid_star_24px),
            PopupIconSettingsFragment.newInstance("Screen", R.drawable.circle_24px),
            PopupIconSettingsFragment.newInstance("Setting", R.drawable.settings_24px)
        )

        viewPager.adapter = PopupIconSettingsPagerAdapter(this, pages)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = pages[position].title
//        }.attach()
    }
}
