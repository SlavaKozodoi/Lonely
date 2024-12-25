package com.example.timely1.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.timely1.models.Entry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataBase(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "task_manager"
        private const val DB_VERSION = 4

        private const val DB_TABLE = "entry"
        private const val DB_COLUMN_NAME = "client_name"
        private const val DB_COLUMN_SECONDNAME = "client_second_name"
        private const val DB_COLUMN_THIRDNAME = "client_third_name"
        private const val DB_COLUMN_NUMBER = "client_number"
        private const val DB_COLUMN_DATE = "client_date"
        private const val DB_COLUMN_TIME = "client_time"
        private const val DB_COLUMN_PRICE = "client_price"
        private const val DB_COLUMN_ADDITIONAL = "client_additional"
        private const val DB_COLUMN_ISDone = "client_isDone"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = """
            CREATE TABLE $DB_TABLE (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $DB_COLUMN_NAME TEXT NOT NULL,
                $DB_COLUMN_SECONDNAME TEXT NOT NULL,
                $DB_COLUMN_THIRDNAME TEXT NOT NULL,
                $DB_COLUMN_NUMBER TEXT NOT NULL,
                $DB_COLUMN_DATE TEXT NOT NULL,
                $DB_COLUMN_TIME TEXT NOT NULL,
                $DB_COLUMN_PRICE REAL NOT NULL,
                $DB_COLUMN_ADDITIONAL TEXT,
                $DB_COLUMN_ISDone TEXT
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
        number: String,
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
            put(DB_COLUMN_ISDone,"false")
        }
        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun deleteData(id: Int) {
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
            entry[DB_COLUMN_NUMBER] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_NUMBER))
            entry[DB_COLUMN_DATE] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_DATE))
            entry[DB_COLUMN_TIME] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_TIME))
            entry[DB_COLUMN_PRICE] = cursor.getDouble(cursor.getColumnIndexOrThrow(DB_COLUMN_PRICE))
            entry[DB_COLUMN_ADDITIONAL] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ADDITIONAL))
            entry[DB_COLUMN_ISDone] = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ISDone))
            allEntries.add(entry)
        }
        cursor.close()
        db.close()
        return allEntries
    }


    fun getAllEntriesForMes(): List<Entry> {
        val allEntries = mutableListOf<Entry>()
        val db = readableDatabase
        val cursor: Cursor = db.query(DB_TABLE, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val entry = Entry(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_NAME)),
                secondName = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_SECONDNAME)),
                thirdName = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_THIRDNAME)),
                number = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_NUMBER)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_DATE)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_TIME)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(DB_COLUMN_PRICE)),
                additional = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ADDITIONAL)),
                isDone = (cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ISDone)) == "true").toString()
            )
            allEntries.add(entry)
        }
        cursor.close()
        db.close()
        return allEntries
    }

    fun updateData(
        id: Long,
        name: String,
        secondName: String,
        thirdName: String,
        number: String,
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

        db.update(DB_TABLE, values, "ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getEntryById(id: Long): Entry? {
        val db = readableDatabase
        val cursor = db.query(
            DB_TABLE,
            null,
            "ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val entry = Entry(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("ID")),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_NAME)),
                secondName = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_SECONDNAME)),
                thirdName = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_THIRDNAME)),
                number = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_NUMBER)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_DATE)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_TIME)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(DB_COLUMN_PRICE)),
                additional = cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ADDITIONAL)),
                isDone = (cursor.getString(cursor.getColumnIndexOrThrow(DB_COLUMN_ISDone)) == "true").toString()
            )
            cursor.close()
            db.close()
            return entry
        }
        cursor?.close()
        db.close()
        return null
    }

    fun updateIsDone(entryId: Long, isDone: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(DB_COLUMN_ISDone, if (isDone) "true" else "false")
        }
        db.update(DB_TABLE, values, "ID = ?", arrayOf(entryId.toString()))
        db.close()
    }

    fun deleteOldEntries() {
        val db = this.writableDatabase

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -3)
        calendar.add(Calendar.DAY_OF_MONTH, -5)
        val cutoffDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)

        db.delete(DB_TABLE, "$DB_COLUMN_DATE < ?", arrayOf(cutoffDate))
        db.close()
    }
}

