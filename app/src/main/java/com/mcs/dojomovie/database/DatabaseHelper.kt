package com.mcs.dojomovie.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.mcs.dojomovie.model.Film
import com.mcs.dojomovie.model.Transaction
import com.mcs.dojomovie.model.User

class DatabaseHelper(var context: Context): SQLiteOpenHelper(context, "DoJo_Movie_DB", null, 2) {
    companion object{
        // users
        private const val TABLE_USERS = "Users"
        private const val COL_USER_ID = "UserId"
        private const val COL_USER_PHONE = "Phone"
        private const val COL_USER_PASSWORD = "Password"

        // films
        private const val TABLE_FILMS = "Films"
        private const val COL_FILM_ID = "FilmId"
        private const val COL_FILM_TITLE = "FilmTitle"
        private const val COL_FILM_IMAGE = "FilmImage"
        private const val COL_FILM_PRICE = "FilmPrice"

        // transaction
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val COL_TRANSACTION_ID = "id"
        private const val COL_TRANSACTION_USER_ID = "UserId"
        private const val COL_TRANSACTION_FILM_ID = "FilmId"
        private const val COL_TRANSACTION_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // tabel users
        val createUserTableSQL = "CREATE TABLE $TABLE_USERS (" +
                "$COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_USER_PHONE VARCHAR(30) UNIQUE NOT NULL, " +
                "$COL_USER_PASSWORD VARCHAR(256) NOT NULL)"
        db?.execSQL(createUserTableSQL)

        // tabel films
        val createFilmTableSQL = "CREATE TABLE $TABLE_FILMS (" +
                "$COL_FILM_ID TEXT PRIMARY KEY, " +
                "$COL_FILM_TITLE VARCHAR(255) NOT NULL, " +
                "$COL_FILM_IMAGE TEXT, " +
                "$COL_FILM_PRICE INTEGER NOT NULL)"
        db?.execSQL(createFilmTableSQL)

        // tabel transactions
        val createTransactionTableSQL = "CREATE TABLE $TABLE_TRANSACTIONS (" +
                "$COL_TRANSACTION_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COL_TRANSACTION_USER_ID INTEGER," +
                "$COL_TRANSACTION_FILM_ID TEXT NOT NULL, " +
                "$COL_TRANSACTION_QUANTITY INTEGER NOT NULL, " +
                "FOREIGN KEY($COL_TRANSACTION_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID), " +
                "FOREIGN KEY($COL_TRANSACTION_FILM_ID) REFERENCES $TABLE_FILMS($COL_FILM_ID))"
        db?.execSQL(createTransactionTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FILMS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        onCreate(db)
    }

    fun insertUser(user: User) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_USER_PHONE, user.phone)
        cv.put(COL_USER_PASSWORD, user.password)
        val result = db.insert(TABLE_USERS, null, cv)

        if (result == -1.toLong()) {
            Toast.makeText(context, "Registration failed.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getUser() : MutableList<User>{
        val list : MutableList<User> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do{
                val user = User()
                user.id = result.getString(0).toInt()
                user.phone = result.getString(1)
                user.password = result.getString(2)
                list.add(user)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    fun insertFilm(film: Film): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(COL_FILM_ID, film.id)
            put(COL_FILM_TITLE, film.title)
            put(COL_FILM_IMAGE, film.image)
            put(COL_FILM_PRICE, film.price)
        }
        val result = db.insert(TABLE_FILMS, null, cv)

        return result != -1L
    }

    fun getFilm(): MutableList<Film> {
        val filmList: MutableList<Film> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FILMS ORDER BY $COL_FILM_TITLE ASC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val film = Film(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_TITLE)),
                    image = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_IMAGE)),
                    price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FILM_PRICE))
                )
                filmList.add(film)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return filmList
    }

    fun getFilmById(filmId: String): Film? {
        val db = this.readableDatabase
        var film: Film? = null
        val query = "SELECT * FROM $TABLE_FILMS WHERE $COL_FILM_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(filmId))

        if (cursor.moveToFirst()) {
            film = Film(
                id = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_TITLE)),
                image = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_IMAGE)),
                price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FILM_PRICE))
            )
        }

        cursor.close()
        return film
    }

    fun getFilmByTitle(title: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_FILMS WHERE $COL_FILM_TITLE = ?", arrayOf(title))
        val title = cursor.count > 0

        cursor.close()
        return title
    }

    fun insertTransaction(userId: Int, filmId: String, quantity: Int): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(COL_USER_ID, userId)
            put(COL_FILM_ID, filmId)
            put(COL_TRANSACTION_QUANTITY, quantity)
        }
        val result = db.insert(TABLE_TRANSACTIONS, null, cv)

        return result != -1L
    }

    fun getTransaction(userId: Int): MutableList<Transaction> {
        val transactionList: MutableList<Transaction> = ArrayList()
        val db = this.readableDatabase

        if (userId == -1) {
            return transactionList
        }

        val query = "SELECT " +
                "t.$COL_TRANSACTION_ID, " +
                "t.$COL_USER_ID, " +
                "t.$COL_FILM_ID AS transaction_table_film_id, " +
                "t.$COL_TRANSACTION_QUANTITY, " +
                "f.$COL_FILM_TITLE, " +
                "f.$COL_FILM_IMAGE, " +
                "f.$COL_FILM_PRICE " +
                "FROM $TABLE_TRANSACTIONS t " +
                "INNER JOIN $TABLE_FILMS f ON t.$COL_FILM_ID = f.$COL_FILM_ID " +
                "WHERE t.$COL_USER_ID = ? "
                "ORDER BY t.$COL_TRANSACTION_ID DESC"

        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val transaction = Transaction(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_ID)),
                    user_id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    film_id = cursor.getString(cursor.getColumnIndexOrThrow("transaction_table_film_id")),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TRANSACTION_QUANTITY)),
                    film_title = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_TITLE)),
                    film_image = cursor.getString(cursor.getColumnIndexOrThrow(COL_FILM_IMAGE)),
                    film_price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FILM_PRICE))
                )
                transactionList.add(transaction)

            } while (cursor.moveToNext())
        }
        return transactionList
    }
}