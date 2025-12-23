package com.example.student_game

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class DatabaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database)

        val studentListLayout = findViewById<LinearLayout>(R.id.studentListLayout)
        val dbHelper = DatabaseHelper(this)

        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "student_cards",
            arrayOf("id", "name", "birthdate", "gender", "course", "can_enter"),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val birthdate = cursor.getString(cursor.getColumnIndexOrThrow("birthdate"))
            val gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"))
            val course = cursor.getString(cursor.getColumnIndexOrThrow("course"))
            val canEnter = cursor.getString(cursor.getColumnIndexOrThrow("can_enter"))

            // Контейнер для каждой карточки студента
            val studentContainer = LinearLayout(this)
            studentContainer.orientation = LinearLayout.VERTICAL
            studentContainer.setPadding(0, 16, 0, 16)

            // Информация о студенте
            val textView = TextView(this)
            textView.text = "Имя: $name | Дата: $birthdate | Пол: $gender | Курс: $course"
            textView.setTextColor(Color.WHITE)
            textView.textSize = 18f

            // Кнопки "Впустить" и "Не впустить"
            val buttonLayout = LinearLayout(this)
            buttonLayout.orientation = LinearLayout.HORIZONTAL

            val allowButton = Button(this)
            allowButton.text = "Впустить"
            allowButton.setOnClickListener {
                checkDecision(canEnter, true)
            }

            val denyButton = Button(this)
            denyButton.text = "Не впустить"
            denyButton.setOnClickListener {
                checkDecision(canEnter, false)
            }

            buttonLayout.addView(allowButton)
            buttonLayout.addView(denyButton)

            // Добавляем всё в контейнер студента
            studentContainer.addView(textView)
            studentContainer.addView(buttonLayout)

            studentListLayout.addView(studentContainer)
        }

        cursor.close()
        db.close()
    }

    private fun checkDecision(canEnter: String, playerDecision: Boolean) {
        val correctDecision = canEnter.lowercase() == "да"
        if (playerDecision == correctDecision) {
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Неправильно!", Toast.LENGTH_SHORT).show()
        }
    }
}
