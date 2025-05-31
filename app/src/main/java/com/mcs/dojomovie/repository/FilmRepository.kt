package com.mcs.dojomovie.repository

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.mcs.dojomovie.database.DatabaseHelper
import com.mcs.dojomovie.model.Film
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException

class FilmRepository(private val context: Context) {
    private val dbHelper: DatabaseHelper
    private val requestQueue: RequestQueue
    private val tag = "FilmRepository"
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        dbHelper = DatabaseHelper(context.applicationContext)
        requestQueue = Volley.newRequestQueue(context.applicationContext)
    }

    suspend fun getFilmsFromDb(): List<Film> {
        return withContext(Dispatchers.IO) {
            dbHelper.getFilm()
        }
    }

    fun fetchFilmsFromNetwork(onOperationComplete: (isSuccess: Boolean, message: String?) -> Unit) {
        val url = "https://api.npoint.io/66cce8acb8f366d2a508"
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                saveDataToDatabase(response, onOperationComplete)
            },
            {
                onOperationComplete(false, null)
            }
        )
        requestQueue.add(jsonArrayRequest)
    }

    private fun saveDataToDatabase(responseArray: JSONArray, onOperationComplete: (isSuccess: Boolean, message: String?) -> Unit) {
        repositoryScope.launch {
            var allSuccessful = true

            try {
                if (responseArray.length() == 0) {
                    withContext(Dispatchers.Main) {
                        onOperationComplete(true, null)
                    }
                    return@launch
                }

                for (i in 0 until responseArray.length()) {
                    val filmJson = responseArray.getJSONObject(i)

                    val id = filmJson.getString("id")
                    val title = filmJson.getString("title")
                    val image = filmJson.optString("image", "")
                    val price = filmJson.getInt("price")

                    val film = Film(
                        id = id,
                        image = image,
                        price = price,
                        title = title
                    )

                    if (i == 0) {
                        film.image = "kongzilla"
                    } else if (i == 1) {
                        film.image = "final_fantalion"
                    } else if (i == 2) {
                        film.image = "bond_jampshoot"
                    }

                    if (!dbHelper.getFilmByTitle(film.title)) {
                        if (!dbHelper.insertFilm(film)) {
                            allSuccessful = false
                        }
                    }
                }

            } catch (e: JSONException) {
                allSuccessful = false
            } catch (e: Exception) {
                allSuccessful = false
            } finally {
                withContext(Dispatchers.Main) {
                    onOperationComplete(allSuccessful, null)
                }
            }
        }
    }
}