package com.example.assistivetouchclone

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProductListActivity : BaseActivity() {

    private lateinit var appGridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        appGridView = findViewById(R.id.appGridView)

        val packageManager = packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { packageManager.getApplicationLabel(it).toString().lowercase() }

        val adapter = AppAdapter(this, installedApps, packageManager)
        appGridView.adapter = adapter
    }

    private class AppAdapter(
        context: Context,
        private val apps: List<android.content.pm.ApplicationInfo>,
        private val packageManager: PackageManager
    ) : ArrayAdapter<android.content.pm.ApplicationInfo>(context, 0, apps) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(
                R.layout.item_app,
                parent,
                false
            )

            val app = getItem(position) ?: return view
            val appIcon = view.findViewById<ImageView>(R.id.appIcon)
            val appName = view.findViewById<TextView>(R.id.appName)

            appIcon.setImageDrawable(packageManager.getApplicationIcon(app))
            appName.text = packageManager.getApplicationLabel(app).toString()

            return view
        }
    }
}
