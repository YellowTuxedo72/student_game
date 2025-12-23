package com.example.student_game

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "student_cards.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "student_cards"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_BIRTHDATE = "birthdate"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_COURSE = "course"
        private const val COLUMN_CAN_ENTER = "can_enter"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_BIRTHDATE TEXT NOT NULL,
                $COLUMN_GENDER TEXT NOT NULL,
                $COLUMN_COURSE TEXT NOT NULL,
                $COLUMN_CAN_ENTER TEXT NOT NULL
            );
        """.trimIndent()

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addStudentCard(name: String, birthdate: String, gender: String, course: String, canEnter: String): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_BIRTHDATE, birthdate)
        values.put(COLUMN_GENDER, gender)
        values.put(COLUMN_COURSE, course)
        values.put(COLUMN_CAN_ENTER, canEnter)

        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result != -1L
    }

    fun canStudentEnter(name: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_CAN_ENTER),
            "$COLUMN_NAME = ?",
            arrayOf(name),
            null,
            null,
            null
        )

        var result = false
        if (cursor.moveToFirst()) {
            val canEnter = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAN_ENTER))
            result = canEnter.lowercase() == "да"
        }

        cursor.close()
        db.close()
        return result
    }
}
