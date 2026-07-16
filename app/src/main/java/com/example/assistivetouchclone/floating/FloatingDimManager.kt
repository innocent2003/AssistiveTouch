package com.example.assistivetouchclone.floating

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.*

import com.example.assistivetouchclone.R

class FloatingDimManager(
    context: Context
) {

    private val wm =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val dimView =
        LayoutInflater.from(context)
            .inflate(R.layout.layout_dim, null)

    fun show(onClick:()->Unit){

        val params =
            WindowManager.LayoutParams(

                WindowManager.LayoutParams.MATCH_PARENT,

                WindowManager.LayoutParams.MATCH_PARENT,

                if(Build.VERSION.SDK_INT>=26)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,

                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                PixelFormat.TRANSLUCENT
            )

        dimView.setOnClickListener {

            onClick()

        }

        wm.addView(dimView,params)

        dimView.alpha=0f

        dimView.animate()
            .alpha(1f)
            .setDuration(180)
            .start()

    }

    fun hide(){

        dimView.animate()

            .alpha(0f)

            .setDuration(150)

            .withEndAction{

                wm.removeView(dimView)

            }

            .start()

    }

}