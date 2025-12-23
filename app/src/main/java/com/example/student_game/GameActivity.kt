package com.example.student_game

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.StudentDatabaseHelper
import ui.StudentAdapter



class GameActivity : ComponentActivity() {

    private lateinit var listButtonContainer: View

    private lateinit var openListButton: Button

    private lateinit var studentListContainer: View
    private lateinit var studentCard: View
    private lateinit var character: ImageView
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button

    private val characterImages = listOf(R.drawable.character_1, R.drawable.character_2)
    private var currentCharacterIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val recycler = findViewById<RecyclerView>(R.id.studentRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val db = StudentDatabaseHelper(this)
        val students = db.getAllStudents()

        recycler.adapter = StudentAdapter(students)


        character = findViewById(R.id.characterImage)
        buttonLeft = findViewById(R.id.button1)
        buttonRight = findViewById(R.id.button2)

        studentCard = findViewById(R.id.studentCardContainer)
        listButtonContainer = findViewById(R.id.studentListButtonContainer)
        openListButton = findViewById(R.id.openStudentListButton)
        studentListContainer = findViewById(R.id.studentListContainer)

        studentCard.visibility = View.INVISIBLE
        listButtonContainer.visibility = View.INVISIBLE
        studentListContainer.visibility = View.INVISIBLE


        buttonLeft.visibility = View.INVISIBLE
        buttonRight.visibility = View.INVISIBLE
        studentCard = findViewById(R.id.studentCardContainer)
        studentCard.visibility = View.INVISIBLE

        // Ждём layout и запускаем цикл
        character.post {
            showNextCharacter()
        }
    }

    private fun showNextCharacter() {
        // Устанавливаем текущий персонаж
        character.setImageResource(characterImages[currentCharacterIndex])
        character.visibility = View.VISIBLE

        startCharacterEntrance()
    }

    private fun startCharacterEntrance() {
        val parentWidth = (character.parent as View).width
        character.translationX = parentWidth.toFloat() // справа за экраном

        val enterAnim = ObjectAnimator.ofFloat(character, "translationX", parentWidth.toFloat(), 0f)
        enterAnim.duration = 1000
        enterAnim.interpolator = DecelerateInterpolator()
        enterAnim.start()

        enterAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                buttonLeft.visibility = View.VISIBLE
                buttonRight.visibility = View.VISIBLE
                studentCard.alpha = 0f
                studentCard.visibility = View.VISIBLE
                studentCard.animate().alpha(1f).setDuration(300).start()

                listButtonContainer.alpha = 0f
                listButtonContainer.visibility = View.VISIBLE
                listButtonContainer.animate().alpha(1f).setDuration(300).start()

                openListButton.setOnClickListener {
                    // скрываем карточку и кнопку
                    studentCard.visibility = View.INVISIBLE
                    listButtonContainer.visibility = View.INVISIBLE

                    // показываем листок со списком
                    studentListContainer.alpha = 0f
                    studentListContainer.visibility = View.VISIBLE
                    studentListContainer.animate().alpha(1f).setDuration(300).start()
                }


                // Показываем зачетку
                studentCard.alpha = 0f
                studentCard.visibility = View.VISIBLE
                studentCard.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()

                setupButtonActions()
            }

        })
    }

    private fun setupButtonActions() {
        val parentWidth = (character.parent as View).width

        val exitLeft = {
            animateCharacterExit(-parentWidth.toFloat())
        }
        val exitRight = {
            animateCharacterExit(parentWidth.toFloat())
        }

        buttonLeft.setOnClickListener {
            buttonLeft.visibility = View.INVISIBLE
            buttonRight.visibility = View.INVISIBLE
            exitLeft()
        }

        buttonRight.setOnClickListener {
            buttonLeft.visibility = View.INVISIBLE
            buttonRight.visibility = View.INVISIBLE
            exitRight()
        }
    }

    private fun animateCharacterExit(targetX: Float) {
        studentCard.visibility = View.INVISIBLE
        listButtonContainer.visibility = View.INVISIBLE
        studentListContainer.visibility = View.INVISIBLE

        studentCard.animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                studentCard.visibility = View.INVISIBLE
            }
            .start()

        val exitAnim = ObjectAnimator.ofFloat(character, "translationX", character.translationX, targetX)
        exitAnim.duration = 1000
        exitAnim.interpolator = DecelerateInterpolator()
        exitAnim.start()

        exitAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // После ухода персонажа через 1–2 секунды появляется следующий
                character.visibility = View.INVISIBLE
                currentCharacterIndex = (currentCharacterIndex + 1) % characterImages.size

                handler.postDelayed({ showNextCharacter() }, 1500) // пауза 1.5 сек
            }
        })
    }
}