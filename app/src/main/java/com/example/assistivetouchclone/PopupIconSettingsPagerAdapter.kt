package com.example.assistivetouchclone

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PopupIconSettingsPagerAdapter(
    activity: AppCompatActivity,
    private val pages: List<PopupIconSettingsFragment>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = pages.size
    override fun createFragment(position: Int): Fragment = pages[position]
}
