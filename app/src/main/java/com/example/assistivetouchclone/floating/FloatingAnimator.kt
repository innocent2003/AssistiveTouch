package com.example.assistivetouchclone.floating

import android.view.View

object FloatingAnimator {

    fun hideFloating(view: View){

        view.animate()

            .scaleX(.5f)

            .scaleY(.5f)

            .alpha(0f)

            .setDuration(120)

            .start()

    }

    fun showFloating(view: View){

        view.scaleX=.5f

        view.scaleY=.5f

        view.alpha=0f

        view.animate()

            .scaleX(1f)

            .scaleY(1f)

            .alpha(1f)

            .setDuration(180)

            .start()

    }

}