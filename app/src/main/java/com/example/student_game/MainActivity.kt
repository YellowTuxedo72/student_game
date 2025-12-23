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

        val bgView = findViewById<ImageView>(R.id.backgroundView)
        val animation = bgView.background as AnimationDrawable
        val newGameButton = findViewById<Button>(R.id.play_newButton)
        newGameButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
        animation.start()

        startCameraShake(bgView)

        val dbHelper = DatabaseHelper(this)

        // --- Добавляем тестовые данные только если таблица пустая ---
        val db = dbHelper.readableDatabase
        val cursor = db.query("student_cards", arrayOf("id"), null, null, null, null, null)
        if (!cursor.moveToFirst()) {
            // Таблица пустая, добавляем студентов
            dbHelper.addStudentCard("Иван Иванов", "01.01.2005", "М", "1", "да")
            dbHelper.addStudentCard("Мария Петрова", "12.03.2004", "Ж", "2", "да")
            dbHelper.addStudentCard("Сергей Выдуманный", "05.05.2006", "М", "1", "нет")
            dbHelper.addStudentCard("Анна Фиктивная", "22.07.2005", "Ж", "3", "нет")
            dbHelper.addStudentCard("Петр Нереальный", "15.02.2004", "М", "2", "нет")
        }
        cursor.close()
        db.close()

        // --- Кнопка для перехода к проверке базы ---
        val checkDbButton = findViewById<Button>(R.id.checkDatabaseButton)
        checkDbButton.setOnClickListener {
            val intent = Intent(this, DatabaseActivity::class.java)
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