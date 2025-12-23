package com.example.student_game

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class GameActivity : ComponentActivity() {

    private lateinit var bgView: ImageView   // движущийся фон
    private lateinit var wView: ImageView    // фиксированная "стенка"
    private lateinit var keysUI: LinearLayout
    private lateinit var windowUI: LinearLayout

    private var cameraPosition = CameraPosition.LEFT

    // --- НАСТРОЙКИ ---
    private var bgOffsetFactor = 0.27f      // сила сдвига bgView
    private var animationDuration = 600L    // длительность анимации
    // ------------------

    enum class CameraPosition {
        LEFT, RIGHT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        bgView = findViewById(R.id.backgroundView)
        wView = findViewById(R.id.WView)    // фиксированная стенка
        keysUI = findViewById(R.id.keysUI)
        windowUI = findViewById(R.id.windowUI)

        val btnLeft = findViewById<Button>(R.id.btnLeft)
        val btnRight = findViewById<Button>(R.id.btnRight)

        // Изначальная позиция камеры
        bgView.post {
            cameraPosition = CameraPosition.RIGHT
            applyCameraPosition(animated = false)
        }

        btnLeft.setOnClickListener {
            cameraPosition = CameraPosition.LEFT
            applyCameraPosition(animated = true)
        }

        btnRight.setOnClickListener {
            cameraPosition = CameraPosition.RIGHT
            applyCameraPosition(animated = true)
        }
    }

    private fun applyCameraPosition(animated: Boolean) {
        // --- смещение bgView ---
        val screenWidth = resources.displayMetrics.widthPixels
        val imageWidth = bgView.drawable.intrinsicWidth
        val scale = bgView.height.toFloat() / bgView.drawable.intrinsicHeight
        val scaledImageWidth = imageWidth * scale
        val maxOffset = scaledImageWidth - screenWidth

        val targetX = when (cameraPosition) {
            CameraPosition.LEFT -> 0f
            CameraPosition.RIGHT -> -maxOffset * bgOffsetFactor
        }

        if (animated) {
            ObjectAnimator.ofFloat(bgView, "translationX", bgView.translationX, targetX)
                .setDuration(animationDuration)
                .start()
        } else {
            bgView.translationX = targetX
        }

        // wView остаётся **фиксированной** — translationX не меняется

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
