package db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import model.Student

class StudentDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "students.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                record_book TEXT,
                name TEXT,
                course TEXT,
                faculty TEXT
            )
            """.trimIndent()
        )

        // ТЕСТОВЫЕ ДАННЫЕ
        insertStudent(db, "123456", "Иван Иванов", "2", "ФИИТ")
        insertStudent(db, "234567", "Мария Петрова", "1", "ИИТ")
        insertStudent(db, "345678", "Сергей Сидоров", "3", "ФПМИ")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    private fun insertStudent(
        db: SQLiteDatabase,
        recordBook: String,
        name: String,
        course: String,
        faculty: String
    ) {
        val cv = ContentValues()
        cv.put("record_book", recordBook)
        cv.put("name", name)
        cv.put("course", course)
        cv.put("faculty", faculty)
        db.insert("students", null, cv)
    }

    fun getAllStudents(): List<Student> {
        val list = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM students", null)

        while (cursor.moveToNext()) {
            list.add(
                Student(
                    cursor.getString(cursor.getColumnIndexOrThrow("record_book")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("course")),
                    cursor.getString(cursor.getColumnIndexOrThrow("faculty"))
                )
            )
        }

        cursor.close()
        return list
    }
}
