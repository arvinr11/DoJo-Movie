package com.mcs.dojomovie.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.mcs.dojomovie.database.DatabaseHelper
import com.mcs.dojomovie.databinding.ActivityFilmPageBinding
import com.mcs.dojomovie.model.Film
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilmPage : AppCompatActivity() {

    private lateinit var binding: ActivityFilmPageBinding
    private lateinit var dbHelper: DatabaseHelper
    private var currentFilm: Film? = null
    private var filmPrice: Int = 0

    companion object {
        const val EXTRA_FILM_ID = "extra_film_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFilmPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        setupToolbar()

        val filmId = intent.getStringExtra(EXTRA_FILM_ID)
        if (filmId == null) {
            finish()
            return
        }

        loadFilmDetails(filmId)
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarFilmPage)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarFilmPage.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadFilmDetails(filmId: String) {
        currentFilm = dbHelper.getFilmById(filmId)

        currentFilm?.let { film ->
            binding.tvTitleDetail.text = film.title
            filmPrice = film.price
            binding.tvPriceDetail.text = "Harga: Rp ${film.price}"

            val imageResId = resources.getIdentifier(film.image, "drawable", packageName)

            if (imageResId != 0) {
                binding.ivPosterFilm.setImageResource(imageResId)
            }
            updateTotalPrice()
        }
    }

    private fun setupListeners() {
        binding.etQuantityDetail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateTotalPrice()
            }
        })

        binding.btnBuyFilmDetail.setOnClickListener {
            processTransaction()
        }
    }

    private fun updateTotalPrice() {
        val quantityString = binding.etQuantityDetail.text.toString()
        val quantity = if (quantityString.isNotEmpty()) {
            quantityString.toIntOrNull() ?: 0
        } else {
            0
        }
        val nonNegativeQuantity = if (quantity < 0) 0 else quantity

        val totalPrice = nonNegativeQuantity * filmPrice

        binding.tvTotalPriceDetail.text = "Total Price: Rp ${totalPrice}"
    }

    private fun processTransaction() {
        val quantityString = binding.etQuantityDetail.text.toString().trim()
        if (quantityString.isEmpty()) {
            Toast.makeText(this, "Quantity must be filled!", Toast.LENGTH_SHORT).show()
            return
        }

        val quantity = quantityString.toIntOrNull()
        if (quantity == null) {
            Toast.makeText(this, "Quantity must be a number!", Toast.LENGTH_SHORT).show()
            return
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Quantity must more than 0!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.etQuantityDetail.error = null

        val filmToBuy = currentFilm
        if (filmToBuy == null) {
            return
        }

        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPreferences.getInt("LOGGED_IN_USER_ID", -1)

        val filmIdToSave = filmToBuy.id
        val quantityToSave = quantity
        val userIdToSave = loggedInUserId

        lifecycleScope.launch {
            val success = withContext(Dispatchers.IO) {
                dbHelper.insertTransaction(userIdToSave, filmIdToSave, quantityToSave)
            }

            withContext(Dispatchers.Main) {
                if (success) {
                    val totalPriceForDisplay = quantityToSave * filmToBuy.price
                    Toast.makeText(this@FilmPage, "Your transaction was successful!", Toast.LENGTH_LONG).show()

                    binding.etQuantityDetail.text.clear()
                } else {
                    Toast.makeText(this@FilmPage, "Your transaction was unsuccessful.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}