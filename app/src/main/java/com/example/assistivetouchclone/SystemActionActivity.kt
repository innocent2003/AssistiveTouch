package com.example.assistivetouchclone

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SystemActionActivity : BaseActivity() {

    private lateinit var actionsRecyclerView: RecyclerView
    private lateinit var btnBack: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_action)

        btnBack = findViewById(R.id.btnBack)
        actionsRecyclerView = findViewById(R.id.actionsRecyclerView)

        btnBack.setOnClickListener { finish() }

        val actions = SystemActionCatalog.getSystemActions()

        actionsRecyclerView.layoutManager = LinearLayoutManager(this)
        actionsRecyclerView.adapter = SystemActionAdapter(actions)
    }
}

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
            itemView.setOnClickListener { item.action(itemView.context) }
        }
    }
}
