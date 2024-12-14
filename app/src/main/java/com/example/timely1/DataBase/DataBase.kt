package com.example.timely1.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBase(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "task_manager"
        private const val DB_VERSION = 2

        private const val DB_TABLE = "entry"
        private const val DB_COLUMN_NAME = "client_name"
        private const val DB_COLUMN_SECONDNAME = "client_second_name"
        private const val DB_COLUMN_THIRDNAME = "client_third_name"
        private const val DB_COLUMN_NUMBER = "client_number"
        private const val DB_COLUMN_DATE = "client_date"
        private const val DB_COLUMN_TIME = "client_time"
        private const val DB_COLUMN_PRICE = "client_price"
        private const val DB_COLUMN_ADDITIONAL = "client_additional"


    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE $DB_TABLE (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $DB_COLUMN_NAME TEXT NOT NULL,
                $DB_COLUMN_SECONDNAME TEXT NOT NULL,
                $DB_COLUMN_THIRDNAME TEXT NOT NULL,
                $DB_COLUMN_NUMBER LONG NOT NULL,
                $DB_COLUMN_DATE TEXT NOT NULL,
                $DB_COLUMN_TIME TEXT NOT NULL,
                $DB_COLUMN_PRICE REAL NOT NULL,
                $DB_COLUMN_ADDITIONAL TEXT
            );
        """.trimIndent()
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val query = "DROP TABLE IF EXISTS $DB_TABLE"
        db.execSQL(query)
        onCreate(db)
    }



    fun insertData(
        name: String,
        secondName: String,
        thirdName: String,
        number: Long,
        date: String,
        time: String,
        price: Double,
        additional: String?
    ) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_COLUMN_NAME, name)
            put(DB_COLUMN_SECONDNAME, secondName)
            put(DB_COLUMN_THIRDNAME, thirdName)
            put(DB_COLUMN_NUMBER, number)
            put(DB_COLUMN_DATE, date)
            put(DB_COLUMN_TIME, time)
            put(DB_COLUMN_PRICE, price)
            put(DB_COLUMN_ADDITIONAL, additional)
        }
        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun deleteData(id: Long) {
        val db = writableDatabase
        db.delete(DB_TABLE, "ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllEntries(): List<Map<String, Any>> {
        val allEntries = mutableListOf<Map<String, Any>>()
        val db = readableDatabase
        val cursor: Cursor = db.query(DB_TABLE, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val entry = mutableMapOf<String, Any>()
            entry["ID"] = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
            entry[DB_COLUMN_NAME] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_NAME))
            entry[DB_COLUMN_SECONDNAME] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_SECONDNAME))
            entry[DB_COLUMN_THIRDNAME] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_THIRDNAME))
            entry[DB_COLUMN_NUMBER] = cursor.getLong(cursor.getColumnIndexOrThrow(DB_COLUMN_NUMBER))
            entry[DB_COLUMN_DATE] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_DATE))
            entry[DB_COLUMN_TIME] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_TIME))
            entry[DB_COLUMN_PRICE] = cursor.getDouble(cursor.getColumnIndexOrThrow(DB_COLUMN_PRICE))
            entry[DB_COLUMN_ADDITIONAL] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ADDITIONAL))

            allEntries.add(entry)
        }
        cursor.close()
        db.close()
        return allEntries
    }
}
