package com.example.adventurefinder

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "users.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_REGISTRATION_DATE = "registration_date" // Новое поле
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT UNIQUE NOT NULL," +
                "$COLUMN_PASSWORD TEXT NOT NULL," +
                "$COLUMN_REGISTRATION_DATE TEXT)" // Добавлено новое поле
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(user: User): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_PASSWORD, user.password)
        values.put(COLUMN_REGISTRATION_DATE, user.registrationDate) // Сохраняем дату
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    // ... (getUser - добавь registrationDate в запрос)
    fun getUser(username: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_REGISTRATION_DATE), // Добавлено поле даты
            "$COLUMN_USERNAME=?",
            arrayOf(username),
            null, null, null
        )
        val user = if (cursor.moveToFirst()) {
            User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3) // Получаем дату регистрации
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return user
    }
}

data class User(
    val id: Int = 0,
    val username: String,
    val password: String,
    val registrationDate: String? = null // Добавлено поле даты регистрации
)