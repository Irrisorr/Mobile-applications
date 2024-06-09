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
        const val COLUMN_REGISTRATION_DATE = "registration_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTableQuery = "CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT UNIQUE NOT NULL," +
                "$COLUMN_PASSWORD TEXT NOT NULL," +
                "$COLUMN_REGISTRATION_DATE TEXT)"
        db.execSQL(createUsersTableQuery)

        val createWishlistTableQuery = "CREATE TABLE wishlist (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL," +
                "activity_name TEXT NOT NULL," +
                "FOREIGN KEY (username) REFERENCES $TABLE_USERS ($COLUMN_USERNAME))"
        db.execSQL(createWishlistTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS wishlist")
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
                cursor.getString(3)
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return user
    }

    fun getUserWishlist(username: String): List<AdventureActivity> {
        val db = readableDatabase
        val query = "SELECT * FROM wishlist WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))
        val wishlist = mutableListOf<AdventureActivity>()

        while (cursor.moveToNext()) {
            val activityName = cursor.getString(cursor.getColumnIndex("activity_name"))
            val activity = getActivitiesList().find { it.name == activityName }
            activity?.let { wishlist.add(it) }
        }

        cursor.close()
        return wishlist
    }

    fun addToWishlist(username: String, activityName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("activity_name", activityName)
        }
        db.insert("wishlist", null, values)
    }

    fun removeFromWishlist(username: String, activityName: String) {
        val db = writableDatabase
        db.delete("wishlist", "username = ? AND activity_name = ?", arrayOf(username, activityName))
    }
}

data class User(
    val id: Int = 0,
    val username: String,
    val password: String,
    val registrationDate: String? = null
)