package com.example.assistivetouchclone

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class MenuActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // ViewPager2
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        // Text hiển thị số trang
        val pageIndicator = findViewById<TextView>(R.id.pageIndicator)

        // Số lượng trang
        val pages = 2

        // Thiết lập Adapter cho ViewPager2
        viewPager.adapter = MenuPagerAdapter(pages) { pageIndex, itemIndex ->

            when (pageIndex) {

                // Trang 1
                0 -> {
                    handleMainPageClick(itemIndex)
                }

                // Trang 2
                1 -> {
                    handleSecondaryPageClick(itemIndex)
                }
            }
        }

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

    /**
     * Xử lý sự kiện click trên trang chính
     */
    private fun handleMainPageClick(index: Int) {

        when (index) {

            // Item 1
            0 -> {
                Toast.makeText(
                    this,
                    "Chưa cấu hình",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Item 2 - Khóa
            1 -> {
                Toast.makeText(
                    this,
                    "Khóa (device lock)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Item 3
            2 -> {
                Toast.makeText(
                    this,
                    "Chưa cấu hình",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Item 4 - Yêu thích
            3 -> {
                Toast.makeText(
                    this,
                    "Yêu thích",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Item 5 - Chính
            4 -> {
                startActivity(
                    Intent(
                        this,
                        ProductListActivity::class.java
                    )
                )
            }

            // Item 6 - Cài đặt
            5 -> {
                startActivity(
                    Intent(
                        this,
                        IconActivity::class.java
                    )
                )
            }

            // Item 7
            6 -> {
                Toast.makeText(
                    this,
                    "Chưa cấu hình",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Item 8 - Màn hình
            7 -> {
                Toast.makeText(
                    this,
                    "Màn hình action",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Item 9 - Nguồn
            8 -> {
                Toast.makeText(
                    this,
                    "Nguồn (power)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Trường hợp khác
            else -> {
                Toast.makeText(
                    this,
                    "Chưa cấu hình",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Xử lý sự kiện click trên trang phụ
     */
    private fun handleSecondaryPageClick(index: Int) {

        Toast.makeText(
            this,
            "Secondary page item ${index + 1}",
            Toast.LENGTH_SHORT
        ).show()
    }


    /**
     * Adapter cho ViewPager2
     */
    private class MenuPagerAdapter(
        private val pages: Int,
        private val onItemClick:
            (pageIndex: Int, itemIndex: Int) -> Unit
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
                onItemClick
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
                onItemClick:
                    (pageIndex: Int, itemIndex: Int) -> Unit
            ) {


                // ==============================
                // TRANG 1
                // ==============================

                val labelsPage0 = listOf(

                    "",
                    "Khóa",
                    "",
                    "Yêu thích",
                    "Chính",
                    "Cài đặt",
                    "",
                    "Màn\nhình",
                    "Nguồn"
                )


                val iconsPage0 = listOf(

                    android.R.drawable.ic_input_add,

                    android.R.drawable.ic_lock_lock,

                    android.R.drawable.ic_input_add,

                    android.R.drawable.star_big_on,

                    android.R.drawable.ic_menu_view,

                    android.R.drawable.ic_menu_manage,

                    android.R.drawable.ic_input_add,

                    android.R.drawable.btn_radio,

                    android.R.drawable.ic_lock_power_off
                )


                // ==============================
                // TRANG 2
                // ==============================

                val labelsPage1 = listOf(

                    "+",
                    "+",
                    "+",
                    "+",
                    "+",
                    "+",
                    "+",
                    "+",
                    "+"
                )


                val iconsPage1 = List(9) {

                    android.R.drawable.ic_input_add

                }


                // ==============================
                // CHỌN DỮ LIỆU THEO TRANG
                // ==============================

                val labels = if (pageIndex == 0) {

                    labelsPage0

                } else {

                    labelsPage1

                }


                /**
                 * Đổi tên thành iconResources
                 *
                 * Tránh trùng với biến:
                 *
                 * private val icons: List<ImageView>
                 */
                val iconResources = if (pageIndex == 0) {

                    iconsPage0

                } else {

                    iconsPage1

                }


                // ==============================
                // GÁN ICON + TEXT + CLICK
                // ==============================

                for (i in 0 until 9) {


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

                        onItemClick(
                            pageIndex,
                            idx
                        )
                    }
                }
            }
        }
    }
}