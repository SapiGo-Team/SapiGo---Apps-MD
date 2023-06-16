package com.bangkit.sapigo.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.bangkit.sapigo.data.ResultData

class MyDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "my_database.db"
        private const val DATABASE_VERSION = 2

        // Define the table and column names
        const val TABLE_NAME = "result_table"
        const val COLUMN_ID = "id" // Add the ID column
        const val COLUMN_IMAGE_URI = "image_uri"
        const val COLUMN_RESPONSES_1 = "responses1"
        const val COLUMN_RESPONSES_2 = "responses2"
        const val COLUMN_RESPONSES_3 = "responses3"
        const val COLUMN_RESPONSES_4 = "responses4"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create the table
        val createTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_IMAGE_URI TEXT, " +
                "$COLUMN_RESPONSES_1 TEXT, " +
                "$COLUMN_RESPONSES_2 TEXT, " +
                "$COLUMN_RESPONSES_3 TEXT, " +
                "$COLUMN_RESPONSES_4 TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertData(imageUri: Uri, responses: ArrayList<String>): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_IMAGE_URI, imageUri.toString())
            put(COLUMN_RESPONSES_1, responses[0])
            put(COLUMN_RESPONSES_2, responses[1])
            put(COLUMN_RESPONSES_3, responses[2])
            put(COLUMN_RESPONSES_4, responses[3])
        }
        val id = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return id
    }

    // Add a method to retrieve all data with ID
    fun getAllData(): ArrayList<ResultData> {
        val db = readableDatabase
        val projection = arrayOf(
            COLUMN_ID,
            COLUMN_IMAGE_URI,
            COLUMN_RESPONSES_1,
            COLUMN_RESPONSES_2,
            COLUMN_RESPONSES_3,
            COLUMN_RESPONSES_4
        )
        val cursor = db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val resultList = ArrayList<ResultData>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URI))
            val response1 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSES_1))
            val response2 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSES_2))
            val response3 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSES_3))
            val response4 = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSES_4))

            val imageUri = Uri.parse(imageUriString)
            val responses = listOf(response1, response2, response3, response4)
            resultList.add(ResultData(id, imageUri, responses))
        }

        cursor.close()
        db.close()

        return resultList
    }
}
