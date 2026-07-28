package com.example.assistivetouchclone

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class PopupIconSettingsFragment : Fragment() {

    private lateinit var iconImage: ImageView
    private lateinit var titleText: TextView
    private lateinit var labelText: TextView
    private lateinit var preferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        preferences = requireContext().getSharedPreferences("AssistiveSettings", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_popup_icon_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconImage = view.findViewById(R.id.iconImage)
        titleText = view.findViewById(R.id.titleText)
        labelText = view.findViewById(R.id.labelText)

        val title = requireArguments().getString(ARG_TITLE)!!
        val iconRes = requireArguments().getInt(ARG_ICON_RES)

        titleText.text = title
        iconImage.setImageResource(iconRes)
        labelText.text = "Choose which icon should appear for $title in popup"
    }

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_ICON_RES = "arg_icon_res"

        fun newInstance(title: String, iconRes: Int): PopupIconSettingsFragment {
            val fragment = PopupIconSettingsFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_TITLE, title)
                putInt(ARG_ICON_RES, iconRes)
            }
            return fragment
        }
    }
}
