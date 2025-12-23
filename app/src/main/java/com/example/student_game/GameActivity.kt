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
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import db.StudentDatabaseHelper
import model.Student
import ui.StudentAdapter



class GameActivity : ComponentActivity() {
    private lateinit var timerContainer: View
    private lateinit var timerBar: View

    private lateinit var cardContainer: View
    private lateinit var tvId: TextView
    private lateinit var tvName: TextView
    private lateinit var tvCourse: TextView
    private lateinit var tvFaculty: TextView


    private lateinit var studentListContainer: View
    private lateinit var studentCard: View
    private lateinit var character: ImageView
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button
    private lateinit var students: List<Student>


    private val characterImages = listOf(R.drawable.character_1, R.drawable.character_2)
    private var currentCharacterIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val recycler = findViewById<RecyclerView>(R.id.studentRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        cardContainer = findViewById(R.id.studentCardContainer)
        tvId = findViewById(R.id.textStudentId)
        tvName = findViewById(R.id.textStudentName)
        tvCourse = findViewById(R.id.textCourse)
        tvFaculty = findViewById(R.id.textFaculty)

        cardContainer.visibility = View.INVISIBLE


        val db = StudentDatabaseHelper(this)
        students = db.getAllStudents()

        recycler.adapter = StudentAdapter(students)


        character = findViewById(R.id.characterImage)
        buttonLeft = findViewById(R.id.button1)
        buttonRight = findViewById(R.id.button2)

        studentCard = findViewById(R.id.studentCardContainer)
        timerContainer = findViewById(R.id.studentTimerContainer)
        timerBar = findViewById(R.id.timerBar)
        timerContainer.visibility = View.INVISIBLE

        studentListContainer = findViewById(R.id.studentListContainer)

        studentCard.visibility = View.INVISIBLE
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
                // Кнопки невидимы и неактивны пока не появился листочек
                buttonLeft.visibility = View.INVISIBLE
                buttonRight.visibility = View.INVISIBLE
                buttonLeft.isEnabled = false
                buttonRight.isEnabled = false

                // Меняем foreground на "выключенный" офис
                findViewById<ImageView>(R.id.foregroundImage)
                    .setImageResource(R.drawable.office_off_button)

                // Показываем карточку студента и запускаем таймер
                studentCard.alpha = 0f
                studentCard.visibility = View.VISIBLE
                studentCard.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .withEndAction {
                        showRandomStudentCard()
                        startStudentTimer(5000L) // таймер на 5 секунд
                    }
                    .start()
            }
        })
    }



    private fun setupButtonActions() {
        val parentWidth = (character.parent as View).width

        val exitLeft = {
            resetButtons()
            animateCharacterExit(-parentWidth.toFloat())
        }
        val exitRight = {
            resetButtons()
            animateCharacterExit(parentWidth.toFloat())
        }

        buttonLeft.setOnClickListener { exitLeft() }
        buttonRight.setOnClickListener { exitRight() }
    }

    private fun resetButtons() {
        buttonLeft.visibility = View.INVISIBLE
        buttonRight.visibility = View.INVISIBLE
        buttonLeft.isEnabled = false
        buttonRight.isEnabled = false
    }

    private fun animateCharacterExit(targetX: Float) {
        studentCard.visibility = View.INVISIBLE
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

    private fun showRandomStudentCard() {
        if (students.isEmpty()) return

        val original = students.random()
        val modified = maybeCorruptStudent(original)

        tvId.text = "№ ${modified.recordBook}"
        tvName.text = modified.name
        tvCourse.text = "Курс: ${modified.course}"
        tvFaculty.text = "Факультет: ${modified.faculty}"
    }
    private fun maybeCorruptStudent(student: Student): Student {
        val chance = (20..30).random()
        val roll = (1..100).random()

        if (roll > chance) return student

        return when ((1..4).random()) {
            1 -> student.copy(recordBook = corruptRecordBook(student.recordBook))
            2 -> student.copy(name = corruptName(student.name))
            3 -> student.copy(course = (1..4).random().toString())
            else -> student.copy(faculty = randomFaculty())
        }
    }

    private fun corruptRecordBook(book: String): String {
        if (book.isEmpty()) return book

        val index = book.indices.random()
        val newDigit = ('0'..'9').random()

        return book.replaceRange(index, index + 1, newDigit.toString())
    }

    private fun corruptName(name: String): String {
        val vowels = listOf('а','е','ё','и','о','у','ы','э','ю','я')
        val chars = name.toCharArray()

        val indices = chars.indices.filter {
            chars[it].lowercaseChar() in vowels
        }

        if (indices.isEmpty()) return name

        val i = indices.random()
        chars[i] = vowels.random()

        return String(chars)
    }

    private fun randomFaculty(): String {
        return listOf("ФИИТ", "ФПМИ", "ИИТ", "ФКН", "ИБ").random()
    }
    private fun startStudentTimer(duration: Long = 5000L) {
        timerContainer.visibility = View.VISIBLE

        val parentWidth = (timerContainer.parent as View).width

        val animator = android.animation.ValueAnimator.ofInt(0, parentWidth)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            timerBar.layoutParams.width = value
            timerBar.requestLayout()
        }

        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Скрываем карточку и таймер
                studentCard.visibility = View.INVISIBLE
                timerContainer.visibility = View.INVISIBLE

                // Показ списка студентов
                studentListContainer.alpha = 0f
                studentListContainer.visibility = View.VISIBLE
                studentListContainer.animate().alpha(1f).setDuration(300).start()

                // После появления листочка — кнопки видимые и активные
                buttonLeft.visibility = View.VISIBLE
                buttonRight.visibility = View.VISIBLE
                buttonLeft.isEnabled = true
                buttonRight.isEnabled = true

                // Настраиваем действия кнопок
                setupButtonActions()

                // Возвращаем фон офиса
                findViewById<ImageView>(R.id.foregroundImage)
                    .setImageResource(R.drawable.office_2)
            }
        })

        animator.start()
    }




}