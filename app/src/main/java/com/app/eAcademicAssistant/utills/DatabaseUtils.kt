package com.app.eAcademicAssistant.utills

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.eAcademicAssistant.model.UserModel


class DatabaseUtils(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "db1_data"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME_USER = "tb1_user"

        private var database: DatabaseUtils? = null
        fun getInstance(context: Context): DatabaseUtils {
            if (database == null)
                database = DatabaseUtils(context)
            return database as DatabaseUtils
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME_USER (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "_name VARCHAR(500)," +
                    "_email VARCHAR(2000)," +
                    "_password VARCHAR(2000)," +
                    "_gender INT(100)," +
                    "_uid VARCHAR(2000)," +
                    "_user_type INT(100)," +
                    "_user_id VARCHAR(2000)" +
                    ")"
        )

    }



    fun setUser(
        userType: Int,
        name: String,
        email: String,
        password: String,
        gender: Int,
        UID: String,
        userId: String
    ) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("_name", name)
        values.put("_email", email)
        values.put("_password", password)
        values.put("_uid", UID)
        values.put("_gender", gender)
        values.put("_user_type", userType)
        values.put("_user_id", userId)

        db.delete(TABLE_NAME_USER, null, null)
        db.insert(TABLE_NAME_USER, null, values)
    }

    fun updateUserType(
        id: String,
        userRollId: String
    ) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("_user_type", userRollId)
        db.update(TABLE_NAME_USER, values, "_user_id=?", arrayOf(id))
    }

    fun updateUserName(
        id: String,
        name: String
    ) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("_name", name)
        db.update(TABLE_NAME_USER, values, "_user_id=?", arrayOf(id))
    }



    fun getUser(): UserModel? {
        val db = readableDatabase
        val c = db.rawQuery("SELECT * FROM $TABLE_NAME_USER", null)
        var user: UserModel? = null
        while (c.moveToNext()) {
            user = UserModel(
                c.getString(c.getColumnIndex("_id")),
                c.getString(c.getColumnIndex("_name")),
                c.getString(c.getColumnIndex("_email")),
                c.getString(c.getColumnIndex("_password")),
                c.getString(c.getColumnIndex("_uid")),
                c.getInt(c.getColumnIndex("_gender")),
                c.getInt(c.getColumnIndex("_user_type")),
                c.getString(c.getColumnIndex("_user_id"))
            )
        }
        c.close()
        return user
    }

    fun clearUserTable() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME_USER")
    }



    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        dropTables(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
        dropTables(db)
    }

    private fun dropTables(db: SQLiteDatabase?) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_USER")
    }
}