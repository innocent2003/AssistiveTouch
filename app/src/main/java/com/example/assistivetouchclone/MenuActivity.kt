package com.example.assistivetouchclone

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MenuActivity : BaseActivity() {

    private val selectedActions = mapOf(
        0 to mutableMapOf<Int, SystemActionItem?>(),
        1 to mutableMapOf<Int, SystemActionItem?>()
    )
    private val systemActions = SystemActionCatalog.getSystemActions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        loadActions()

        // ViewPager2
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Text hiển thị số trang
        val pageIndicator = findViewById<TextView>(R.id.pageIndicator)

        // Số lượng trang
        val pages = 2

        val adapter = MenuPagerAdapter(
            pages,
            selectedActions,
            onItemClick = { pageIndex, itemIndex ->
                handlePageClick(pageIndex, itemIndex)
            },
            onItemLongClick = { pageIndex, itemIndex ->
                handlePageLongClick(pageIndex, itemIndex)
            }
        )

        // Thiết lập Adapter cho ViewPager2
        viewPager.adapter = adapter

        // Hiển thị trang hiện tại
        pageIndicator.text = "1/$pages MAIN"

        // Theo dõi khi chuyển trang
        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    pageIndicator.text =
                        "${position + 1}/$pages MAIN"
                }
            }
        )
    }

    private fun handlePageClick(pageIndex: Int, index: Int) {
        val actionsForPage = selectedActions[pageIndex] ?: return
        val assignedAction = actionsForPage[index]

        if (assignedAction != null) {
            assignedAction.action(this)
            return
        }

        if (index < PAGE_SLOT_COUNT) {
            showSystemActionPicker(pageIndex, index)
        } else {
            return
        }
    }

    private fun handlePageLongClick(pageIndex: Int, index: Int): Boolean {
        val actionsForPage = selectedActions[pageIndex] ?: return false
        if (actionsForPage.containsKey(index)) {
            actionsForPage.remove(index)
            getSharedPreferences(STORAGE_NAME, MODE_PRIVATE)
                .edit()
                .remove(actionKey(pageIndex, index))
                .apply()
            (findViewById<ViewPager2>(R.id.viewPager).adapter as? MenuPagerAdapter)?.notifyDataSetChanged()
            return true
        }

        return false
    }

    private fun showSystemActionPicker(pageIndex: Int, slotIndex: Int) {
        val labels = systemActions.map { it.label }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Chọn hành động")
            .setItems(labels) { _, which ->
                selectedActions[pageIndex]?.set(slotIndex, systemActions[which])
                getSharedPreferences(STORAGE_NAME, MODE_PRIVATE)
                    .edit()
                    .putInt(actionKey(pageIndex, slotIndex), which)
                    .apply()
                (findViewById<ViewPager2>(R.id.viewPager).adapter as? MenuPagerAdapter)?.notifyDataSetChanged()
            }
            .show()
    }

    private fun loadActions() {
        val preferences = getSharedPreferences(STORAGE_NAME, MODE_PRIVATE)
        val editor = preferences.edit()

        for (pageIndex in 0 until PAGE_COUNT) {
            for (slotIndex in 0 until PAGE_SLOT_COUNT) {
                val key = actionKey(pageIndex, slotIndex)
                val actionIndex = if (pageIndex == 0 && !preferences.contains(key)) {
                    DEFAULT_PAGE_ONE_ACTIONS[slotIndex]?.also { editor.putInt(key, it) } ?: -1
                } else {
                    preferences.getInt(key, -1)
                }
                if (actionIndex in systemActions.indices) {
                    selectedActions[pageIndex]?.set(slotIndex, systemActions[actionIndex])
                }
            }
        }

        editor.apply()
    }

    private fun actionKey(pageIndex: Int, slotIndex: Int): String =
        "page${pageIndex + 1}_action_$slotIndex"

    companion object {
        private const val STORAGE_NAME = "AssistiveSettings"
        private const val PAGE_COUNT = 2
        private const val PAGE_SLOT_COUNT = 9
        private val DEFAULT_PAGE_ONE_ACTIONS = mapOf(
            1 to 11, // Lock
            3 to 7,  // Favourite
            4 to 12, // Home
            5 to 8,  // Setting
            6 to 9   // Main Activity
        )
    }


    /**
     * Adapter cho ViewPager2
     */
    private class MenuPagerAdapter(
        private val pages: Int,
        private val selectedActions: Map<Int, Map<Int, SystemActionItem?>>,
        private val onItemClick:
            (pageIndex: Int, itemIndex: Int) -> Unit,
        private val onItemLongClick:
            (pageIndex: Int, itemIndex: Int) -> Boolean
    ) : RecyclerView.Adapter<MenuPagerAdapter.PageViewHolder>() {


        /**
         * Tạo ViewHolder
         */
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PageViewHolder {

            val view = LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.menu_page,
                    parent,
                    false
                )

            return PageViewHolder(view)
        }


        /**
         * Bind dữ liệu vào ViewHolder
         */
        override fun onBindViewHolder(
            holder: PageViewHolder,
            position: Int
        ) {

            // position chính là pageIndex
            holder.bind(
                position,
                selectedActions,
                onItemClick,
                onItemLongClick
            )
        }


        /**
         * Tổng số trang
         */
        override fun getItemCount(): Int {
            return pages
        }


        /**
         * ViewHolder của từng trang
         */
        class PageViewHolder(
            itemView: View
        ) : RecyclerView.ViewHolder(itemView) {


            /**
             * Danh sách ImageView
             *
             * Đây là ImageView thực tế
             */
            private val icons: List<ImageView> = listOf(

                itemView.findViewById(R.id.icon1),
                itemView.findViewById(R.id.icon2),
                itemView.findViewById(R.id.icon3),
                itemView.findViewById(R.id.icon4),
                itemView.findViewById(R.id.icon5),
                itemView.findViewById(R.id.icon6),
                itemView.findViewById(R.id.icon7),
                itemView.findViewById(R.id.icon8),
                itemView.findViewById(R.id.icon9)
            )


            /**
             * Danh sách TextView
             */
            private val texts: List<TextView> = listOf(

                itemView.findViewById(R.id.text1),
                itemView.findViewById(R.id.text2),
                itemView.findViewById(R.id.text3),
                itemView.findViewById(R.id.text4),
                itemView.findViewById(R.id.text5),
                itemView.findViewById(R.id.text6),
                itemView.findViewById(R.id.text7),
                itemView.findViewById(R.id.text8),
                itemView.findViewById(R.id.text9)
            )


            /**
             * Danh sách LinearLayout
             * Dùng để bắt sự kiện click
             */
            private val itemLayouts: List<LinearLayout> = listOf(

                itemView.findViewById(R.id.item1),
                itemView.findViewById(R.id.item2),
                itemView.findViewById(R.id.item3),
                itemView.findViewById(R.id.item4),
                itemView.findViewById(R.id.item5),
                itemView.findViewById(R.id.item6),
                itemView.findViewById(R.id.item7),
                itemView.findViewById(R.id.item8),
                itemView.findViewById(R.id.item9)
            )


            /**
             * Hiển thị dữ liệu của từng page
             */
            fun bind(
                pageIndex: Int,
                selectedActions: Map<Int, Map<Int, SystemActionItem?>>,
                onItemClick:
                    (pageIndex: Int, itemIndex: Int) -> Unit,
                onItemLongClick:
                    (pageIndex: Int, itemIndex: Int) -> Boolean
            ) {


                // ==============================
                // TRANG 1
                // ==============================

                val actionsForPage = selectedActions[pageIndex].orEmpty()
                val labels = List(9) { index -> actionsForPage[index]?.label ?: "+" }
                val iconResources = List(9) { index ->
                    actionsForPage[index]?.iconRes ?: android.R.drawable.ic_input_add
                }


                // ==============================
                // GÁN ICON + TEXT + CLICK
                // ==============================

                for (i in 0 until 9) {

                    itemLayouts[i].visibility =
                        if (pageIndex == 1 && i == 4) View.INVISIBLE else View.VISIBLE

                    // Gán TextView
                    texts[i].text = labels[i]


                    // Gán ImageView
                    //
                    // icons[i] là ImageView
                    // iconResources[i] là ID của drawable
                    //
                    // Đây chính là phần sửa lỗi
                    icons[i].setImageResource(
                        iconResources[i]
                    )


                    // Lưu index của item
                    val idx = i


                    // Xử lý sự kiện click
                    itemLayouts[i].setOnClickListener {
                        onItemClick(pageIndex, idx)
                    }

                    itemLayouts[i].setOnLongClickListener {
                        onItemLongClick(pageIndex, idx)
                    }
                }
            }
        }
    }
}