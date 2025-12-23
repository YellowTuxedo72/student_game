package com.example.student_game

import android.animation.ObjectAnimator
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class GameActivity : ComponentActivity() {

    private lateinit var bgView: ImageView
    private lateinit var keysUI: LinearLayout
    private lateinit var windowUI: LinearLayout

    private val matrix = Matrix()

    private var cameraPosition = CameraPosition.LEFT

    enum class CameraPosition {
        LEFT, RIGHT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        bgView = findViewById(R.id.backgroundView)
        keysUI = findViewById(R.id.keysUI)
        windowUI = findViewById(R.id.windowUI)

        val btnLeft = findViewById<Button>(R.id.btnLeft)
        val btnRight = findViewById<Button>(R.id.btnRight)

        bgView.post {
            cameraPosition = CameraPosition.RIGHT
            applyCameraPosition(false)
        }

        btnLeft.setOnClickListener {
            cameraPosition = CameraPosition.LEFT
            applyCameraPosition(true)
        }

        btnRight.setOnClickListener {
            cameraPosition = CameraPosition.RIGHT
            applyCameraPosition(true)
        }
    }

    private fun applyCameraPosition(animated: Boolean) {
        val screenWidth = resources.displayMetrics.widthPixels
        val imageWidth = bgView.drawable.intrinsicWidth
        val scale = bgView.height.toFloat() / bgView.drawable.intrinsicHeight
        val scaledImageWidth = imageWidth * scale

        val maxOffset = scaledImageWidth - screenWidth

        val targetX = when (cameraPosition) {
            CameraPosition.LEFT -> 0f
            CameraPosition.RIGHT -> -maxOffset
        }

        if (animated) {
            ObjectAnimator.ofFloat(bgView, "translationX", bgView.translationX, targetX)
                .setDuration(600)
                .start()
        } else {
            bgView.translationX = targetX
        }

        updateUI()
    }



    private fun updateUI() {
        when (cameraPosition) {
            CameraPosition.LEFT -> {
                keysUI.visibility = View.VISIBLE
                windowUI.visibility = View.GONE
            }
            CameraPosition.RIGHT -> {
                keysUI.visibility = View.GONE
                windowUI.visibility = View.VISIBLE
            }
        }
    }
}
