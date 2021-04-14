package com.example.feedy1

import android.animation.ValueAnimator
import android.content.Intent

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)
        startLoaderAnimate()
        //4second splash time
        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@SplashScreenActivity, WebViewActivity::class.java))
            //finish this activity

            finish()
            endLoaderAnimate()
        }, 4000)

    }


    private fun endLoaderAnimate() {

        loaderImage.visibility = View.VISIBLE
        loaderImage.clearAnimation()
    }


    private fun startLoaderAnimate() {
        val objectAnimator = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                val startHeight = 170
                val newHeight = (startHeight + (startHeight + 40) * interpolatedTime).toInt()
                loaderImage.layoutParams.height = newHeight
                loaderImage.requestLayout()
            }

            override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
                super.initialize(width, height, parentWidth, parentHeight)
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        objectAnimator.repeatCount = 1

        objectAnimator.repeatMode = ValueAnimator.RESTART
        objectAnimator.duration = 1000
        loaderImage.startAnimation(objectAnimator)
    }
}
