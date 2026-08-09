package com.example.assistivetouchclone

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assistivetouchclone.utils.SystemAction

class SystemActionActivity : BaseActivity() {

    private lateinit var actionsRecyclerView: RecyclerView
    private lateinit var btnBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_action)

        btnBack = findViewById(R.id.btnBack)
        actionsRecyclerView = findViewById(R.id.actionsRecyclerView)

        btnBack.setOnClickListener { finish() }

        val actions = listOf(
            SystemActionItem("Open Wifi", R.drawable.wifi_24px) { SystemAction.openWifi(this) },
            SystemActionItem("Open Bluetooth", R.drawable.bluetooth_24px) { SystemAction.openBluetooth(this) },
            SystemActionItem("Open Display", R.drawable.mobile_rotate_lock_24px) { SystemAction.openDisplay(this) },
            SystemActionItem("Volume Up", R.drawable.volume_up_24px) { SystemAction.volumeUp(this) },
            SystemActionItem("Volume Down", R.drawable.volume_down_24px) { SystemAction.volumeDown(this) },
            SystemActionItem("Toggle Silent", R.drawable.notifications_active_24px) { SystemAction.toggleSilent(this) },
            SystemActionItem("Toggle Flash", R.drawable.highlight_24px) { SystemAction.toggleFlash(this) }
        )

        actionsRecyclerView.layoutManager = LinearLayoutManager(this)
        actionsRecyclerView.adapter = SystemActionAdapter(actions)
    }
}

data class SystemActionItem(
    val label: String,
    val iconRes: Int,
    val action: () -> Unit
)

class SystemActionAdapter(
    private val actions: List<SystemActionItem>
) : RecyclerView.Adapter<SystemActionAdapter.SystemActionViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): SystemActionViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_system_action, parent, false)
        return SystemActionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SystemActionViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int = actions.size

    class SystemActionViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val actionIcon: ImageView = itemView.findViewById(R.id.actionIcon)
        private val actionLabel: TextView = itemView.findViewById(R.id.actionLabel)

        fun bind(item: SystemActionItem) {
            actionIcon.setImageResource(item.iconRes)
            actionLabel.text = item.label
            itemView.setOnClickListener { item.action() }
        }
    }
}
