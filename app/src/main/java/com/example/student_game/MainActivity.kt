package com.example.student_game

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Фоновая анимация
        val bgView = findViewById<ImageView>(R.id.backgroundView)
        val animation = bgView.background as AnimationDrawable
        animation.start()

        // Эффект дрожания / смещения фона
        startCameraShake(bgView)

        // Кнопка "Играть"
        val newGameButton = findViewById<Button>(R.id.play_newButton)
        newGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startCameraShake(view: ImageView) {
        val shakeX = ObjectAnimator.ofFloat(view, "translationX", 0f, 4f, -4f, 3f, -3f, 0f)
        val shakeY = ObjectAnimator.ofFloat(view, "translationY", 0f, 3f, -3f, 4f, -4f, 0f)

        shakeX.duration = 6000
        shakeY.duration = 6000
        shakeX.repeatCount = ObjectAnimator.INFINITE
        shakeY.repeatCount = ObjectAnimator.INFINITE

        val set = AnimatorSet()
        set.playTogether(shakeX, shakeY)
        set.start()
    }
}
